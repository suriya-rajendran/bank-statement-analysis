package com.bankstatement.analysis.base.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bankstatement.analysis.base.datamodel.BankStatementAggregate;
import com.bankstatement.analysis.request.pojo.InitiateRequestPojo;

public interface BankStatementAggregateRepo extends JpaRepository<BankStatementAggregate, Long> {

	BankStatementAggregate findByWebRefID(String webRefNo);

	BankStatementAggregate findByApplicationReferenceNo(String webRefNo);

}
