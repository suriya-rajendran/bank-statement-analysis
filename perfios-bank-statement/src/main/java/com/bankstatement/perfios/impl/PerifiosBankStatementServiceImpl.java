package com.bankstatement.perfios.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bankstatement.analysis.base.datamodel.BankStatementBaseModel.STATUS;
import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;
import com.bankstatement.analysis.base.datamodel.BankStatementReport;
import com.bankstatement.analysis.base.datamodel.BankStatementTransaction;
import com.bankstatement.analysis.base.service.BankStatementImpl;
import com.bankstatement.analysis.base.service.BankStatementService;
import com.bankstatement.analysis.base.service.FeatureService;
import com.bankstatement.analysis.base.service.ProductService;
import com.bankstatement.analysis.perfios.response.pojo.Part;
import com.bankstatement.analysis.perfios.response.pojo.TransactionStatusResponse;
import com.bankstatement.analysis.request.pojo.CustomException;
import com.bankstatement.analysis.request.pojo.InitiateRequestPojo;
import com.bankstatement.analysis.request.pojo.TransactionStatusDetail;
import com.bankstatement.analysis.request.pojo.TransactionStatusPojo;
import com.bankstatement.perfios.async.AsyncBankStatementService;
import com.bankstatement.perfios.configuration.PerfiosConfiguration;
import com.bankstatement.perfios.util.PerfiosHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class PerifiosBankStatementServiceImpl implements
		BankStatementService<InitiateRequestPojo, BankStatementInitiate, BankStatementTransaction, BankStatementReport> {

	@Autowired
	BankStatementImpl bankStatementImpl;

	@Autowired
	PerfiosConfiguration perfiosConfiguration;

	@Autowired
	PerfiosHelper perfiosHelper;

	@Autowired
	ProductService productService;

	@Autowired
	FeatureService featureService;

	@Autowired
	AsyncBankStatementService asyncBankStatementService;

	public final static Logger logger = LoggerFactory.getLogger(PerifiosBankStatementServiceImpl.class);

	private ObjectMapper objectMapper = new ObjectMapper();

	private static final String SUCCESS_RESPONSE = "Success";

	private static final String TRANSACTION_URL = "url";

	private static final String TRANSACTION_EXPIRES = "expires";

	private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

	@Value("${report.downloadPath}")
	private String reportDownloadPath;

	private final String[] initiateRequestType = new String[] { "netbankingFetch", "statement", "choice", "choice-all",
			"nbf-all", "statement-all", "accountAggregator" };

	private String createPayload(String processId, InitiateRequestPojo inputs) throws JsonProcessingException {

		HashMap<String, Object> payload = new HashMap<>();

		payload.put("apiVersion", perfiosConfiguration.getVersion());
		payload.put("vendorId", perfiosConfiguration.getVendor());
		payload.put("txnId", processId);
		payload.put("destination", inputs.getRequestType());
		payload.put("loanAmount", "1000000");
		payload.put("loanDuration", "36");
		payload.put("loanType", "Home");
		payload.put("transactionCompleteCallbackUrl", perfiosConfiguration.getCallBackUrl() + processId);
		payload.put("acceptancePolicy", "atLeastOneTransactionInRange");

		payload.put("returnUrl", perfiosConfiguration.getReturnUrl());
		if (!StringUtils.isEmpty(inputs.getYearMonthFrom())) {
			payload.put("yearMonthFrom", inputs.getYearMonthFrom());
		}
		if (!StringUtils.isEmpty(inputs.getYearMonthTo())) {
			payload.put("yearMonthTo", inputs.getYearMonthTo());
		}

		Map<String, Object> payloadMap = new HashMap<>();
		payloadMap.put("payload", payload);
		return objectMapper.writeValueAsString(payloadMap);
	}

	@Override
	public ResponseEntity<?> initiateTransaction(InitiateRequestPojo initiateRequestPojo, String productCode)
			throws Exception {
		if (!StringUtils.isEmpty(initiateRequestPojo.getRequestId())) {
			if (Arrays.asList(initiateRequestType).contains(initiateRequestPojo.getRequestType())) {
				try {
					boolean valid = false;
					BankStatementInitiate bsinitiate = bankStatementImpl
							.getBankStatementInitiateByRequestId(initiateRequestPojo.getRequestId(), productCode);
					if (bsinitiate == null) {
						bsinitiate = new BankStatementInitiate();
						bsinitiate.setRequestId(initiateRequestPojo.getRequestId());
						bsinitiate.setProcessType(initiateRequestPojo.getProcessType());
						bsinitiate.setProcessId(
								productCode.toUpperCase() + "-" + perfiosConfiguration.getVendorCode() + "-");
						bsinitiate.setApplicationReferenceNo(initiateRequestPojo.getApplicationReferenceNo()); 
						valid = true;
					} 
					bsinitiate.setApplicationDate(initiateRequestPojo.getApplicationDate());
					String actualPayload = generateInitiateRequest(initiateRequestPojo, productCode, bsinitiate);

					generateInitiateResponse(initiateRequestPojo, bsinitiate, actualPayload);
					if (STATUS.COMPLETED == bsinitiate.getStatus() && valid) {
						productService.updateValidityCount(productCode);
					}

				} catch (IOException | URISyntaxException | ParseException e) {
					logger.info("error while initiating  ", e);
					throw new Exception();
				}

				return ResponseEntity.ok(initiateRequestPojo);
			} else {
				throw new CustomException("400", "Invalid Request Type");
			}
		} else {
			throw new CustomException("400", "Request Id cannot be empty");
		}
	}

	private void generateInitiateResponse(InitiateRequestPojo initiateRequestPojo, BankStatementInitiate bsinitiate,
			String actualPayload) throws Exception {
		HttpResponse httpResponse = perfiosHelper.executeRequest(HttpPost.class,
				perfiosConfiguration.getInitTransactionUrl(), actualPayload, CONTENT_TYPE, null, null);
		String responseBody = EntityUtils.toString(httpResponse.getEntity());
		logger.debug("response body {} ", responseBody);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			JSONObject xmlJSONObj = XML.toJSONObject(responseBody);

			bsinitiate.setResponse(xmlJSONObj.toString());
			bsinitiate.setResponseCode(String.valueOf(httpResponse.getStatusLine().getStatusCode()));
			Map<String, Object> response = objectMapper.readValue(xmlJSONObj.toString(),
					new TypeReference<Map<String, Object>>() {
					});
			if (response != null && response.containsKey(SUCCESS_RESPONSE)) {
				Map<String, Object> successResponse = (Map<String, Object>) response.get(SUCCESS_RESPONSE);
				bsinitiate.setStatus(STATUS.COMPLETED);
				initiateRequestPojo.setUrl((String) successResponse.get(TRANSACTION_URL));
				initiateRequestPojo.setExpiry((String) successResponse.get(TRANSACTION_EXPIRES));
				initiateRequestPojo.setStatus(STATUS.COMPLETED.toString());
			} else {
				bsinitiate.setStatus(STATUS.FAILED);
				initiateRequestPojo.setStatus(STATUS.FAILED.toString());
			}
		}
		bankStatementImpl.saveBankStatementInitiate(bsinitiate);
		initiateRequestPojo.setProcessId(bsinitiate.getProcessId());
	}

	private String generateInitiateRequest(InitiateRequestPojo initiateRequestPojo, String productCode,
			BankStatementInitiate bsinitiate) throws Exception {
		bsinitiate.setProductCode(productCode);
		bsinitiate.setRequestType(initiateRequestPojo.getRequestType());

		bsinitiate.setName(initiateRequestPojo.getName());
		bsinitiate.setNameMatch(initiateRequestPojo.isNameMatch());
		bsinitiate.setPennyDropVerification(initiateRequestPojo.isPennyDropVerification());

		String payload = createPayload(bsinitiate.getProcessId(), initiateRequestPojo);
		JSONObject json = new JSONObject(payload);
		String actualPayload = XML.toString(json);
		bsinitiate.setRequest(actualPayload);
		bankStatementImpl.saveBankStatementInitiate(bsinitiate);
		return actualPayload;
	}

	private String createTxnStatusPayload(String processId) throws JsonProcessingException {
		HashMap<String, Object> payload = new HashMap<>();

		payload.put("apiVersion", perfiosConfiguration.getVersion());
		payload.put("vendorId", perfiosConfiguration.getVendor());
		payload.put("txnId", processId);

		Map<String, Object> payloadMap = new HashMap<>();
		payloadMap.put("payload", payload);
		return objectMapper.writeValueAsString(payloadMap);
	}

	@Override
	public ResponseEntity<?> transactionStatus(BankStatementInitiate bsinitiate) throws Exception {
		TransactionStatusPojo transactionStatusPojo = new TransactionStatusPojo();
		try {
			InitiateRequestPojo initiateRequestPojo = new InitiateRequestPojo();
			if (STATUS.COMPLETED == bsinitiate.getStatus() && StringUtils.isNotEmpty(bsinitiate.getResponse())) {
				Map<String, Object> response = objectMapper.readValue(bsinitiate.getResponse(),
						new TypeReference<Map<String, Object>>() {
						});
				if (response != null && response.containsKey(SUCCESS_RESPONSE)) {
					Map<String, Object> successResponse = (Map<String, Object>) response.get(SUCCESS_RESPONSE);

					initiateRequestPojo.setUrl((String) successResponse.get(TRANSACTION_URL));
					initiateRequestPojo.setExpiry((String) successResponse.get(TRANSACTION_EXPIRES));

					BankStatementTransaction bankStatementTransaction = bankStatementImpl
							.getBankStatementTransactionByProcessId(bsinitiate.getProcessId());

					if (bankStatementTransaction == null) {
						bankStatementTransaction = new BankStatementTransaction();
						bankStatementTransaction.setCustomProcessId(bsinitiate.getProcessId());
						bankStatementTransaction.setProcessType(bsinitiate.getProcessType());
					}

					if (STATUS.COMPLETED != bankStatementTransaction.getStatus()) {

						String payload = createTxnStatusPayload(bsinitiate.getProcessId());
						bankStatementTransaction.setRequest(payload);

						JSONObject json = new JSONObject(payload);
						String actualPayload = XML.toString(json);
						HttpResponse httpResponse = perfiosHelper.executeRequest(HttpPost.class,
								perfiosConfiguration.getTxnStatusUrl(), actualPayload,
								"application/x-www-form-urlencoded", null, null);
						String responseBody = EntityUtils.toString(httpResponse.getEntity());
						// TODO Change to debug
						logger.info("response " + responseBody);

						JSONObject xmlJSONObj = XML.toJSONObject(responseBody);

						JsonNode jsonNode = objectMapper.readTree(xmlJSONObj.toString());

						// Get the "Part" node
						JsonNode partNode = jsonNode.path("Status").path("Part");

						if (!partNode.isArray()) {
							// Convert the "Part" object to an array
							JsonNode[] partsArray = { partNode };

							// Replace the "Part" object with the array
							((ObjectNode) jsonNode.path("Status")).set("Part", objectMapper.valueToTree(partsArray));
						}

						// Convert the updated JSON back to a string
						String updatedResponseBody = objectMapper.writeValueAsString(jsonNode);

						logger.info("response JSON: " + updatedResponseBody);
						TransactionStatusResponse transactionStatusResponse = objectMapper
								.readValue(updatedResponseBody, TransactionStatusResponse.class);

						bankStatementTransaction
								.setResponseCode(String.valueOf(httpResponse.getStatusLine().getStatusCode()));
						bankStatementTransaction.setResponse(updatedResponseBody);
						if ("completed".equalsIgnoreCase(transactionStatusResponse.getStatus().getProcessing())) {
							bankStatementTransaction.setStatus(STATUS.COMPLETED);
						} else {
							bankStatementTransaction.setStatus(STATUS.PENDING);
						}
						for (Part vo : transactionStatusResponse.getStatus().getPart()) {
							TransactionStatusDetail transactionStatusDetail = new TransactionStatusDetail();

							transactionStatusDetail.setStatus(vo.getStatus());
							transactionStatusDetail.setReason(vo.getReason());
							transactionStatusDetail.setErrorCode(vo.getErrorCode());
							transactionStatusDetail.setTransactionId(vo.getPerfiosTransactionId());
							// getReportStatus(bankStatementTransaction, vo, transactionStatusDetail);
							transactionStatusDetail.setReportStatus("Initiated");
							transactionStatusPojo.getTransactionDetails().add(transactionStatusDetail);
						}
						transactionStatusPojo.setStatus(transactionStatusResponse.getStatus().getProcessing());
						bankStatementImpl.saveBankStatementTransaction(bankStatementTransaction);

					} else {
						TransactionStatusResponse transactionStatusResponse = objectMapper
								.readValue(bankStatementTransaction.getResponse(), TransactionStatusResponse.class);
						for (Part vo : transactionStatusResponse.getStatus().getPart()) {

							TransactionStatusDetail transactionStatusDetail = new TransactionStatusDetail();

							transactionStatusDetail.setStatus(vo.getStatus());
							transactionStatusDetail.setReason(vo.getReason());
							transactionStatusDetail.setErrorCode(vo.getErrorCode());
							transactionStatusDetail.setTransactionId(vo.getPerfiosTransactionId());

							transactionStatusDetail.setReportStatus("Initiated");

							transactionStatusPojo.getTransactionDetails().add(transactionStatusDetail);
						}
					}
					if (STATUS.COMPLETED == bankStatementTransaction.getStatus()) {
						initiateReport(bankStatementTransaction);
					}
				}
			}

			initiateRequestPojo.setStatus(bsinitiate.getStatus().toString());
			initiateRequestPojo.setRequestId(bsinitiate.getRequestId());
			initiateRequestPojo.setProcessId(bsinitiate.getProcessId());
			transactionStatusPojo.setInitiateRequestPojo(initiateRequestPojo);
			return ResponseEntity.ok(transactionStatusPojo);
		} catch (URISyntaxException | IOException | ParseException e) {
			logger.error("Error fetching transaction status ", e);
			throw new Exception();
		}
	}

//	private void getReportStatus(BankStatementTransaction bankStatementTransaction, Part vo,
//			TransactionStatusDetail transactionStatusDetail) throws Exception {
//		BankStatementReport bankStatementReport = bankStatementImpl.getBSReportByProcessIdAndTransactionId(
//				bankStatementTransaction.getProcessId(), vo.getPerfiosTransactionId());
//		if (bankStatementReport != null) {
//			transactionStatusDetail.setReportStatus(bankStatementReport.getStatus().toString());
//			return;
//		}
//
//		transactionStatusDetail.setReportStatus("Not Initiated");
//	}

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

	@Override
	public ResponseEntity<?> initiateReport(BankStatementTransaction bankStatementTransaction) throws Exception {
		asyncBankStatementService.initiateAsynReport(bankStatementTransaction);
		return ResponseEntity.ok("Report initiation successful");

	}

	@Override
	public ResponseEntity<?> reportLink(BankStatementReport bankStatementReport) throws Exception {

		try {
			byte[] responseAsByte = null;

			if (STATUS.COMPLETED == bankStatementReport.getStatus()) {
				if (bankStatementReport.getData() == null || bankStatementReport.getData().length == 0) {
					String payloadAsJson = createRetrieveReportPayload(bankStatementReport.getTransactionId(),
							bankStatementReport.getProcessId(), perfiosConfiguration.getReportFormat());
					JSONObject json = new JSONObject(payloadAsJson);
					String actualPayload = XML.toString(json);
					HttpResponse httpResponse;
					httpResponse = perfiosHelper.executeRequest(HttpPost.class,
							perfiosConfiguration.getRetrieveReportUrl(), actualPayload,
							"application/x-www-form-urlencoded", null, null);
					responseAsByte = EntityUtils.toByteArray(httpResponse.getEntity());

					logger.debug("response for {} - {}", bankStatementReport.getProcessId(), responseAsByte.toString());

					bankStatementReport.setData(responseAsByte);

					bankStatementImpl.saveBankStatementReport(bankStatementReport);
				}
				if (bankStatementReport.getData() != null && bankStatementReport.getData().length > 0)
					return ResponseEntity.ok()
							.header("Content-Disposition",
									"attachment; filename=\"" + bankStatementReport.getTransactionId() + "."
											+ perfiosConfiguration.getReportFormat() + "\"")
							.body(bankStatementReport.getData());
			}

			return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			logger.error("Error while report download ", e);
			throw new Exception();
		}
	}

}
