package com.bankstatement.analysis.rest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankstatement.analysis.base.service.BankStatementImpl;
import com.bankstatement.analysis.request.pojo.InitiateRequestPojo;
import com.bankstatement.perfios.impl.PerifiosBankStatementServiceImpl;

@RestController
@RequestMapping("/rest/bank")
public class StatementController {

	@Autowired
	PerifiosBankStatementServiceImpl perifiosBankStatementServiceImpl;

	@Autowired
	BankStatementImpl bankStatementImpl;

	@PostMapping("/initate-statement")
	public ResponseEntity<?> processStatement(@RequestBody InitiateRequestPojo initiateRequestPojo,
			@RequestParam(value = "product_code", required = false) String productCode) {
		if (StringUtils.isNotEmpty(initiateRequestPojo.getProcessType())) {
			if ("Perifios".equalsIgnoreCase(initiateRequestPojo.getProcessType())) {
				perifiosBankStatementServiceImpl.initiateTransaction(initiateRequestPojo, productCode);
			}
		}
		return ResponseEntity.badRequest().body("Invalid statement type");

	}

	@GetMapping("/fetch/initate-detail")
	public ResponseEntity<?> processStatement(@RequestParam(value = "process_id", required = false) String processId,
			@RequestParam(value = "product_code", required = false) String productCode) {
		if (StringUtils.isNotEmpty(processId)) {
			bankStatementImpl.fetchTransactionDetails(processId);
		}
		return ResponseEntity.badRequest().body("Invalid statement type");

	}
}
