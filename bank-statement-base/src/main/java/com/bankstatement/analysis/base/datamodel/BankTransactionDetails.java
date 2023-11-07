package com.bankstatement.analysis.base.datamodel;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "bank_transaction_details")
@DynamicUpdate
public class BankTransactionDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8927679960709318693L;

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	@Column(name = "id")
	@Setter(value = AccessLevel.PROTECTED)
	private Long id;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "IST")
	@JsonProperty("created_date")
	@Column(name = "created_date")
	@Setter(value = AccessLevel.NONE)
	private Timestamp createdDate;

	@Column(name = "date")
	public String date;

	@Column(name = "cheque_no")
	public String chqNo;

	@Column(name = "narration")
	public String narration;

	@Column(name = "amount")
	public double amount;

	@Column(name = "original_category")
	public String originalCategory;

	@Column(name = "balance")
	public double balance;

	@Column(name = "request_id")
	private String requestId;

	@Column(name = "category_type")
	@Enumerated(EnumType.STRING)
	private CATEGORY_TYPE categoryType;

	@Column(name = "category")
	public String category;

	public static enum CATEGORY_TYPE {
		INFLOW, OUTFLOW
	}

	@PrePersist
	public void prePersist() {
		this.createdDate = new Timestamp(new Date().getTime());
	}
}