package com.bankstatement.analysis.rest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankstatement.analysis.request.pojo.InitiateRequestPojo;
import com.bankstatement.perfios.impl.PerifiosBankStatementServiceImpl;

@RestController
@RequestMapping("/rest/bank")
public class StatementController {

	@Autowired
	PerifiosBankStatementServiceImpl perifiosBankStatementServiceImpl;

	@PostMapping("/initate-statement")
	public ResponseEntity<?> processStatement(@RequestBody InitiateRequestPojo initiateRequestPojo) {

		if (StringUtils.isNotEmpty(initiateRequestPojo.getProcessType())) {
			if ("Perifios".equalsIgnoreCase(initiateRequestPojo.getProcessType())) {
				perifiosBankStatementServiceImpl.initiateTransaction(initiateRequestPojo);
			}
		}
		return ResponseEntity.badRequest().body("Invalid statement type");

	}
}
