package com.bankstatement.analysis.request.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
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
	
	private String expiry;
	
	private String status;

}