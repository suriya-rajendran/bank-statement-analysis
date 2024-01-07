package com.bankstatement.perfios.async;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.bankstatement.analysis.base.datamodel.BankStatementBaseModel.STATUS;
import com.bankstatement.analysis.base.repo.BSInitiateRepository;
import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;
import com.bankstatement.analysis.base.datamodel.BankStatementReport;
import com.bankstatement.analysis.base.datamodel.BankStatementTransaction;
import com.bankstatement.analysis.base.service.BankStatementImpl;
import com.bankstatement.analysis.base.service.FeatureService;
import com.bankstatement.analysis.base.service.ProductService;
import com.bankstatement.analysis.perfios.response.pojo.Part;
import com.bankstatement.analysis.perfios.response.pojo.TransactionStatusResponse;
import com.bankstatement.analysis.request.pojo.InitiateRequestPojo;
import com.bankstatement.perfios.configuration.PerfiosConfiguration;
import com.bankstatement.perfios.util.PerfiosHelper;
import com.bankstatement.perfios.util.PerfiosUploadHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AsyncBankStatementService {

	private static final Logger logger = LoggerFactory.getLogger(AsyncBankStatementService.class);

	@Autowired
	FeatureService featureService;

	@Autowired
	BSInitiateRepository bsInitiateRepository;

	@Autowired
	BankStatementImpl bankStatementImpl;

	@Autowired
	PerfiosConfiguration perfiosConfiguration;

	@Autowired
	PerfiosHelper perfiosHelper;

	@Autowired
	ProductService productService;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	PerfiosUploadHelper perfiosUploadHelper;

	@Value("${proofdocuments.upload_file_path}")
	private String uploadFilePath;

	private static final String TRANSACTION = "transaction";
	private static final String PERFIOS_TRANSACTION_ID = "perfiosTransactionId";
	private static final String REPORT_TYPE_JSON = "json";

	@Async
	public InitiateRequestPojo generateUploadInitiateResponse(InitiateRequestPojo initiateRequestPojo,
			BankStatementInitiate bsinitiate, String actualPayload) throws Exception {
		String fileId = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			String xPerfiosDate = perfiosUploadHelper.createXPerfiosDate();
			String url = "https://" + perfiosUploadHelper.getPerfiosHost()
					+ perfiosUploadHelper.getUploadInitTransactionUrl();
			logger.info("BSA Upload Initiate Transaction Url {} ", url);
			String xPerfiosSignature = perfiosUploadHelper.createSignature(HttpMethod.POST.name(),
					perfiosUploadHelper.getUploadInitTransactionUrl(), actualPayload, xPerfiosDate, null);
			logger.info("Signature created successfully {} ", bsinitiate.getRequestId());
			HttpHeaders httpHeaders = getHttpHeaders(actualPayload, xPerfiosSignature, xPerfiosDate, "application/xml");
			logger.info("Http headers {} for request id {} ", httpHeaders, bsinitiate.getRequestId());
			HttpEntity<String> entity = new HttpEntity<String>(actualPayload, httpHeaders);
			logger.info("Initiating perfios upload transaction {} ", bsinitiate.getRequestId());
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			logger.info("Initiate transaction response {} ", response);
			if (response.getStatusCode().equals(HttpStatus.OK) && response != null && response.getBody() != null) {
				String resp = response.getBody();
				String perfiosTransactionId = null;

				bsinitiate = bankStatementImpl.getBankStatementInitiateByRequestIdAndCustomerWebNo(
						bsinitiate.getRequestId(), bsinitiate.getCustWebNo(), bsinitiate.getDocWebNo());

				bsinitiate.setResponse(resp);
				JSONObject xmlJSONObj = XML.toJSONObject(resp);
				final Map<String, Object> responseAsMap = objectMapper.readValue(xmlJSONObj.toString(),
						new TypeReference<Map<String, Object>>() {
						});
				if (responseAsMap != null && responseAsMap.containsKey(TRANSACTION)) {
					final Map<String, String> successResponse = (Map<String, String>) responseAsMap.get(TRANSACTION);
					perfiosTransactionId = successResponse.get(PERFIOS_TRANSACTION_ID);
					logger.info("perfios transaction id {} for request id {} ", perfiosTransactionId,
							bsinitiate.getRequestId());
				}
				bsinitiate.setStatus(STATUS.PENDING);
				bsinitiate.setTransactionId(perfiosTransactionId);

				bsInitiateRepository.save(bsinitiate);

				bsinitiate = bsInitiateRepository.findById(bsinitiate.getId()).get();

				String uploadFileUrl = perfiosUploadHelper.getUploadFileUrl().replace("{perfiosTransactionId}",
						perfiosTransactionId);
				url = "https://" + perfiosUploadHelper.getPerfiosHost() + uploadFileUrl;
				logger.info("Perfios Upload File url {} ", url);
				String signature = perfiosUploadHelper.createSignature(HttpMethod.POST.name(), uploadFileUrl, "",
						xPerfiosDate, null);

				logger.info("Perfios signature created successfully ");
				httpHeaders = getHttpHeaders("", signature, xPerfiosDate, "multipart/form-data");
				LinkedMultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
				multiValueMap.add("file", generateBody(initiateRequestPojo.getFileName()));
				HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(
						multiValueMap, httpHeaders);
				logger.info("Upload file request {} ", requestEntity);
				response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
				if (HttpStatus.OK.equals(response.getStatusCode()) && response != null && response.getBody() != null) {
					bsinitiate.setResponse(response.getBody());
					ObjectMapper mapper = new ObjectMapper();
					xmlJSONObj = XML.toJSONObject(response.getBody());
					final Map<String, Object> responseAsMap1 = mapper.readValue(xmlJSONObj.toString(),
							new TypeReference<Map<String, Object>>() {
							});
					if (responseAsMap1.containsKey("file")) {
						final Map<String, String> file = (Map<String, String>) responseAsMap1.get("file");
						fileId = file.get("fileId");
						logger.info("File Id {} for perfios txn Id {} ", fileId, perfiosTransactionId);
						bsinitiate.setStatus(STATUS.COMPLETED);
						initiateRequestPojo.setStatus(STATUS.COMPLETED.toString());

//						perfiosUploadModel.setFileId(fileId);
//						perfiosUploadModel.setTxnStatus("UPLOADED");
//						perfiosUploadModel.setResponseDate(new Date());
					} else {
						bsinitiate.setStatus(STATUS.FAILED);
						initiateRequestPojo.setStatus(STATUS.FAILED.toString());
						featureService.updateCustomer(bsinitiate.getCustWebNo(), bsinitiate.getDocWebNo(), "FAILED",
								null);
					}
				}

			} else {
				bsinitiate.setResponse(response.getStatusCode() + " " + response.getBody());
				bsinitiate.setStatus(STATUS.FAILED);
				initiateRequestPojo.setStatus(STATUS.FAILED.toString());
				featureService.updateCustomer(bsinitiate.getCustWebNo(), bsinitiate.getDocWebNo(), "FAILED", null);
			}
		} catch (Exception e) {
			logger.error("generateUploadInitiateResponse failed {}", e);
			bsinitiate.setResponse(e.getLocalizedMessage());
			bsinitiate.setStatus(STATUS.FAILED);
			initiateRequestPojo.setStatus(STATUS.FAILED.toString());
			featureService.updateCustomer(bsinitiate.getCustWebNo(), bsinitiate.getDocWebNo(), "FAILED", null);
		}

		bsInitiateRepository.save(bsinitiate);

//		bankStatementImpl.saveBankStatementInitiate(bsinitiate);
		initiateRequestPojo.setProcessId(bsinitiate.getProcessId());
		initiateRequestPojo.setFileId(fileId);
		return initiateRequestPojo;
	}

	private FileSystemResource generateBody(String fileName) {
		String fileLocalPath = uploadFilePath + File.separator + fileName;
		logger.info("File path {} ", fileLocalPath);
		File file = new File(fileLocalPath);
//		if (!file.exists()) {
//			String filePath = bucketFolder + fileName;
//			logger.info("Show File:AWS Parameter {}", filePath);
//			cloudUtils.download(filePath, fileLocalPath);
//			logger.info("Show File:S3 Object {}", cloudUtils);
//			file = new File(fileLocalPath);
//		} else {
//			logger.debug("File Exist {} ", file);
//		}
		FileSystemResource resource = new FileSystemResource(file);
		return resource;
	}

	private HttpHeaders getHttpHeaders(String payload, String signature, String date, String contentType)
			throws NoSuchAlgorithmException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("content-type", contentType);
		headers.set("Accept", "application/xml");
		headers.set("Host", perfiosUploadHelper.getPerfiosHost());
		headers.set("X-Perfios-Algorithm", "PERFIOS-RSA-SHA256");
		headers.set("X-Perfios-Content-Sha256", perfiosUploadHelper.SHA256(payload));
		headers.set("X-Perfios-Date", date);
		headers.set("X-Perfios-Signature", signature);
		headers.set("X-Perfios-Signed-Headers", "host;x-perfios-content-sha256;x-perfios-date");
		headers.set("cache-control", "no-cache");
		return headers;
	}

	private String createRetrieveReportPayload(String perfiosTxnId, String requestId, String reportType)
			throws JsonProcessingException {
		HashMap<String, Object> payload = new HashMap<>();

		payload.put("apiVersion", perfiosConfiguration.getVersion());
		payload.put("vendorId", perfiosConfiguration.getVendor());
		payload.put("txnId", requestId);
		payload.put("perfiosTransactionId", perfiosTxnId);

		if (!StringUtils.isEmpty(reportType)) {
			payload.put("reportType", reportType);
		} else {
			payload.put("reportType", perfiosConfiguration.getReportFormat());
		}
		Map<String, Object> payloadMap = new HashMap<>();
		payloadMap.put("payload", payload);
		return objectMapper.writeValueAsString(payloadMap);
	}

	@Async
	public InitiateRequestPojo initiateAsynReport(BankStatementTransaction bankStatementTransaction, Object extra)
			throws Exception {
		InitiateRequestPojo pojo = null;
		try {

			pojo = InitiateRequestPojo.class.cast(extra);

			if ("UPLOAD".equalsIgnoreCase(pojo.getRequestType())) {
				BankStatementReport bankStatementReport = bankStatementImpl.getBSReportByProcessIdAndTransactionId(
						bankStatementTransaction.getProcessId(), pojo.getTransactionId());
				if (bankStatementReport == null) {
					bankStatementReport = new BankStatementReport();
					bankStatementReport.setCustomProcessId(bankStatementTransaction.getProcessId());
					bankStatementReport.setTransactionId(pojo.getTransactionId());
					bankStatementReport.setProcessType(bankStatementTransaction.getProcessType());
				}
				featureService.updateCustomer(pojo.getCustomerWebRefNo(), pojo.getTranWebRefNo(), null, "REPORT");

				if (STATUS.COMPLETED != bankStatementReport.getStatus()) {

					RestTemplate restTemplate = new RestTemplate();
					String xPerfiosDate = perfiosUploadHelper.createXPerfiosDate();
					String retrievereportUrl = perfiosUploadHelper.getGenerateReportUrl()
							.replace("{perfiosTransactionId}", pojo.getTransactionId());
					String url = "https://" + perfiosUploadHelper.getPerfiosHost() + retrievereportUrl;
					logger.info("Perfios generate report url {} ", url);
					String signature = perfiosUploadHelper.createSignature(HttpMethod.POST.name(), retrievereportUrl,
							"", xPerfiosDate, null);
					logger.info("Perfios signature created successfully ");
					HttpHeaders httpHeaders = getHttpHeaders("", signature, xPerfiosDate, "application/xml");
					HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);
					logger.info("Http entity for retrieve report {} ", entity);
					ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
					if (HttpStatus.ACCEPTED.equals(response.getStatusCode()) && response != null
							&& response.getBody() != null) {
						logger.info("Http entity for retrieve report {} ", entity);
						bankStatementReport.setStatus(STATUS.PENDING);
						bankStatementImpl.saveBankStatementReport(bankStatementReport);
						retrieveReport(bankStatementReport, pojo);
					} else {
						featureService.updateCustomer(pojo.getCustomerWebRefNo(), pojo.getTranWebRefNo(), "FAILED",
								null);
						bankStatementReport.setStatus(STATUS.FAILED);
						bankStatementReport.setResponse(response.getBody());
						bankStatementImpl.saveBankStatementReport(bankStatementReport);
					}

				}

			} else {
				TransactionStatusResponse transactionStatusResponse = objectMapper
						.readValue(bankStatementTransaction.getResponse(), TransactionStatusResponse.class);
				for (Part vo : transactionStatusResponse.getStatus().getPart()) {
					BankStatementReport bankStatementReport = bankStatementImpl.getBSReportByProcessIdAndTransactionId(
							bankStatementTransaction.getProcessId(), vo.getPerfiosTransactionId());

					if (bankStatementReport == null) {
						bankStatementReport = new BankStatementReport();
						bankStatementReport.setCustomProcessId(bankStatementTransaction.getProcessId());
						bankStatementReport.setTransactionId(vo.getPerfiosTransactionId());
						bankStatementReport.setProcessType(bankStatementTransaction.getProcessType());
					
					}
					featureService.updateCustomer(pojo.getCustomerWebRefNo(), pojo.getTranWebRefNo(), null,
							"REPORT");
					pojo.setTransactionId(vo.getPerfiosTransactionId());
					if (STATUS.COMPLETED != bankStatementReport.getStatus()) {

						String payload = createRetrieveReportPayload(bankStatementReport.getTransactionId(),
								bankStatementTransaction.getProcessId(), null);
						JSONObject json = new JSONObject(payload);
						String actualPayload = XML.toString(json);
						HttpResponse httpResponse = perfiosHelper.executeRequest(HttpPost.class,
								perfiosConfiguration.getRetrieveReportUrl(), actualPayload,
								"application/x-www-form-urlencoded", null, null);
						String responseBody = EntityUtils.toString(httpResponse.getEntity());
						logger.debug("response for {} - {}", bankStatementTransaction.getProcessId(), responseBody);

						bankStatementReport
								.setResponseCode(String.valueOf(httpResponse.getStatusLine().getStatusCode()));
						bankStatementReport.setResponse(responseBody);
						bankStatementReport.setStatus(STATUS.COMPLETED);

						bankStatementImpl.saveBankStatementReport(bankStatementReport);
						if (STATUS.COMPLETED == bankStatementReport.getStatus()) {
							pojo.setStatus("success");
							break;
						}
					}

				}
			}
			return pojo;
		} catch (Exception e) {
			logger.error("Error while report initiate ", e);
			throw new Exception();
		}
	}

	private void retrieveReport(BankStatementReport bankStatementReport, InitiateRequestPojo pojo) throws Exception {
		try {
			RestTemplate restTemplate = new RestTemplate();
			String xPerfiosDate = perfiosUploadHelper.createXPerfiosDate();
			String retrievereportUrl = perfiosUploadHelper.getRetrieveReportUrl().replace("{perfiosTransactionId}",
					pojo.getTransactionId());
			String type = REPORT_TYPE_JSON;

			String url = "https://" + perfiosUploadHelper.getPerfiosHost() + retrievereportUrl + "?types=" + type;
			logger.info("Perfios retrieve report url {} ", url);
			Map<String, String> params = new HashMap<>();
			params.put("types", type);
			String signature = perfiosUploadHelper.createSignature(HttpMethod.GET.name(), retrievereportUrl, "",
					xPerfiosDate, params);
			logger.info("Perfios signature created successfully ");
			HttpHeaders httpHeaders = getHttpHeaders("", signature, xPerfiosDate, "application/xml");
			HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);
			logger.info("Http entity for retrieve report {} ", entity);
			Thread.sleep(10000);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			if ((HttpStatus.ACCEPTED.equals(response.getStatusCode()) || HttpStatus.OK.equals(response.getStatusCode()))
					&& response != null && response.getBody() != null) {
				logger.info("Http response status code for retrieve report  {}", response.getStatusCode());
//				logger.info("response  {}", response.getBody());
				bankStatementReport.setResponseCode(response.getStatusCode().value() + "");
				bankStatementReport.setResponse(response.getBody());
				bankStatementReport.setStatus(STATUS.COMPLETED);

			} else {
				bankStatementReport.setResponseCode(response.getStatusCode().value() + "");
				bankStatementReport.setResponse(response.getBody());
				bankStatementReport.setStatus(STATUS.FAILED);
				featureService.updateCustomer(pojo.getCustomerWebRefNo(), pojo.getTranWebRefNo(), "FAILED", null);
			}

			bankStatementImpl.saveBankStatementReport(bankStatementReport);

		} catch (Exception e) {
			logger.error("Error uploading file {} ", e);
			bankStatementReport.setStatus(STATUS.FAILED);
			bankStatementReport.setResponse(e.getLocalizedMessage());
			bankStatementImpl.saveBankStatementReport(bankStatementReport);
			featureService.updateCustomer(pojo.getCustomerWebRefNo(), pojo.getTranWebRefNo(), "FAILED", null);

		}

		if (STATUS.COMPLETED == bankStatementReport.getStatus()) {

			pojo.setStatus("success");
//
//			BankStatementInitiate bankStatementInitiate = bankStatementImpl
//					.getBankStatementInitiateByProcessId(bankStatementReport.getProcessId());
//
//			featureService.constructFeature(bankStatementReport.getResponse(), bankStatementInitiate);
		}
	}
}
