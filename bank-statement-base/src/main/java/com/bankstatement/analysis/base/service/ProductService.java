package com.bankstatement.analysis.base.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.bankstatement.analysis.base.datamodel.Product;
import com.bankstatement.analysis.base.datamodel.ProductDetails.PRODUCT_DETAILS_SERVICE;
import com.bankstatement.analysis.base.repo.ProductRepository;
import com.bankstatement.analysis.base.util.EncryptionDecryptionUtil;
import com.bankstatement.analysis.request.pojo.ProductDetailsPojo;

@Service
public class ProductService {

	@Autowired
	ProductRepository productRepository;

	@Value("${product.environment:STAGING}")
	private String productEnvironment;

	@Value("${product.environment.count:100}")
	private int productEnvironmentCount;

	@Transactional
	public HashMap<String, String> saveProductDetails(ProductDetailsPojo productDetailsPojo) {
		Product product = productRepository.findByProductCode(productDetailsPojo.getCode());
		if (product != null) {
			return product.getSpecificProductDetail(PRODUCT_DETAILS_SERVICE.valueOf(productEnvironment.toUpperCase()));
		}
		product = new Product();
		product.setProductCode(productDetailsPojo.getCode());
		product.setProductName(productDetailsPojo.getName());
		product.addProductDetails(productEnvironment.toUpperCase());
		product.setValidityCount(productEnvironmentCount);

		productRepository.save(product);

		return product.getSpecificProductDetail(PRODUCT_DETAILS_SERVICE.valueOf(productEnvironment.toUpperCase()));
	}

	public HashMap<String, String> fetchProductDetails(String productCode) {

		Product product = productRepository.findByProductCode(productCode);
		if (product != null) {
			return product.getSpecificProductDetail(PRODUCT_DETAILS_SERVICE.valueOf(productEnvironment.toUpperCase()));
		}
		return null;
	}

	public String getProductCode(HttpServletRequest request) {
		List<String> details = decryptDetails(request);
		if (!CollectionUtils.isEmpty(details) && details.size() == 3) {
			return details.get(0);
		}
		//TODO throw ex
		return null;
	}

	private List<String> decryptDetails(HttpServletRequest request) {
		String token = request.getHeader("token");
		return EncryptionDecryptionUtil.decryptString(token);

	}

	public void updateValidityCount(String productCode) {
		Product product = productRepository.findByProductCode(productCode);
		if (product == null) {
			return;
		}
		product.setValidityCount(product.getValidityCount() - 1);
		productRepository.save(product);

	}

	public ProductDetailsPojo fetchProfileDetails(HttpServletRequest request) {
		List<String> details = decryptDetails(request);
		ProductDetailsPojo productPojo = new ProductDetailsPojo();
		Product product = productRepository.findByProductCode(details.get(0));
		if (product != null) {
			productPojo.setCode(product.getProductCode());
			productPojo.setName(product.getProductName());
			productPojo.setCount(product.getValidityCount());

		}
		return productPojo;
	}

}
