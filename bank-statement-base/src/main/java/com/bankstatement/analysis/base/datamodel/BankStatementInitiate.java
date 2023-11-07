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

	@Column(name = "application_ref_no")
	private String applicationReferenceNo;

	@Column(name = "application_date")
	private String applicationDate;

	@Column(name = "request_id")
	private String requestId;

	@Column(name = "product_code")
	private String productCode;

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
	private PENNYDROPSTATUS pennyDropStatus = PENNYDROPSTATUS.NO_PENNY_DROP;

	public enum PENNYDROPSTATUS {
		NO_PENNY_DROP, INITIATED, FAILED, COMPLETED, PENDING
	}

}