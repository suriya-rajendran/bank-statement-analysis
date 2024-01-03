package com.bankstatement.analysis.base.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface BankStatementService<It, Ts, Ir, Rs> {
	ResponseEntity<?> initiateTransaction(It requestBody, String productCode) throws Exception;

	ResponseEntity<?> transactionStatus(Ts requestBody) throws Exception;

	ResponseEntity<?> initiateReport(Ir requestBody,Object extra) throws Exception;

	ResponseEntity<?> reportLink(Rs requestBody) throws Exception;

	String getProcessDefinitionName();
}