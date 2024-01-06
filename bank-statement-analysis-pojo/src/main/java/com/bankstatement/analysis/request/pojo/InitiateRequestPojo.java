package com.bankstatement.analysis.request.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class InitiateRequestPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9140553891726859613L;

//	private String reportType;

	@JsonProperty("process_type")
	private String processType;

	@JsonProperty("year_month_from")
	private String yearMonthFrom;

	@JsonProperty("customer_web_ref_no")
	private String customerWebRefNo;

	@JsonProperty("application_web_ref_no")
	private String applicationWebRefNo;
	
	@JsonProperty("tran_web_ref_no")
	private String tranWebRefNo;

	@JsonProperty("application_date")
	private String applicationDate;

	@JsonProperty("year_month_to")
	private String yearMonthTo;

	@JsonProperty("application_request_no")
	private String applicationRequestNo;

	@JsonProperty("request_type")
	private String requestType;
 
	private String name;

	@JsonProperty("penny_drop_verification")
	private boolean pennyDropVerification;

	@JsonProperty("scanned_doc")
	private boolean scannedDoc;

	@JsonProperty("process_id")
	private String processId;

	private String url;

	private String expiry;

	private String status;

	@JsonProperty("file_name")
	private String fileName;

	@JsonProperty("institution_type")
	private String institutionType;
	
	@JsonProperty("transaction_id")
	private String transactionId;

	@JsonProperty("file_id")
	private String fileId;

	public InitiateRequestPojo() {
		super();
	}

	public InitiateRequestPojo(String processType) {
		super();
		this.processType = processType;
	}

}