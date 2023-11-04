package com.bankstatement.analysis.base.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;

public interface BSInitiateRepository extends JpaRepository<BankStatementInitiate, Long> {

	BankStatementInitiate findByRequestId(String requestId);

	BankStatementInitiate findByProcessId(String processId);

}
