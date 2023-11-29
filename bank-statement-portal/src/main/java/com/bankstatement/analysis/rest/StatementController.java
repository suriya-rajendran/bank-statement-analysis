package com.bankstatement.analysis.rest;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankstatement.analysis.base.datamodel.BankStatementAggregate;
import com.bankstatement.analysis.base.datamodel.BankStatementBaseModel.STATUS;
import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;
import com.bankstatement.analysis.base.datamodel.BankStatementReport;
import com.bankstatement.analysis.base.datamodel.BankStatementTransaction;
import com.bankstatement.analysis.base.service.BankStatementImpl;
import com.bankstatement.analysis.base.service.FeatureService;
import com.bankstatement.analysis.request.pojo.BankStatementPojo;
import com.bankstatement.analysis.request.pojo.CustomException;
import com.bankstatement.analysis.request.pojo.InitiateRequestPojo;
import com.bankstatement.perfios.impl.PerifiosBankStatementServiceImpl;

@RestController
@RequestMapping("/rest/bank")
public class StatementController {

	@Autowired
	PerifiosBankStatementServiceImpl perifiosBankStatementServiceImpl;

	@Autowired
	BankStatementImpl bankStatementImpl;

	@Autowired
	FeatureService featureService;

	public final static Logger logger = LoggerFactory.getLogger(StatementController.class);

	@PostMapping("/initate")
	public HashMap<String, String> saveApplicationDetails(HttpServletRequest request,
			@RequestBody BankStatementPojo bankStatementPojo) throws Exception {

		return featureService.saveApplicationDetails(bankStatementPojo);

	}

	@GetMapping("/initate-detail")
	public BankStatementAggregate getApplicationDetails(HttpServletRequest request,
			@RequestParam(value = "web_ref_id") String webRefId) throws Exception {

		return featureService.getApplicationDetails(webRefId);

	}

	@PostMapping("/initate-statement")
	public ResponseEntity<?> processStatement(HttpServletRequest request,
			@RequestBody InitiateRequestPojo initiateRequestPojo) throws Exception {
		if (StringUtils.isNotEmpty(initiateRequestPojo.getProcessType())) {
			featureService.updateApplicationDetails(initiateRequestPojo);
			if ("Perifios".equalsIgnoreCase(initiateRequestPojo.getProcessType())) {
				return perifiosBankStatementServiceImpl.initiateTransaction(initiateRequestPojo,
						getProductCode(request));
			}
		}
		throw new CustomException("400", "Invalid ProcessId type");

	}

	@GetMapping("/fetch/initate-detail")
	public ResponseEntity<?> fetchTransactionDetails(@RequestParam(value = "process_id") String processId)
			throws Exception {
		if (StringUtils.isNotEmpty(processId)) {
			BankStatementInitiate bankStatementInitiate = bankStatementImpl
					.getBankStatementInitiateByProcessId(processId);
			if (bankStatementInitiate != null) {
				if ("Perifios".equalsIgnoreCase(bankStatementInitiate.getProcessType())) {
					return perifiosBankStatementServiceImpl.transactionStatus(bankStatementInitiate);
				}
			}
		}
		throw new CustomException("400", "Invalid ProcessId");

	}

	@PostMapping("/initate-report")
	public ResponseEntity<?> initiateReport(@RequestParam(value = "process_id") String processId) throws Exception {
		if (StringUtils.isNotEmpty(processId)) {
			BankStatementTransaction bankStatementTransaction = bankStatementImpl
					.getBankStatementTransactionByProcessId(processId);
			if (bankStatementTransaction != null) {
				if (STATUS.COMPLETED == bankStatementTransaction.getStatus()) {

					if ("Perifios".equalsIgnoreCase(bankStatementTransaction.getProcessType())) {
						return perifiosBankStatementServiceImpl.initiateReport(bankStatementTransaction);
					}

				} else {
					throw new CustomException("400", "Transaction is pending");
				}
			}
		}
		throw new CustomException("400", "Invalid ProcessId type");

	}

	@GetMapping("/download-report")
	public ResponseEntity<?> reportFile(@RequestParam(value = "process_id") String processId,
			@RequestParam(value = "transaction_id") String transactionId) throws Exception {
		if (StringUtils.isNotEmpty(processId)) {
			BankStatementReport bankStatementReport = bankStatementImpl
					.getBSReportByProcessIdAndTransactionId(processId, transactionId);
			if (bankStatementReport != null) {
				if ("Perifios".equalsIgnoreCase(bankStatementReport.getProcessType())) {
					return perifiosBankStatementServiceImpl.reportLink(bankStatementReport);
				}
			}
		}
		throw new CustomException("400", "Invalid ProcessId type");

	}

	@PostMapping("/fetch/feature-detail")
	public ResponseEntity<?> fetchfeatureResponse(@RequestBody BankStatementPojo bankStatementPojo) throws Exception {
		return featureService.fetchfeatureResponse(bankStatementPojo);

	}

	private String getProductCode(HttpServletRequest request) {
		return (String) request.getAttribute("product_code");
	}

}
