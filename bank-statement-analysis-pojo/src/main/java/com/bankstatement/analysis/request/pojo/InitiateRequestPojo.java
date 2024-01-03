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

//	private String reportType;

	private String processType;

	private String yearMonthFrom;

	private String customerWebRefNo;

	private String applicationWebRefNo;
	
	private String tranWebRefNo;

	private String applicationDate;

	private String yearMonthTo;

	private String applicationRequestNo;

	private String requestType;

	private String name;

	private boolean pennyDropVerification;

	private boolean scannedDoc;

	private String processId;

	private String url;

	private String expiry;

	private String status;

	private String fileName;

	private String institutionType;
	
	private String transactionId;

	private String fileId;

	public InitiateRequestPojo() {
		super();
	}

	public InitiateRequestPojo(String processType) {
		super();
		this.processType = processType;
	}

}