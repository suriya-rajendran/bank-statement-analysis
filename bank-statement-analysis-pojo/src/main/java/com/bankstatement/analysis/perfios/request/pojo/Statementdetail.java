package com.bankstatement.analysis.perfios.request.pojo;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Statementdetail{
	
    public String fileName;
    public String statementStatus;
    public CustomerInfo customerInfo;
    public ArrayList<StatementAccount> statementAccounts;
    
}
