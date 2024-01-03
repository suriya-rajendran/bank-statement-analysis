package com.bankstatement.perfios.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankstatement.perfios.datamodel.PerfiosInstitutionModel;

@Repository
public interface PerfiosInstitutionModelRepository extends JpaRepository<PerfiosInstitutionModel, Long> {

}
