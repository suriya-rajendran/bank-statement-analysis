package com.bankstatement.analysis.base.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface BankStatementService<It, Ts, Ir, Rs> {
	ResponseEntity<?> initiateTransaction(It requestBody,String productCode);

	ResponseEntity<?> fetchTransactionDetails(It requestBody);

	ResponseEntity<?> transactionStatus(Ts requestBody);

	ResponseEntity<?> initiateReport(Ir requestBody);

	ResponseEntity<?> reportStatus(Rs requestBody);
}