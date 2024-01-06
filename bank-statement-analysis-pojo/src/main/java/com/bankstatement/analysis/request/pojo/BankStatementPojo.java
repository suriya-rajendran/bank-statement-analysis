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

	@JsonProperty("report_type")
	private String reportType = "APPLICATION";

	@JsonProperty("tenure")
	private Integer tenure;

	@JsonProperty("application_date")
	private String applicationDate;

	@JsonProperty("process_type")
	private String processType;

	@JsonProperty("loan_amount")
	private Double loanamount;

	@JsonProperty("customer_list")
	private List<CustomerPojo> customerList = new ArrayList<>();

	@JsonProperty("customer")
	private CustomerPojo customer;
}
