package com.bankstatement.analysis.request.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomerPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8339213761985591225L;

	@JsonProperty("customer_reference_no")
	private String customerReferenceNo;

	@JsonProperty("customer_web_ref_no")
	private String customerWebRefNo;

	@JsonProperty("customer_type")
	private CUSTOMER_TYPE_POJO customerType;

	@JsonProperty("account_details")
	private List<AccountPojo> accountPojo=new ArrayList<>();

	public enum CUSTOMER_TYPE_POJO {
		APPLICANT, CO_APPLICANT
	}
}
