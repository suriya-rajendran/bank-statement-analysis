package com.bankstatement.perfios.impl;

import org.apache.commons.lang.StringUtils;
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

	@Value("${vendor.code:PFS}")
	private String vendorCode;

	@Override
	public ResponseEntity<?> fetchTransactionDetails(InitiateRequestPojo initiateRequestPojo) {
		BankStatementInitiate bsinitiate = bankStatementImpl
				.getBankStatementInitiateByProcessId(initiateRequestPojo.getProcessId());
		if (bsinitiate != null) {
			initiateRequestPojo.setUrl("https://www.google.com/");
			return ResponseEntity.ok(initiateRequestPojo);
		}
		return null;
	}

	@Override
	public ResponseEntity<?> initiateTransaction(InitiateRequestPojo initiateRequestPojo, String productCode) {
		if (!StringUtils.isEmpty(initiateRequestPojo.getRequestId())) {

			BankStatementInitiate bsinitiate = bankStatementImpl
					.getBankStatementInitiateByRequestId(initiateRequestPojo.getRequestId());
			if (bsinitiate == null) {
				bsinitiate = new BankStatementInitiate();
				bsinitiate.setRequestId(initiateRequestPojo.getRequestId());
				bsinitiate.setProcessId(vendorCode);
			}
			bsinitiate.setProductCode(productCode);
			bsinitiate.setProcessType(initiateRequestPojo.getProcessType());
			bsinitiate.setRequestType(initiateRequestPojo.getRequestType());

			bsinitiate.setName(initiateRequestPojo.getName());
			bsinitiate.setNameMatch(initiateRequestPojo.isNameMatch());
			bsinitiate.setPennyDropVerification(initiateRequestPojo.isPennyDropVerification());

			initiateRequestPojo.setUrl("https://www.google.com/");
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
