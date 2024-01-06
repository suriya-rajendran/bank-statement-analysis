package com.bankstatement.analysis.request.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomerTransactionPojo {

	private String status;

	@JsonProperty("request_type")
	private String type;

	@JsonProperty("scanned_doc")
	private boolean scannedDoc;

	@JsonProperty("institution_type")
	private String institutionType;

	@JsonProperty("web_ref_no")
	private String webRefNo;

	@JsonProperty("image_path")
	private String imagePath;

	@JsonProperty("account_details")
	private List<AccountPojo> accountPojo = new ArrayList<>();
}
