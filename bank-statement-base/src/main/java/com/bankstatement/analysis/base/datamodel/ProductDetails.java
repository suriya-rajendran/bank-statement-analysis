package com.bankstatement.analysis.base.datamodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Entity
@Table(name = "product_details")
public class ProductDetails implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -3599224813080675870L;

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	@Column(name = "id")
	@Setter(value = AccessLevel.PROTECTED)
	private Long id;

	@Column(name = "service")
	@Enumerated(EnumType.STRING)
	private PRODUCT_DETAILS_SERVICE service;

	@Column(name = "token")
	private String token;

	public static enum PRODUCT_DETAILS_SERVICE {
		STAGING, PRODUCTION
	}
}
