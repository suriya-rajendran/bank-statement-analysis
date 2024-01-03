package com.bankstatement.analysis.perfios.response.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankAccount {
	public String accountId;
	public Object missingMonths;
	public int institutionId;
	public String accountType;
	public long accountNumber;
	public boolean complete;
}