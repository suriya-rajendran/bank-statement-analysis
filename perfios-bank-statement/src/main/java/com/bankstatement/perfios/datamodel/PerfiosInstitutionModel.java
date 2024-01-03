package com.bankstatement.perfios.datamodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Entity
@Table(name = "perfios_institutions")
@Data
public class PerfiosInstitutionModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4212262500896397095L;
	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;

	@Column(name = "institution_id")
	private String institutionId;

	@Column(name = "institution_type")
	private String institutionType;

	@Column(name = "institution_name")
	private String institutionName;

	@Column(name = "is_netbanking_linked")
	@JsonProperty("is_netbanking_linked")
	private boolean netBankingLinked;

}
