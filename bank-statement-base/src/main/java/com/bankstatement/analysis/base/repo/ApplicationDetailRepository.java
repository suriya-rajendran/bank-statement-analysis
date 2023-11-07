package com.bankstatement.analysis.base.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankstatement.analysis.base.datamodel.ApplicationDetail;

public interface ApplicationDetailRepository extends JpaRepository<ApplicationDetail, Long> {

	ApplicationDetail findByApplicationReferenceNo(String applicationReferenceNo);
}
