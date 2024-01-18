package com.bankstatement.analysis.external.rest;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;
import com.bankstatement.analysis.base.service.BankStatementImpl;
import com.bankstatement.analysis.request.pojo.CustomException;
import com.bankstatement.perfios.impl.PerifiosBankStatementServiceImpl;

@RestController
@RequestMapping("/rest/external")
public class ExternalRestController {

	public final static Logger logger = LoggerFactory.getLogger(ExternalRestController.class);

	@Autowired
	PerifiosBankStatementServiceImpl perifiosBankStatementServiceImpl;

	@Autowired
	BankStatementImpl bankStatementImpl;

	@GetMapping("/perfios/save-transaction-detail")
	public ResponseEntity<?> processStatementUpload(@RequestParam(value = "transaction_id") String processId)
			throws Exception {
		logger.info("ExternalRestController processStatementUpload transaction_id: {}",processId);
		if (StringUtils.isNotEmpty(processId)) {
			BankStatementInitiate bankStatementInitiate = bankStatementImpl
					.getBankStatementInitiateByProcessId(processId);
			if (bankStatementInitiate != null) {
				if ("Perfios".equalsIgnoreCase(bankStatementInitiate.getProcessType())) {
					ResponseEntity<?> transactionStatusPojo = perifiosBankStatementServiceImpl
							.transactionStatus(bankStatementInitiate);
					if (HttpStatus.OK == transactionStatusPojo.getStatusCode()) {
						HashMap<String, String> payload = new HashMap<>();
						payload.put("message", "success");
						return ResponseEntity.ok(payload);
					}
				}
			}
		}
		throw new CustomException("400", "Invalid Transaction Id");

	}
	
	@PostMapping("/perfios/save-transaction-detail")
	public ResponseEntity<?> processStatement(@RequestParam(value = "transaction_id") String processId)
			throws Exception {
		logger.info("ExternalRestController processStatement transaction_id: {}",processId);
		if (StringUtils.isNotEmpty(processId)) {
			BankStatementInitiate bankStatementInitiate = bankStatementImpl
					.getBankStatementInitiateByProcessId(processId);
			if (bankStatementInitiate != null) {
				if ("Perfios".equalsIgnoreCase(bankStatementInitiate.getProcessType())) {
					ResponseEntity<?> transactionStatusPojo = perifiosBankStatementServiceImpl
							.transactionStatus(bankStatementInitiate);
					if (HttpStatus.OK == transactionStatusPojo.getStatusCode()) {
						HashMap<String, String> payload = new HashMap<>();
						payload.put("message", "success");
						return ResponseEntity.ok(payload);
					}
				}
			}
		}
		throw new CustomException("400", "Invalid Transaction Id");

	}
}
