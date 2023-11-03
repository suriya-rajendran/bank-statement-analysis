package com.bankstatement.analysis.perfios.request.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryInfo{
	
    public String instName;
    public String accNo;
    public String accType;
    public int fullMonthCount;
    public Total total;
    public Average average;
    
}
