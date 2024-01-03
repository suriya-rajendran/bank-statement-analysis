package com.bankstatement.analysis.perfios.response.pojo;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerfiosUploadPojo {

	private Long id;

	@JsonProperty("created_date")
	private Date createdDate;

	@JsonProperty("response_date")
	private Date responseDate;

	@JsonProperty("request")
	private String request;

	@JsonProperty("request_id")
	private String requestId;

	@JsonProperty("application_ref_no")
	private String applicationRefNo;

	@JsonProperty("customer_ref_no")
	private String customerRefNo;

	@JsonProperty("perfios_txn_id")
	private String perfiosTxnId;

	@JsonProperty("institution_id")
	private String institutionId;

	@JsonProperty("file_id")
	private String fileId;

	@JsonProperty("file_name")
	private String fileName;

	@JsonProperty("txn_status")
	private String txnStatus;

	@JsonProperty("product")
	private String product;

	@JsonProperty("response")
	private String response;

	@JsonProperty("xl_file_name")
	private String xlFileName;
}