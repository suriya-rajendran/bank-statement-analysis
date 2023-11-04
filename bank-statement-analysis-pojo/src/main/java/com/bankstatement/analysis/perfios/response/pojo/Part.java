package com.bankstatement.analysis.perfios.response.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class Part implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3808173208934723647L;
	public String perfiosTransactionId;
	public String status;
	public String errorCode;
	public String reason;
}
