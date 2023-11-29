package com.bankstatement.analysis.base.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankstatement.analysis.base.datamodel.Customer;

public interface CustomerRepo extends JpaRepository<Customer, Long> {

	Customer findByWebRefID(String webRefId);

}
