package com.bankstatement.analysis.base.datamodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Entity
@Table(name = "document")
@Data
@EqualsAndHashCode(callSuper = false)
public class Document extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5602266022431851955L;
	
	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	@Column(name = "id")
	@Setter(value = AccessLevel.PROTECTED)
	private Long id;

	@Column(name = "image_path")
	@JsonProperty("image_path")
	private String imagePath;

}