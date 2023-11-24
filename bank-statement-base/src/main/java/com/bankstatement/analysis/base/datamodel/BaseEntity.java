package com.bankstatement.analysis.base.datamodel;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@MappedSuperclass
@Data
public abstract class BaseEntity {

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	@Column(name = "id")
	@Setter(value = AccessLevel.PROTECTED)
	protected Long id;

	@Version
	@Column(name = "version")
	protected int version;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "IST")
	@JsonProperty("created_date")
	protected Timestamp createdDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "IST")
	@JsonProperty("updated_date")
	protected Timestamp updatedDate;

	@JsonProperty("web_ref_id")
	protected String webRefID;

	@PreUpdate
	public void preUpdate() {
		updatedDate = new Timestamp(new Date().getTime());
	}

	@PrePersist
	public void prePersist() {
		this.version = 1;
		this.webRefID = UUID.randomUUID().toString().replaceAll("-", "");
		this.createdDate = new Timestamp(new Date().getTime());
	}

}
