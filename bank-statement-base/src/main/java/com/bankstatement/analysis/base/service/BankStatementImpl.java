package com.bankstatement.analysis.base.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;
import com.bankstatement.analysis.base.repo.BSInitiateRepository;

@Component
public class BankStatementImpl {

	@Autowired
	BSInitiateRepository bsInitiateRepository;

	public final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Transactional
	public BankStatementInitiate saveBankStatementInitiate(BankStatementInitiate bankStatementInitiate) {
		try {
			return bsInitiateRepository.save(bankStatementInitiate);
		} catch (Exception e) {
			logger.error(" error while saving initiate bs processId: " + bankStatementInitiate.getProcessId(), e);
		}
		return null;

	}

	public BankStatementInitiate getBankStatementInitiateByRequestId(String requestId) {
		try {
			return bsInitiateRepository.findByRequestId(requestId);
		} catch (Exception e) {
			logger.error(" error while fetching initiate bs requestId: " + requestId, e);
		}
		return null;

	}

	public BankStatementInitiate getBankStatementInitiateByProcessId(String processId) {
		try {
			return bsInitiateRepository.findByProcessId(processId);
		} catch (Exception e) {
			logger.error(" error while fetching initiate bs processId: " + processId, e);
		}
		return null;

	}

}
