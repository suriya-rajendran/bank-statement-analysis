package com.bankstatement.analysis.base.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;

public interface BSInitiateRepository extends JpaRepository<BankStatementInitiate, Long> {

	BankStatementInitiate findByRequestIdAndProductCode(String requestId, String productCode);

	BankStatementInitiate findByProcessId(String processId);

	@Query("SELECT DATE(ds.requestDate), count(ds.id) from BankStatementInitiate ds where DATE(ds.requestDate) <= :requestDate and ds.productCode = :productCode GROUP BY DATE(ds.requestDate), ds.productCode order by ds.requestDate desc")
	List<Object[]> findByRequestDate(String productCode, Date requestDate,
			org.springframework.data.domain.Pageable limit);

}
