package com.bankstatement.perfios.util;

import static java.lang.Integer.toHexString;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bankstatement.analysis.request.pojo.InitiateRequestPojo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PerfiosUploadHelper {

	private static final Logger logger = LoggerFactory.getLogger(PerfiosUploadHelper.class);

	@Value("${perfios.bsa.upload.callBackUrl}")
	private String callBackUrl;

	@Value("${perfios.server:demo.perfios.com}")
	private String perfiosHost;

	@Value("${perfios.bsa.upload.init.transaction.url:}")
	private String uploadInitTransactionUrl;

	@Value("${perfios.bsa.upload.institution.list.url:}")
	private String uploadInstitutionListUrl;

	@Value("${perfios.bsa.upload.file.url:}")
	private String uploadFileUrl;

	@Value("${perfios.bsa.upload.file.process.url:}")
	private String processFileUrl;

	@Value("${perfios.bsa.upload.retrieve.report.url:}")
	private String retrieveReportUrl;

	@Value("${perfios.bsa.upload.generate.report.url:}")
	private String generateReportUrl;

	@Value("${perfios.bsa.upload.transaction.status.url:}")
	private String txnStatusUrl;

	@Value("${perfios.bsa.upload.file.path:}")
	private String uploadFilePath;

	@Value("${perfios.bsa.upload.private-key-path}")
	protected String privateKeyPath;

	@Value("${perfios.bsa.upload.encryption.algorithm:}")
	private String encryptionAlgorithm;

	private ObjectMapper objectMapper = new ObjectMapper();

	protected PrivateKey uploadPrivateKey;

	protected PublicKey uploadPublicKey;

	@Value("${perfios.bsa.report.download.path:}")
	private String reportPath;

	@Value("${perfios.bsa.cloud.upload.file.path:}")
	private String bsaUploadCloudFilePath;

	@PostConstruct
	public void onLoad() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(privateKeyPath));
			String privateKeyPem = new String(encoded);
			PEMReader pemReader = new PEMReader(new StringReader(privateKeyPem));
			KeyPair keyPair = (KeyPair) pemReader.readObject();
			uploadPrivateKey = keyPair.getPrivate();
			uploadPublicKey = keyPair.getPublic();
		} catch (IOException e) {
			logger.error("Error loading key pair", e);
		}
	}

	public String SHA256(String payloadHash) throws NoSuchAlgorithmException {
		return DigestUtils.sha256Hex(payloadHash);
	}

	public String uriEncode(final CharSequence input) {
		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			final char ch = input.charAt(i);
			if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_'
					|| ch == '-' || ch == '~' || ch == '.' || ch == '/') {
				result.append(ch);
			} else {
				result.append(toHexString(ch));
			}
		}
		return result.toString();
	}

	public String createXPerfiosDate() {
		SimpleDateFormat date = new SimpleDateFormat("YYYYMMDD'T'HHMMSS'Z'");
		return date.format(new Date());
	}

	public String encrypt(String raw, PrivateKey k, PublicKey k2)
			throws InvalidKeyException, SignatureException, UnsupportedEncodingException, NoSuchAlgorithmException {
		Signature privateSignature = Signature.getInstance(encryptionAlgorithm);
		privateSignature.initSign(k);
		privateSignature.update(raw.getBytes("UTF-8"));
		byte[] signature = privateSignature.sign();
		byte[] encoded = Hex.encode(signature);
		String encryptStr = new String(encoded);
		return encryptStr;
	}

	public String createSignature(String method, String uri, String payload, String date, Map<String, String> param)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		StringBuilder StringToSign = new StringBuilder();
		logger.info("Initiate Transaction payload {} ", payload);
		String sha256Payload = SHA256(payload);
		String xPerfiosdate = date;
		String uriEncodedQuery = "";
		if (param != null && !StringUtils.isEmpty(param)) {
			uriEncodedQuery = createCanonicalParams(param);
		}
		sb.append(method).append("\n");
		sb.append(uriEncode(uri)).append("\n");
		sb.append(uriEncodedQuery).append("\n");
		sb.append("host:").append(getPerfiosHost()).append("\n");
		sb.append("x-perfios-content-sha256:").append(sha256Payload).append("\n");
		sb.append("x-perfios-date:").append(xPerfiosdate).append("\n");
		sb.append("host;x-perfios-content-sha256;x-perfios-date").append("\n");
		sb.append(sha256Payload);
		StringToSign.append("PERFIOS-RSA-SHA256").append("\n");
		StringToSign.append(xPerfiosdate).append("\n");
		StringToSign.append(SHA256(sb.toString()));
		return encrypt(SHA256(StringToSign.toString()), getUploadPrivateKey(), getUploadPublicKey());
	}

	private String createCanonicalParams(Map<String, String> params) {
		String canonicalParams = "";
		for (String key : params.keySet()) {
			if (canonicalParams == null || StringUtils.isEmpty(canonicalParams))
				canonicalParams = uriEncode(key) + "=" + uriEncode(params.get(key));
			else
				canonicalParams = "&" + uriEncode(key) + "=" + uriEncode(params.get(key));
		}
		return canonicalParams;
	}

	public String createPayload(String processId, InitiateRequestPojo inputs) throws JsonProcessingException {
		Map<String, Object> payloadMap = new HashMap<>();
		final HashMap<String, Object> payload = new HashMap<>();
		payloadMap.put("payload", payload);
		payload.put("txnId", processId);
		payload.put("processingType", "STATEMENT");
		payload.put("loanAmount", "2000");
		payload.put("loanDuration", "6");
		payload.put("loanType", "Home");
		payload.put("transactionCompleteCallbackUrl", callBackUrl + processId);
		payload.put("acceptancePolicy", "atLeastOneTransactionInRange");
		payload.put("facility", "NONE");
		payload.put("uploadingScannedStatements", inputs.isScannedDoc() ? "true" : "false");
		if (inputs.getYearMonthFrom() != null && !inputs.getYearMonthFrom().isEmpty()) {
			payload.put("yearMonthFrom", inputs.getYearMonthFrom());
		}
		if (inputs.getYearMonthTo() != null && !inputs.getYearMonthTo().isEmpty()) {
			payload.put("yearMonthTo", inputs.getYearMonthTo());
		}
		return objectMapper.writeValueAsString(payloadMap);
	}

	public String createPayload(String fileId, String institutionId) throws JsonProcessingException {
		Map<String, Object> payloadMap = new HashMap<>();
		final HashMap<String, Object> payload = new HashMap<>();
		payloadMap.put("payload", payload);
		payload.put("fileId", fileId);
		payload.put("institutionId", institutionId);
		return objectMapper.writeValueAsString(payloadMap);
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	public String getUploadInitTransactionUrl() {
		return uploadInitTransactionUrl;
	}

	public void setUploadInitTransactionUrl(String uploadInitTransactionUrl) {
		this.uploadInitTransactionUrl = uploadInitTransactionUrl;
	}

	public PrivateKey getUploadPrivateKey() {
		return uploadPrivateKey;
	}

	public PublicKey getUploadPublicKey() {
		return uploadPublicKey;
	}

	public String getPerfiosHost() {
		return perfiosHost;
	}

	public void setPerfiosHost(String perfiosHost) {
		this.perfiosHost = perfiosHost;
	}

	public String getUploadInstitutionListUrl() {
		return uploadInstitutionListUrl;
	}

	public void setUploadInstitutionListUrl(String uploadInstitutionListUrl) {
		this.uploadInstitutionListUrl = uploadInstitutionListUrl;
	}

	public String getUploadFileUrl() {
		return uploadFileUrl;
	}

	public void setUploadFileUrl(String uploadFileUrl) {
		this.uploadFileUrl = uploadFileUrl;
	}

	public String getUploadFilePath() {
		return uploadFilePath;
	}

	public void setUploadFilePath(String uploadFilePath) {
		this.uploadFilePath = uploadFilePath;
	}

	public String getProcessFileUrl() {
		return processFileUrl;
	}

	public void setProcessFileUrl(String processFileUrl) {
		this.processFileUrl = processFileUrl;
	}

	public String getRetrieveReportUrl() {
		return retrieveReportUrl;
	}

	public void setRetrieveReportUrl(String retrieveReportUrl) {
		this.retrieveReportUrl = retrieveReportUrl;
	}

	public String getTxnStatusUrl() {
		return txnStatusUrl;
	}

	public void setTxnStatusUrl(String txnStatusUrl) {
		this.txnStatusUrl = txnStatusUrl;
	}

	public String getGenerateReportUrl() {
		return generateReportUrl;
	}

	public void setGenerateReportUrl(String generateReportUrl) {
		this.generateReportUrl = generateReportUrl;
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public String getBsaUploadCloudFilePath() {
		return bsaUploadCloudFilePath;
	}

	public void setBsaUploadCloudFilePath(String bsaUploadCloudFilePath) {
		this.bsaUploadCloudFilePath = bsaUploadCloudFilePath;
	}

}
