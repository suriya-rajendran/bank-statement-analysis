package com.bankstatement.analysis.base.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankstatement.analysis.base.datamodel.BankStatementAggregate;

public interface BankStatementAggregateRepo extends JpaRepository<BankStatementAggregate, Long> {

	BankStatementAggregate findByWebRefID(String webRefNo);

}
