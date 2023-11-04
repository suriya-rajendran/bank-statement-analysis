package com.bankstatement.analysis.request.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class InitiateRequestPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9140553891726859613L;

	private String processType;

	private String yearMonthFrom;

	private String yearMonthTo;

	private String requestId;

	private String requestType;

	private String name;

	private boolean pennyDropVerification;

	private boolean nameMatch;

	private String processId;

	private String url;

}