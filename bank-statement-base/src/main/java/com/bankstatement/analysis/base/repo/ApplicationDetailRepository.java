package com.bankstatement.analysis.base.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bankstatement.analysis.base.datamodel.ApplicationDetail;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails;

public interface ApplicationDetailRepository extends JpaRepository<ApplicationDetail, Long> {

	ApplicationDetail findByApplicationReferenceNo(String applicationReferenceNo);

	@Query("SELECT x FROM BankTransactionDetails x where x.requestId =:requestId")
	List<BankTransactionDetails> findTransactionDetailsByRequestId(String requestId);
}
