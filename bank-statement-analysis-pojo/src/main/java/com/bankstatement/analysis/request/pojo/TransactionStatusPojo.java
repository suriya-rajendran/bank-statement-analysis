package com.bankstatement.analysis.request.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class TransactionStatusPojo implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = -2511969524165345650L;

	@JsonProperty("initiate_request_pojo")
	private InitiateRequestPojo initiateRequestPojo;

	private String status;

	@JsonProperty("transaction_details")
	private List<TransactionStatusDetail> transactionDetails = new ArrayList<>();

}
