package com.bankstatement.perfios.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.bankstatement.analysis.base.datamodel.AccountDetail;
import com.bankstatement.analysis.base.datamodel.BankStatementAggregate;
import com.bankstatement.analysis.base.datamodel.BankStatementBaseModel.STATUS;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails.CATEGORY_TYPE;
import com.bankstatement.analysis.base.datamodel.CustomerTransactionDetails.REPORT_STATUS;
import com.bankstatement.analysis.base.repo.CustomerRepo;
import com.bankstatement.analysis.base.repo.CustomerTransactionDetailsRepo;
import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;
import com.bankstatement.analysis.base.datamodel.BankStatementReport;
import com.bankstatement.analysis.base.datamodel.BankStatementTransaction;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails;
import com.bankstatement.analysis.base.datamodel.Customer;
import com.bankstatement.analysis.base.datamodel.CustomerTransactionDetails;
import com.bankstatement.analysis.base.datamodel.Document;
import com.bankstatement.analysis.base.datamodel.BankStatementAggregate.AGGREGATE_STATUS;
import com.bankstatement.analysis.base.service.BankStatementImpl;
import com.bankstatement.analysis.base.service.BankStatementService;
import com.bankstatement.analysis.base.service.FeatureService;
import com.bankstatement.analysis.base.service.ProductService;
import com.bankstatement.analysis.perfios.request.pojo.AccountAnalysis;
import com.bankstatement.analysis.perfios.response.pojo.Part;
import com.bankstatement.analysis.perfios.response.pojo.TransactionResponse;
import com.bankstatement.analysis.perfios.response.pojo.TransactionStatusResponse;
import com.bankstatement.analysis.request.pojo.CustomException;
import com.bankstatement.analysis.request.pojo.InitiateRequestPojo;
import com.bankstatement.analysis.request.pojo.TransactionStatusDetail;
import com.bankstatement.analysis.request.pojo.TransactionStatusPojo;
import com.bankstatement.analysis.transaction.pojo.BankAccountDetails;
import com.bankstatement.analysis.transaction.pojo.Xn;
import com.bankstatement.perfios.async.AsyncBankStatementService;
import com.bankstatement.perfios.configuration.PerfiosConfiguration;
import com.bankstatement.perfios.datamodel.PerfiosInstitutionModel;
import com.bankstatement.perfios.repo.PerfiosInstitutionModelRepository;
import com.bankstatement.perfios.util.PerfiosHelper;
import com.bankstatement.perfios.util.PerfiosUploadHelper;
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
	PerfiosUploadHelper perfiosUploadHelper;

	@Autowired
	ProductService productService;

	@Autowired
	FeatureService featureService;

	@Autowired
	AsyncBankStatementService asyncBankStatementService;

	@Autowired
	PerfiosInstitutionModelRepository perfiosInstitutionModelRepository;

	@Autowired
	ObjectMapper mapper = new ObjectMapper();

	public final static Logger logger = LoggerFactory.getLogger(PerifiosBankStatementServiceImpl.class);

	private ObjectMapper objectMapper = new ObjectMapper();

	private static final String SUCCESS_RESPONSE = "Success";

	private static final String TRANSACTION_URL = "url";

	private static final String TRANSACTION_EXPIRES = "expires";

	private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

	@Value("${report.downloadPath}")
	private String reportDownloadPath;

	private final String[] initiateRequestType = new String[] { "netbankingFetch", "statement", "choice", "choice-all",
			"nbf-all", "statement-all", "accountAggregator", "upload" };

	@Autowired
	CustomerRepo customerRepo;

	@Autowired
	CustomerTransactionDetailsRepo customerTransactionDetailsRepo;

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
		if (StringUtils.isEmpty(productCode)) {
			productCode = "";
		}

		BankStatementAggregate aggregate = featureService
				.getApplicationDetails(initiateRequestPojo.getApplicationWebRefNo());

		if (aggregate != null) {
			Customer customer = aggregate.getCustomer().stream()
					.filter(d -> d.getWebRefID()!=null && d.getWebRefID().equalsIgnoreCase(initiateRequestPojo.getCustomerWebRefNo()))
					.findFirst().orElseThrow(() -> new CustomException("400", "Invalid Customer Web Ref.No"));

			CustomerTransactionDetails detail = customer.getTransactionDetail().stream()
					.filter(d -> d.getWebRefID()!=null && d.getWebRefID().equalsIgnoreCase(initiateRequestPojo.getTranWebRefNo())).findFirst()
					.orElseThrow(() -> new CustomException("400", "Invalid Transaction Web Ref.No"));

			if (Arrays.asList(initiateRequestType).contains(detail.getRequestType().toLowerCase())) {

				try {

					BankStatementInitiate bsinitiate = bankStatementImpl
							.getBankStatementInitiateByRequestIdAndCustomerWebNo(aggregate.getWebRefID(),
									customer.getWebRefID(), detail.getWebRefID());

					if (bsinitiate == null) {
						bsinitiate = new BankStatementInitiate();

						featureService.updateCustomer(bsinitiate.getCustWebNo(), bsinitiate.getDocWebNo(), null,
								"INITIATED");
						bsinitiate.setRequestId(aggregate.getWebRefID());
						bsinitiate.setRequestType(detail.getRequestType());
						bsinitiate.setProcessType(aggregate.getProcessType());
						bsinitiate.setProcessId(aggregate.getProcessType().toUpperCase() + "-"
								+ perfiosConfiguration.getVendorCode() + "-");
						bsinitiate.setCustWebNo(customer.getWebRefID());
						bsinitiate.setDocWebNo(detail.getWebRefID());

					}
					if (STATUS.COMPLETED != bsinitiate.getStatus()) {
						bsinitiate.setApplicationDate(aggregate.getApplicationDate());

						initiateRequestPojo.setFileName(detail.getDocuments().getImagePath());
						if ("UPLOAD".equalsIgnoreCase(detail.getRequestType())) {
							bsinitiate.setScannedDoc(detail.isScannedDoc());
							bsinitiate.setInstitutionType(detail.getInstitutionType());
							initiateRequestPojo.setScannedDoc(detail.isScannedDoc());
							String actualPayload = generateUploadInitiateRequest(initiateRequestPojo, bsinitiate);
							asyncBankStatementService.generateUploadInitiateResponse(initiateRequestPojo, bsinitiate,
									actualPayload);
							initiateRequestPojo.setStatus("INPROGRESS");
							aggregate.setAggregateStatus(AGGREGATE_STATUS.COMPLETED);
						} else {
							String actualPayload = generateInitiateRequest(initiateRequestPojo, productCode,
									bsinitiate);
							generateInitiateResponse(initiateRequestPojo, bsinitiate, actualPayload);
							initiateRequestPojo.setStatus("INPROGRESS");
							aggregate.setAggregateStatus(AGGREGATE_STATUS.COMPLETED);
						}
					}
//					if (STATUS.COMPLETED == bsinitiate.getStatus() && valid) {
//						productService.updateValidityCount(productCode);
//					}

				} catch (IOException | URISyntaxException | ParseException e) {
					logger.info("error while initiating  ", e);
					throw new Exception();
				}

				return ResponseEntity.ok(customerTransactionDetailsRepo.findByWebRefID(detail.getWebRefID()));
			} else {
				throw new CustomException("400", "Invalid Request Type");
			}

		} else {
			throw new CustomException("400", "Invalid Web Ref Id");
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
				featureService.updateCustomer(bsinitiate.getCustWebNo(), bsinitiate.getDocWebNo(), "FAILED", null);
			}
		}
		bankStatementImpl.saveBankStatementInitiate(bsinitiate);
		initiateRequestPojo.setProcessId(bsinitiate.getProcessId());
	}

	private String generateInitiateRequest(InitiateRequestPojo initiateRequestPojo, String productCode,
			BankStatementInitiate bsinitiate) throws Exception {
//		bsinitiate.setProductCode(productCode);
		bsinitiate.setRequestType(initiateRequestPojo.getRequestType());

		String payload = createPayload(bsinitiate.getProcessId(), initiateRequestPojo);
		JSONObject json = new JSONObject(payload);
		String actualPayload = XML.toString(json);
		bsinitiate.setRequest(actualPayload);
		bankStatementImpl.saveBankStatementInitiate(bsinitiate);
		return actualPayload;
	}

	private String generateUploadInitiateRequest(InitiateRequestPojo initiateRequestPojo,
			BankStatementInitiate bsinitiate) throws Exception {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		LocalDate currentDate = LocalDate.now();
		LocalDate minusOne = currentDate.minusMonths(12);
		String yearMonthFrom = format.format(java.sql.Date.valueOf(minusOne));
		String yearMonthTo = format.format(java.sql.Date.valueOf(currentDate.minusMonths(1)));
		initiateRequestPojo.setYearMonthFrom(yearMonthFrom);
		initiateRequestPojo.setYearMonthTo(yearMonthTo);

		String payload = perfiosUploadHelper.createPayload(bsinitiate.getProcessId(), initiateRequestPojo);
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
				InitiateRequestPojo pojo = new InitiateRequestPojo();
				pojo.setCustomerWebRefNo(bsinitiate.getCustWebNo());
				pojo.setTranWebRefNo(bsinitiate.getDocWebNo());
				pojo.setRequestType(bsinitiate.getRequestType());
				pojo.setProcessId(bsinitiate.getProcessId());
				pojo.setApplicationWebRefNo(bsinitiate.getRequestId());
				BankStatementTransaction bankStatementTransaction = bankStatementImpl
						.getBankStatementTransactionByProcessId(bsinitiate.getProcessId());

				if (bankStatementTransaction == null) {
					bankStatementTransaction = new BankStatementTransaction();
					bankStatementTransaction.setCustomProcessId(bsinitiate.getProcessId());
					bankStatementTransaction.setProcessType(bsinitiate.getProcessType());
					featureService.updateCustomer(bsinitiate.getCustWebNo(), bsinitiate.getDocWebNo(), null,
							"CALLBACK");
				}

				if ("UPLOAD".equalsIgnoreCase(bsinitiate.getRequestType())) {
					pojo.setTransactionId(bsinitiate.getTransactionId());
					pojo.setScannedDoc(bsinitiate.isScannedDoc());
					ObjectMapper mapper = new ObjectMapper();
					JSONObject xmlJSONObj = XML.toJSONObject(bsinitiate.getResponse());
					final Map<String, Object> responseAsMap1 = mapper.readValue(xmlJSONObj.toString(),
							new TypeReference<Map<String, Object>>() {
							});
					try {
						if (responseAsMap1.containsKey("file")) {
							final Map<String, String> file = (Map<String, String>) responseAsMap1.get("file");
							String fileId = file.get("fileId");

							initiateRequestPojo.setFileId(fileId);

							RestTemplate restTemplate = new RestTemplate();
							String xPerfiosDate = perfiosUploadHelper.createXPerfiosDate();
							String fileProcessUrl = perfiosUploadHelper.getProcessFileUrl()
									.replace("{perfiosTransactionId}", bsinitiate.getTransactionId());
							String url = "https://" + perfiosUploadHelper.getPerfiosHost() + fileProcessUrl;
							logger.info("Perfios Upload File url {} ", url);
							String payload = createProcessFilePayload(fileId, bsinitiate.getInstitutionType());
							String signature = perfiosUploadHelper.createSignature(HttpMethod.POST.name(),
									fileProcessUrl, payload, xPerfiosDate, null);
							logger.info("Perfios signature created successfully ");
							HttpHeaders httpHeaders = getHttpHeaders(payload, signature, xPerfiosDate,
									"application/xml");
							HttpEntity<String> entity = new HttpEntity<String>(payload, httpHeaders);
							logger.info("Process file entity {} ", entity);
							ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity,
									String.class);
							if (HttpStatus.OK.equals(response.getStatusCode()) && response != null
									&& response.getBody() != null) {
								xmlJSONObj = XML.toJSONObject(response.getBody());
								TransactionResponse jsonResponse = objectMapper.readValue(xmlJSONObj.toString(),
										TransactionResponse.class);

								bankStatementTransaction.setResponse(response.getBody());
								if (jsonResponse != null && jsonResponse.getBankStatement() != null
										&& jsonResponse.getBankStatement().getBankAccounts() != null
										&& jsonResponse.getBankStatement().getBankAccounts().getBankAccount() != null
										&& jsonResponse.getBankStatement().getBankAccounts().getBankAccount()
												.isComplete()) {
									bankStatementTransaction.setStatus(STATUS.COMPLETED);

									initiateReport(bankStatementTransaction, pojo);
								} else {
									bankStatementTransaction.setStatus(STATUS.FAILED);
									bankStatementTransaction.setResponse(response.getBody());
									featureService.updateCustomer(bsinitiate.getCustWebNo(), bsinitiate.getDocWebNo(),
											"FAILED", null);

								}

								// SCANNED DOC
							} else if (HttpStatus.ACCEPTED.equals(response.getStatusCode()) && response != null
									&& response.getBody() != null) {
								initiateReport(bankStatementTransaction, pojo);
							} else {
								bankStatementTransaction.setStatus(STATUS.FAILED);
								bankStatementTransaction.setResponse(response.getBody());
								featureService.updateCustomer(bsinitiate.getCustWebNo(), bsinitiate.getDocWebNo(),
										"FAILED", null);

							}

						}
					} catch (Exception e) {
						if (e instanceof HttpClientErrorException) {
							HttpClientErrorException httpClientErrorException = (HttpClientErrorException) e;

							if (httpClientErrorException.getStatusCode().value() == 404) {
								String responseBody = httpClientErrorException.getResponseBodyAsString();
								xmlJSONObj = XML.toJSONObject(responseBody);
								parseErrorMessageFromJson(bsinitiate.getCustWebNo(), bsinitiate.getDocWebNo(),
										xmlJSONObj.toString());

							}
						}
						bankStatementTransaction.setStatus(STATUS.FAILED);
						bankStatementTransaction.setResponse(e.getLocalizedMessage());
					}
					bankStatementImpl.saveBankStatementTransaction(bankStatementTransaction);
				} else {
					Map<String, Object> response = objectMapper.readValue(bsinitiate.getResponse(),
							new TypeReference<Map<String, Object>>() {
							});
					if (response != null && response.containsKey(SUCCESS_RESPONSE)) {
						Map<String, Object> successResponse = (Map<String, Object>) response.get(SUCCESS_RESPONSE);

						initiateRequestPojo.setUrl((String) successResponse.get(TRANSACTION_URL));
						initiateRequestPojo.setExpiry((String) successResponse.get(TRANSACTION_EXPIRES));

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
								((ObjectNode) jsonNode.path("Status")).set("Part",
										objectMapper.valueToTree(partsArray));
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
							initiateReport(bankStatementTransaction, null);
						}
					}
				}
			}

			initiateRequestPojo.setStatus(bsinitiate.getStatus().toString());
			initiateRequestPojo.setApplicationRequestNo(bsinitiate.getRequestId());
			initiateRequestPojo.setProcessId(bsinitiate.getProcessId());
			transactionStatusPojo.setInitiateRequestPojo(initiateRequestPojo);
			return ResponseEntity.ok(transactionStatusPojo);
		} catch (URISyntaxException | IOException |

				ParseException e) {
			logger.error("Error fetching transaction status ", e);
			throw new Exception();
		}
	}

	private void parseErrorMessageFromJson(String custRefNo, String docWebRefNo, String jsonString) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(jsonString);

			// Assuming the error message is in the "message" field of the "error" object
			JsonNode errorMessageNode = jsonNode.path("error").path("message");
			logger.info("errorMessage {}", errorMessageNode.textValue());
			featureService.updateCustomer(custRefNo, docWebRefNo, "FAILED", null);

		} catch (Exception e) {
			e.printStackTrace(); // Handle the exception according to your needs
		}
	}

	private String createProcessFilePayload(String fileId, String institutionId) throws JsonProcessingException {
		String payload = perfiosUploadHelper.createPayload(fileId, institutionId);
		JSONObject json = new JSONObject(payload);
		String actualPayload = XML.toString(json);
		return actualPayload;
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
	public ResponseEntity<?> initiateReport(BankStatementTransaction bankStatementTransaction, Object extra)
			throws Exception {
		if (extra == null) {
			BankStatementInitiate bsinitiate = bankStatementImpl
					.getBankStatementInitiateByProcessId(bankStatementTransaction.getProcessId());
			InitiateRequestPojo initiateRequestPojo = new InitiateRequestPojo();
			initiateRequestPojo.setRequestType(bsinitiate.getRequestType());
			initiateRequestPojo.setTransactionId(bsinitiate.getTransactionId());
			initiateRequestPojo.setScannedDoc(bsinitiate.isScannedDoc());
			initiateRequestPojo.setCustomerWebRefNo(bsinitiate.getCustWebNo());
			initiateRequestPojo.setTranWebRefNo(bsinitiate.getDocWebNo());
			initiateRequestPojo.setProcessId(bsinitiate.getProcessId());
			initiateRequestPojo.setApplicationWebRefNo(bsinitiate.getRequestId());
			asyncBankStatementService.initiateAsynReport(bankStatementTransaction, initiateRequestPojo);
		} else {
			asyncBankStatementService.initiateAsynReport(bankStatementTransaction, extra);
		}
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

	@Override
	public String getProcessDefinitionName() {
		// TODO Auto-generated method stub
		return "Perfios";
	}

	public List<PerfiosInstitutionModel> getInstitutionList() {
		List<PerfiosInstitutionModel> perfiosInstitutionModel = perfiosInstitutionModelRepository.findAll();
		if (!CollectionUtils.isEmpty(perfiosInstitutionModel)) {
			return perfiosInstitutionModel;
		} else {
			try {
				perfiosInstitutionModelRepository.deleteAll();

				String xPerfiosDate = perfiosUploadHelper.createXPerfiosDate();

				String url = "https://" + perfiosUploadHelper.getPerfiosHost()
						+ perfiosUploadHelper.getUploadInstitutionListUrl() + "?processingType=STATEMENT";

				logger.info("BSA Upload Institution list url {} ", url);
				Map<String, String> params = new HashMap<>();

				params.put("processingType", "STATEMENT");
				String signature = perfiosUploadHelper.createSignature(HttpMethod.GET.name(),
						perfiosUploadHelper.getUploadInstitutionListUrl(), "", xPerfiosDate, params);
				logger.info("Signature created successfully ");
				HttpHeaders httpHeaders = getHttpHeaders("", signature, xPerfiosDate, "application/xml");
				HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);

				RestTemplate restTemplate = new RestTemplate();
				ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

				logger.info("Institution list Api response {} ", response);

				if (response.getStatusCode().equals(HttpStatus.OK) && response != null && response.getBody() != null) {
					ObjectMapper mapper = new ObjectMapper();
					JSONObject xmlJSONObj = XML.toJSONObject(response.getBody());
					final Map<String, Object> responseAsMap = mapper.readValue(xmlJSONObj.toString(),
							new TypeReference<Map<String, Object>>() {
							});
					if (responseAsMap.containsKey("institutions")) {
						final Map<String, Object> institutions = (Map<String, Object>) responseAsMap
								.get("institutions");
						final List<Map<String, Object>> ins = (List<Map<String, Object>>) institutions
								.get("institution");
						List<PerfiosInstitutionModel> institutionList = new ArrayList<PerfiosInstitutionModel>();
						for (Map<String, Object> institution : ins) {
							PerfiosInstitutionModel institutionModel = new PerfiosInstitutionModel();
							Boolean isNetbankingLinked = (Boolean) institution.get("netBankingLinked");
							institutionModel.setInstitutionId(String.valueOf(institution.get("id")));
							institutionModel.setInstitutionName((String) institution.get("name"));
							institutionModel.setInstitutionType((String) institution.get("institutionType"));
							institutionModel.setNetBankingLinked(isNetbankingLinked);
							institutionList.add(institutionModel);
						}
						perfiosInstitutionModelRepository.saveAll(institutionList);
						return institutionList;
					}
				}
			} catch (Exception e) {
				logger.error("Error getting institution list {} ", e);
			}
		}
		return null;
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

	public void constructFeature(InitiateRequestPojo initiateRequestPojo) throws Exception {

		BankStatementReport bankStatementReport = bankStatementImpl.getBSReportByProcessIdAndTransactionId(
				initiateRequestPojo.getProcessId(), initiateRequestPojo.getTransactionId());
		if (bankStatementReport != null && STATUS.COMPLETED == bankStatementReport.getStatus()
				&& bankStatementReport.getResponse() != null) {

			Customer cust = customerRepo.findByWebRefID(initiateRequestPojo.getCustomerWebRefNo());

			if (cust != null) {
				CustomerTransactionDetails vo = cust.getTransactionDetail().stream()
						.filter(d -> d.getWebRefID()!=null && d.getWebRefID().equalsIgnoreCase(initiateRequestPojo.getTranWebRefNo()))
						.findFirst().orElse(null);

				if (!CollectionUtils.isEmpty(vo.getAccountDetail())) {
					vo.getAccountDetail().clear();
				}

				if (vo != null) {
					JsonNode jsonNode = objectMapper.readTree(bankStatementReport.getResponse());

					JsonNode accountDetails = jsonNode.get("accountXns");

					List<BankAccountDetails> bankDetails = objectMapper.readValue(accountDetails.toString(),
							new TypeReference<List<BankAccountDetails>>() {
							});

					for (BankAccountDetails det : bankDetails) {

						JsonNode bankdetails = jsonNode.get("accountAnalysis");

						List<AccountAnalysis> accountAnalysis = objectMapper.readValue(bankdetails.toString(),
								new TypeReference<List<AccountAnalysis>>() {
								});

						AccountDetail accountDetail = new AccountDetail();

						accountDetail.setAcNumber(det.getAccountNo());

						AccountAnalysis info = accountAnalysis.stream()
								.filter(d -> d.getAccountNo().equalsIgnoreCase(det.getAccountNo())).findAny()
								.orElse(null);

						accountDetail.setBankName(info.getSummaryInfo().getInstName());

						for (Xn d : det.getXns()) {
							BankTransactionDetails details = new BankTransactionDetails();
							details.setDate(d.getDate());

							details.setChqNo(d.getChqNo());

							details.setNarration(d.getNarration());

							details.setAmount(d.getAmount());

							details.setOriginalCategory(d.getCategory());

							details.setBalance(d.getBalance());

							details.setRequestId(initiateRequestPojo.getApplicationWebRefNo());

							if (d.getAmount() < 0) {
								details.setCategoryType(CATEGORY_TYPE.OUTFLOW);
							} else {
								details.setCategoryType(CATEGORY_TYPE.INFLOW);
							}

							if (d.getCategory().toUpperCase().contains("Transfer To".toUpperCase())) {
								details.setCategory("Transfer out");
							} else if (d.getCategory().toUpperCase().contains("Transfer From".toUpperCase())) {
								details.setCategory("Transfer in");
							} else {
								details.setCategory(d.getCategory());
							}

							accountDetail.addTransactionDetails(details);

						}
						logger.info("{}", accountDetail.getTransaction());

						vo.addAccountDetails(accountDetail);
					}
					vo.setReportStatus(REPORT_STATUS.CALLBACK);
					customerTransactionDetailsRepo.save(vo);
					featureService.fetchfeatureResponse(initiateRequestPojo);
				}
			}
		}
	}
}
