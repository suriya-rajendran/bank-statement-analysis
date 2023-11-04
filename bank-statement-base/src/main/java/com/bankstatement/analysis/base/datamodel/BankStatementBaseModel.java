package com.bankstatement.analysis.base.datamodel;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@MappedSuperclass
@Data
public abstract class BankStatementBaseModel {

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	@Column(name = "id") 
	private Long id;

	@Version
	@Column(name = "version")
	@JsonIgnore
	public int version;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "IST")
	@JsonProperty("request_date")
	@Column(name = "request_date")
	@Setter(value = AccessLevel.NONE)
	private Timestamp requestDate;

	@JsonProperty("response_date")
	@Column(name = "response_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "IST")
	@Setter(value = AccessLevel.NONE)
	private Timestamp responseDate;

	@Lob
	@Column(name = "request", length = 80000)
	private String request;
	 
	@Column(name = "request_url")
	private String requestUrl;

	@Lob
	@Column(name = "response", length = 80000)
	private String response;

	@Column(name = "process_id")
	private String processId;

	@Column(name = "gst_status")
	@Enumerated(EnumType.STRING)
	private STATUS status;

	@PrePersist
	public void prePersist() {
		this.version = 1; 
		this.requestDate = new Timestamp(new Date().getTime());
	}

	public void setProcessId(String processId) {
		this.processId = processId+UUID.randomUUID().toString().replaceAll("-", "");
	}

	@PreUpdate
	public void preUpdate() {
		this.responseDate = new Timestamp(new Date().getTime());
	}

	public enum STATUS {
		INITIATED, FAILED, COMPLETED, PENDING
	}
}
