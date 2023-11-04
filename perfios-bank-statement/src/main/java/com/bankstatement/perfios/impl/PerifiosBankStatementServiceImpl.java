package com.bankstatement.perfios.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;
import com.bankstatement.analysis.base.service.BankStatementImpl;
import com.bankstatement.analysis.base.service.BankStatementService;
import com.bankstatement.analysis.request.pojo.InitiateRequestPojo;

@Service
public class PerifiosBankStatementServiceImpl
		implements BankStatementService<InitiateRequestPojo, String, String, String> {

	@Autowired
	BankStatementImpl bankStatementImpl;

	public final static Logger logger = LoggerFactory.getLogger(PerifiosBankStatementServiceImpl.class);

	@Value("${perifios.code:PFS}")
	private String vendorCode;

	public ResponseEntity<?> fetchTransactionDetails(BankStatementInitiate bsinitiate) {
		InitiateRequestPojo initiateRequestPojo = new InitiateRequestPojo();
		initiateRequestPojo.setUrl(bsinitiate.getRequestUrl());
		initiateRequestPojo.setRequestId(bsinitiate.getRequestId());
		initiateRequestPojo.setProcessId(bsinitiate.getProcessId());
		return ResponseEntity.ok(initiateRequestPojo);
	}

	@Override
	public ResponseEntity<?> initiateTransaction(InitiateRequestPojo initiateRequestPojo, String productCode) {
		if (!StringUtils.isEmpty(initiateRequestPojo.getRequestId())) {

			BankStatementInitiate bsinitiate = bankStatementImpl
					.getBankStatementInitiateByRequestId(initiateRequestPojo.getRequestId(), productCode);
			if (bsinitiate == null) {
				bsinitiate = new BankStatementInitiate();
				bsinitiate.setRequestId(initiateRequestPojo.getRequestId());
				bsinitiate.setProcessId(productCode.toUpperCase()+"-"+vendorCode+"-");
			}
			bsinitiate.setProductCode(productCode);
			bsinitiate.setProcessType(initiateRequestPojo.getProcessType());
			bsinitiate.setRequestType(initiateRequestPojo.getRequestType());

			bsinitiate.setName(initiateRequestPojo.getName());
			bsinitiate.setNameMatch(initiateRequestPojo.isNameMatch());
			bsinitiate.setPennyDropVerification(initiateRequestPojo.isPennyDropVerification());
			bsinitiate.setRequestUrl("https://www.google.com/");
			
			initiateRequestPojo.setUrl(bsinitiate.getRequestUrl());
			initiateRequestPojo.setProcessId(bsinitiate.getProcessId());
			bankStatementImpl.saveBankStatementInitiate(bsinitiate);
			return ResponseEntity.ok(initiateRequestPojo);
		} else {
			return ResponseEntity.badRequest().body("Request Id cannot be empty");
		}
	}

	@Override
	public ResponseEntity<?> transactionStatus(String requestBody) {

		return ResponseEntity.ok("Transaction status checked successfully");
	}

	@Override
	public ResponseEntity<?> initiateReport(String requestBody) {

		return ResponseEntity.ok("Report initiation successful");
	}

	@Override
	public ResponseEntity<?> reportStatus(String requestBody) {

		return ResponseEntity.ok("Report status checked successfully");
	}

}
