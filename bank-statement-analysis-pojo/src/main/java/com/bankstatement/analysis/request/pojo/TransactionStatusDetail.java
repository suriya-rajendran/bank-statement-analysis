package com.bankstatement.analysis.request.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class TransactionStatusDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8599718321814493517L;
	public String status;
	public String reason;
	public String errorCode;
	public String transactionId;
	public String reportStatus;

}