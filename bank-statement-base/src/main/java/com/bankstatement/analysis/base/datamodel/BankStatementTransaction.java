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
@Table(name = "bank_statement_transaction")
public class BankStatementTransaction extends BankStatementBaseModel implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -4464469374695222538L;


}