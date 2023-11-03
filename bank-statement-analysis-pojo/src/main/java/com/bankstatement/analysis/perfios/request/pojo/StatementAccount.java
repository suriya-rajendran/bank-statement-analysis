package com.bankstatement.analysis.perfios.request.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatementAccount{
	
    public String accountNo;
    public String accountType;
    public String xnsStartDate;
    public String xnsEndDate;
    
}
