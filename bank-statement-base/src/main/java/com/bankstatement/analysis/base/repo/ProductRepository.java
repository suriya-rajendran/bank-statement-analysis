package com.bankstatement.analysis.base.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankstatement.analysis.base.datamodel.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Product findByProductCode(String code);

}
