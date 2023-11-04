package com.bankstatement.analysis.request.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InitiatedTask implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 979718782296930252L;

	private String processId;

	private String requestId;

	private String processType;

	private String requestType;

	private String name;

	private boolean nameMatch;

	private boolean pennyDropVerification;

	private String pennyDropStatus;

	private String status;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "IST")
	@JsonProperty("request_date")
	private Date requestDate;

	public InitiatedTask(String processId, String requestId, String processType, String requestType, String name,
			boolean nameMatch, boolean pennyDropVerification, String pennyDropStatus, String status, Date requestDate) {
		super();
		this.processId = processId;
		this.requestId = requestId;
		this.processType = processType;
		this.requestType = requestType;
		this.name = name;
		this.nameMatch = nameMatch;
		this.pennyDropVerification = pennyDropVerification;
		this.pennyDropStatus = pennyDropStatus;
		this.status = status;
		this.requestDate = requestDate;
	}
}
