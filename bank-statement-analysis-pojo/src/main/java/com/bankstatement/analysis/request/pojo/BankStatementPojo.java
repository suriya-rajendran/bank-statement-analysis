package com.bankstatement.analysis.request.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BankStatementPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1087415538735262128L;

	@JsonProperty("application_reference_no")
	private String applicationReferenceNo;

	@JsonProperty("web_ref_no")
	private String webRefNo;

	@JsonProperty("tenure")
	private Integer tenure;

	@JsonProperty("application_date")
	private String applicationDate;

	@JsonProperty("loan_amount")
	private Double loanamount;

	@JsonProperty("customer")
	private List<CustomerPojo> Customer = new ArrayList<>();
}
