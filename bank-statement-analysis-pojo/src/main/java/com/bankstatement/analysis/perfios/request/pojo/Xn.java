package com.bankstatement.analysis.perfios.request.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Xn{
	
    public String date;
    public String chqNo;
    public String narration;
    public double amount;
    public String category;
    public double balance;
    
}
