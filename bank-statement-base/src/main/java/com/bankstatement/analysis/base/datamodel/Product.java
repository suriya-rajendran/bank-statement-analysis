package com.bankstatement.analysis.base.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;

import com.bankstatement.analysis.base.datamodel.ProductDetails.PRODUCT_DETAILS_SERVICE;
import com.bankstatement.analysis.base.util.EncryptionDecryptionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Entity
@Table(name = "product")
public class Product implements Serializable {
	/**
	*
	*/
	private static final long serialVersionUID = -3599224813080675870L;

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	@Column(name = "id")
	@Setter(value = AccessLevel.PROTECTED)
	private Long id;

	@Column(name = "product_code")
	private String productCode;

	@Column(name = "product_name")
	private String productName;
	
	@Column(name = "validity_count")
	private Integer validityCount;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@OrderBy("id")
	@JoinColumn(name = "product_id")
	@Setter(AccessLevel.NONE)
	private List<ProductDetails> productDetails = new ArrayList<>();

	@JsonIgnore
	public boolean validToken(PRODUCT_DETAILS_SERVICE service, String token) {
		if (service == null || CollectionUtils.isEmpty(this.productDetails)) {
			return false;
		}
		ProductDetails productDetail = this.productDetails.stream().filter(d -> service == d.getService()).findFirst()
				.get();
		if (productDetail == null) {
			return false;
		}
		PasswordEncoder encoder = new BCryptPasswordEncoder();

		boolean valid = encoder.matches(token, productDetail.getToken());
		return valid;
	}

	@JsonIgnore
	public HashMap<String, String> getSpecificProductDetail(PRODUCT_DETAILS_SERVICE service) {
		HashMap<String, String> details = new HashMap<>();
		if (service == null || CollectionUtils.isEmpty(this.productDetails)) {
			return details;
		}
		ProductDetails productDetail = this.productDetails.stream().filter(d -> service == d.getService()).findFirst()
				.get();
		if (productDetail == null) {
			return details;
		}
		details.put(productDetail.getService().toString(), EncryptionDecryptionUtil.encryptList(Arrays
				.asList(this.productCode, productDetail.getService().toString(), productCode + "BSA" + productDetail.getService())));
		return details;
	}

	@JsonIgnore
	public HashMap<String, String> getAllProductDetail() {
		HashMap<String, String> details = new HashMap<>();

		if (CollectionUtils.isEmpty(this.productDetails)) {
			return details;
		}
		for (ProductDetails vo : productDetails) {
			details.put(vo.getService().toString(), EncryptionDecryptionUtil.encryptList(Arrays
					.asList(this.productCode, vo.getService().toString(), productCode + "BSA" + vo.getService())));
		}
		return details;
	}

	@JsonIgnore
	public void addProductDetails(String env) {
		if (env != null && !env.isEmpty()) {
			try {
				PasswordEncoder encoder = new BCryptPasswordEncoder();

				ProductDetails details = new ProductDetails();
				details.setService(PRODUCT_DETAILS_SERVICE.valueOf(env));
				details.setToken(encoder.encode(productCode + "BSA" + PRODUCT_DETAILS_SERVICE.valueOf(env)));
				this.productDetails.add(details);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@JsonIgnore
	public void addAllEnvironmentProductDetails() {
		PasswordEncoder encoder = new BCryptPasswordEncoder();

		try {
			ArrayList<PRODUCT_DETAILS_SERVICE> serviceList = new ArrayList<>(
					Arrays.asList(PRODUCT_DETAILS_SERVICE.values()));
			for (PRODUCT_DETAILS_SERVICE vo : serviceList) {
				ProductDetails details = new ProductDetails();
				details.setService(vo);
				details.setToken(encoder.encode(productCode + "BSA" + vo));
				this.productDetails.add(details);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
