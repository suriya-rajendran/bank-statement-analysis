package com.bankstatement.analysis.request.pojo;

import java.io.Serializable;
import java.util.HashMap;

import lombok.Data;

@Data
public class ProductDetailsPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3689145959977863732L;

	private String code;

	private String name;

	private Integer count;

}
