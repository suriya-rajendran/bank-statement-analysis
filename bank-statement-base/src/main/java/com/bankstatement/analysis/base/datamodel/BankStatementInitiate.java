package com.bankstatement.analysis.base.datamodel;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "bank_statement_initiate")
public class BankStatementInitiate extends BankStatementBaseModel implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -4464469374695222538L;

	@Column(name = "request_id")
	private String requestId;

	@Column(name = "process_type")
	private String processType;

	@Column(name = "request_type")
	private String requestType;

	@Column(name = "name")
	private String name;

	@Column(name = "name_match")
	private boolean nameMatch;

	@Column(name = "penny_drop_verification")
	private boolean pennyDropVerification;

	@Column(name = "penny_drop_status")
	@Enumerated(EnumType.STRING)
	private PENNYDROPSTATUS pennyDropStatus = PENNYDROPSTATUS.NOT_INITIATED;

	public enum PENNYDROPSTATUS {
		NOT_INITIATED, INITIATED, FAILED, COMPLETED, PENDING
	}

}