package com.bankstatement.analysis.perfios.response.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Status implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4539152446650903978L;
	public String txnId;
	public String parts;
	public String processing;
	public String files;
	@JsonProperty("Part")
	public Part[] part;

//	public List<Part> partList;
//
//	public List<Part> getPartList() {
//		return Arrays.asList(this.part);
//	}
}