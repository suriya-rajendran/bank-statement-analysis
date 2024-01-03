package com.bankstatement.analysis.base.datamodel;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "bank_statement_initiate")
@DynamicUpdate
@DynamicInsert
@SelectBeforeUpdate
public class BankStatementInitiate extends BankStatementBaseModel implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -4464469374695222538L;

	@Column(name = "cust_web_no")
	private String custWebNo;

	@Column(name = "cust_trans_web_no")
	private String docWebNo;

	@Column(name = "application_date")
	private String applicationDate;

	@Column(name = "request_id")
	private String requestId;

	@Column(name = "transaction_id")
	private String transactionId;

	@Column(name = "request_type")
	private String requestType;

	@Column(name = "institution_type")
	private String institutionType;
	
	@Column(name = "scanned_doc")
	private boolean scannedDoc;

	@Column(name = "penny_drop_status")
	@Enumerated(EnumType.STRING)
	private PENNYDROPSTATUS pennyDropStatus = PENNYDROPSTATUS.NO_PENNY_DROP;

	public enum PENNYDROPSTATUS {
		NO_PENNY_DROP, INITIATED, FAILED, COMPLETED, PENDING
	}

}