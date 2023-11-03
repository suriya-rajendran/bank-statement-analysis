package com.bankstatement.analysis.perfios.request.pojo;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BigDebitAfterSalaryXns {
	
	@JsonProperty("bigDebitAfterSalaryXns")
	private ArrayList<BigDebitAfterSalaryXn> bigDebitAfterSalaryXn;

}
