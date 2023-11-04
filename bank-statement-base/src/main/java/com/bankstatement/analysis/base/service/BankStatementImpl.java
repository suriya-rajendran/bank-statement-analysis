package com.bankstatement.analysis.base.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

	@Transactional
	public BankStatementInitiate saveBankStatementInitiate(BankStatementInitiate bankStatementInitiate)
			throws Exception {
		try {
			return bsInitiateRepository.saveAndFlush(bankStatementInitiate);
		} catch (Exception e) {
			logger.error(" error while saving initiate bs processId: " + bankStatementInitiate.getProcessId(), e);
			throw new Exception();
		}

	}

	public BankStatementInitiate getBankStatementInitiateByRequestId(String requestId, String productCode)
			throws Exception {
		try {
			return bsInitiateRepository.findByRequestIdAndProductCode(requestId, productCode);
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

	@Transactional
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

	@Transactional
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
