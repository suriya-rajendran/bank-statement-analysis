package com.bankstatement.analysis.base.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankstatement.analysis.base.datamodel.BankStatementTransaction;

public interface BSTransactionRepository extends JpaRepository<BankStatementTransaction, Long> {

	BankStatementTransaction findByProcessId(String processId);

}
