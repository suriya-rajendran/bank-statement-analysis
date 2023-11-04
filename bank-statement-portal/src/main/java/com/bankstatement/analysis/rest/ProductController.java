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
	public HashMap<String, String> fetchProductDetails(@RequestParam("product_code") String productCode) {
		return productService.fetchProductDetails(productCode);

	}
	
	@GetMapping(value = "/rest/profile/details")
	public ProductDetailsPojo fetchProfileDetails(HttpServletRequest request) {
		return productService.fetchProfileDetails(request);

	}
}