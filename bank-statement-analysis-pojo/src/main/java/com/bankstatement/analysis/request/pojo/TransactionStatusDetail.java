package com.bankstatement.analysis.request.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class TransactionStatusDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8599718321814493517L;
	public String status;
	public String reason;
	@JsonProperty("error_code")
	public String errorCode;
	@JsonProperty("transaction_id")
	public String transactionId;
	@JsonProperty("report_status")
	public String reportStatus;

}