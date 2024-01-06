package com.bankstatement.analysis.base.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankstatement.analysis.base.datamodel.CustomerTransactionDetails;

public interface CustomerTransactionDetailsRepo extends JpaRepository<CustomerTransactionDetails, Long> {

	CustomerTransactionDetails findByWebRefID(String webRefId);
}
