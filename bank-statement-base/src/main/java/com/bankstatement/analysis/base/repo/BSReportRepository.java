package com.bankstatement.analysis.base.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankstatement.analysis.base.datamodel.BankStatementReport;

public interface BSReportRepository extends JpaRepository<BankStatementReport, Long> {

	BankStatementReport findByProcessIdAndTransactionId(String processId, String transactionId);

	List<BankStatementReport> findByProcessId(String processId);

	void deleteByProcessId(String processId);

}
