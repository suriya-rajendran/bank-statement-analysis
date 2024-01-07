package com.bankstatement.analysis.base.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;

import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;
import com.bankstatement.analysis.base.datamodel.BankStatementReport;
import com.bankstatement.analysis.base.datamodel.BankStatementTransaction;
import com.bankstatement.analysis.base.repo.BSInitiateRepository;
import com.bankstatement.analysis.base.repo.BSReportRepository;
import com.bankstatement.analysis.base.repo.BSTransactionRepository;

@Component
public class BankStatementImpl {

	@Autowired
	BSInitiateRepository bsInitiateRepository;

	@Autowired
	BSTransactionRepository bsTransactionRepository;

	@Autowired
	BSReportRepository bsReportRepository;

	public final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	public Map<String, String> fetchBankStatementRecords(Integer days, String productCode) {
		Pageable limit = PageRequest.of(0, days);
		// return convertObjectToMap(bsInitiateRepository.findByRequestDate(productCode,
		// new Date(), limit));
		return null;
	}

	private Map<String, String> convertObjectToMap(List<Object[]> obj) {

		Map<String, String> map = new HashMap<>();
		for (Object[] ob : obj) {
			Date createdDate = (Date) ob[0];
			map.put(new SimpleDateFormat("yyyy-MM-dd").format(createdDate), (String) ob[1].toString());
		}
		return map;

	}

	@org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BankStatementInitiate saveBankStatementInitiate(BankStatementInitiate bankStatementInitiate)
			throws Exception {
		try {
			return bsInitiateRepository.saveAndFlush(bankStatementInitiate);
		} catch (Exception e) {
			logger.error(" error while saving initiate bs processId: " + bankStatementInitiate.getProcessId(), e);
			throw new Exception();
		}

	}

//	public BankStatementInitiate getBankStatementInitiateByRequestId(String requestId, String productCode)
//			throws Exception {
//		try {
//			return bsInitiateRepository.findByRequestIdAndProductCode(requestId, productCode);
//		} catch (Exception e) {
//			logger.error(" error while fetching initiate bs requestId: " + requestId, e);
//			throw new Exception();
//		}
//
//	}

	public BankStatementInitiate getBankStatementInitiateByRequestIdAndCustomerWebNo(String requestId, String custWebNo,
			String docWebNo) throws Exception {
		try {
			return bsInitiateRepository.findByRequestIdAndCustWebNoAndDocWebNo(requestId, custWebNo, docWebNo);
		} catch (Exception e) {
			logger.error(" error while fetching initiate bs requestId: " + requestId, e);
			throw new Exception();
		}

	}

	public BankStatementInitiate getBankStatementInitiateByProcessId(String processId) throws Exception {
		try {
			return bsInitiateRepository.findByProcessId(processId);
		} catch (Exception e) {
			logger.error(" error while fetching initiate bs processId: " + processId, e);
			throw new Exception();
		}

	}
	
	public BankStatementInitiate getBankStatementInitiateByDocWebRefId(String processId) throws Exception {
		try {
			return bsInitiateRepository.findByDocWebNo(processId);
		} catch (Exception e) {
			logger.error(" error while fetching initiate bs processId: " + processId, e);
			throw new Exception();
		}

	}

	@org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BankStatementTransaction saveBankStatementTransaction(BankStatementTransaction bankStatementTransaction)
			throws Exception {
		try {
			return bsTransactionRepository.saveAndFlush(bankStatementTransaction);
		} catch (Exception e) {
			logger.error(" error while saving transaction bs processId: " + bankStatementTransaction.getProcessId(), e);
			throw new Exception();
		}

	}

	public BankStatementTransaction getBankStatementTransactionByProcessId(String processId) throws Exception {
		try {
			return bsTransactionRepository.findByProcessId(processId);
		} catch (Exception e) {
			logger.error(" error while fetching initiate bs requestId: " + processId, e);
			throw new Exception();
		}

	}

	@org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public BankStatementReport saveBankStatementReport(BankStatementReport bankStatementReport) throws Exception {
		try {
			return bsReportRepository.saveAndFlush(bankStatementReport);
		} catch (Exception e) {
			logger.error(" error while saving report bs processId: " + bankStatementReport.getProcessId(), e);
			throw new Exception();
		}

	}

	public BankStatementReport getBSReportByProcessIdAndTransactionId(String processId, String transactionId)
			throws Exception {
		try {
			return bsReportRepository.findByProcessIdAndTransactionId(processId, transactionId);
		} catch (Exception e) {
			logger.error(" error while fetching report bs requestId: " + transactionId, e);
			throw new Exception();
		}

	}

	public List<BankStatementReport> getBSReportByProcessId(String processId) throws Exception {
		try {
			return bsReportRepository.findByProcessId(processId);
		} catch (Exception e) {
			logger.error(" error while fetching report bs requestId: " + processId, e);
			throw new Exception();
		}

	}

}
