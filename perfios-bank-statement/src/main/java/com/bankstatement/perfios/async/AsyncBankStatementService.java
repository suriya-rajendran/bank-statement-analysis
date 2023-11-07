package com.bankstatement.perfios.async;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bankstatement.analysis.base.datamodel.BankStatementBaseModel.STATUS;
import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;
import com.bankstatement.analysis.base.datamodel.BankStatementReport;
import com.bankstatement.analysis.base.datamodel.BankStatementTransaction;
import com.bankstatement.analysis.base.service.BankStatementImpl;
import com.bankstatement.analysis.base.service.FeatureService;
import com.bankstatement.analysis.base.service.ProductService;
import com.bankstatement.analysis.perfios.response.pojo.Part;
import com.bankstatement.analysis.perfios.response.pojo.TransactionStatusResponse;
import com.bankstatement.perfios.configuration.PerfiosConfiguration;
import com.bankstatement.perfios.util.PerfiosHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AsyncBankStatementService {

	private static final Logger logger = LoggerFactory.getLogger(AsyncBankStatementService.class);

	@Autowired
	FeatureService featureService;

	@Autowired
	BankStatementImpl bankStatementImpl;

	@Autowired
	PerfiosConfiguration perfiosConfiguration;

	@Autowired
	PerfiosHelper perfiosHelper;

	@Autowired
	ProductService productService;

	private ObjectMapper objectMapper = new ObjectMapper();

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
	public void initiateAsynReport(BankStatementTransaction bankStatementTransaction) throws Exception {

		try {

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

					bankStatementReport.setResponseCode(String.valueOf(httpResponse.getStatusLine().getStatusCode()));
					bankStatementReport.setResponse(responseBody);
					bankStatementReport.setStatus(STATUS.COMPLETED);

					bankStatementImpl.saveBankStatementReport(bankStatementReport);
					BankStatementInitiate bankStatementInitiate = bankStatementImpl
							.getBankStatementInitiateByProcessId(bankStatementTransaction.getProcessId());

					featureService.constructFeature(responseBody, bankStatementInitiate);
				} 
			}
		} catch (Exception e) {
			logger.error("Error while report initiate ", e);
			throw new Exception();
		}
	}
}
