package com.bankstatement.analysis.rest;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankstatement.analysis.base.service.ProductService;
import com.bankstatement.analysis.request.pojo.ProductDetailsPojo;

@RestController
public class ProductController {

	@Autowired
	ProductService productService;

	@PostMapping(value = "/rest/create/product-details")
	public HashMap<String, String> saveProductDetails(@RequestBody ProductDetailsPojo productDetailsPojo) {
		return productService.saveProductDetails(productDetailsPojo);

	}

	@GetMapping(value = "/rest/fetch/product-token")
	public HashMap<String, String> fetchProductDetails(@RequestParam("product_code") String productCode)
			throws Exception {
		return productService.fetchProductDetails(productCode);

	}

	@GetMapping(value = "/rest/profile/details")
	public ProductDetailsPojo fetchProfileDetails(HttpServletRequest request,
			@RequestParam(defaultValue = "5") Integer lastRecords) {
		return productService.fetchProfileDetails(getProductCode(request), lastRecords);

	}

	private String getProductCode(HttpServletRequest request) {
		return (String) request.getAttribute("product_code");
	}
}