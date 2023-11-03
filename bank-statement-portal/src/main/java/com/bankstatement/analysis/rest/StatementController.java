package com.bankstatement.analysis.rest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.statement.perfios.impl.PerifiosBankStatementServiceImpl;
import com.bankstatement.analysis.request.pojo.InitiateRequestPojo;

@RestController
@RequestMapping("/rest/bank")
public class StatementController {

	@Autowired
	PerifiosBankStatementServiceImpl perifiosBankStatementServiceImpl;

	@PostMapping("/initate-statement")
	public ResponseEntity<?> processStatement(@RequestBody InitiateRequestPojo initiateRequestPojo) {

		if (StringUtils.isNotEmpty(initiateRequestPojo.getRequestType())) {
			if ("Perifios".equalsIgnoreCase(initiateRequestPojo.getRequestType())) {
				perifiosBankStatementServiceImpl.initiateTransaction(initiateRequestPojo);
			}
		}
		return ResponseEntity.badRequest().body("Invalid statement type");

	}
}
