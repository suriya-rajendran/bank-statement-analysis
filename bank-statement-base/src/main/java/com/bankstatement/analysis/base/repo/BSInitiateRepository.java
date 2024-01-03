package com.bankstatement.analysis.base.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;

public interface BSInitiateRepository extends JpaRepository<BankStatementInitiate, Long> {

	BankStatementInitiate findByRequestIdAndCustWebNoAndDocWebNo(String requestId, String customerWebNo,
			String docWebNo);

	BankStatementInitiate findByProcessId(String processId);

}
