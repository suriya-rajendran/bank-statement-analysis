package com.bankstatement.analysis.base.datamodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "bank_statement_report")
public class BankStatementReport extends BankStatementBaseModel implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -5880978694790469201L;

	@Column(name = "transaction_id")
	private String transactionId;

	@Lob
	@Column(name = "data")
	private byte[] data;
}
