package com.bankstatement.analysis.request.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.List;

@Data
public class PagedData {
	public PagedData(List data, int recordsTotal, int recordsFiltered) {
		this.data = data;
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsFiltered;
	}

	@JsonProperty("data")
	private List data;

	@JsonProperty("recordsTotal")
	private int recordsTotal;

	@JsonProperty("recordsFiltered")
	private int recordsFiltered;
}