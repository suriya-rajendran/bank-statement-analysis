package com.bankstatement.analysis.base.datamodel;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@MappedSuperclass
@Data
public class CategoryBaseModel {

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

	@Column(name = "category")
	public String category;

	@Column(name = "balance")
	public double balance;

	@PrePersist
	public void prePersist() {
		this.createdDate = new Timestamp(new Date().getTime());
	}
}