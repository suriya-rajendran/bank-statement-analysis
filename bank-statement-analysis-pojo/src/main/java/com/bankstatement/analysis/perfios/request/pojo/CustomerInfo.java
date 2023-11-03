package com.bankstatement.analysis.perfios.request.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerInfo{
	
    public String name;
    public String address;
    public String landline;
    public String mobile;
    public String email;
    public String pan;
    public String perfiosTransactionId;
    public String customerTransactionId;
    public String bank;
    public int instId;
    
}
