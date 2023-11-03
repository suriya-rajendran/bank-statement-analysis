package com.bankstatement.analysis.perfios.request.pojo;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankStatementResponse{
	
    public CustomerInfo customerInfo;
    public ArrayList<Statementdetail> statementdetails;
    public ArrayList<AccountAnalysis> accountAnalysis;
    public ArrayList<CombinedAccountXn> combinedAccountXns;
    public ArrayList<CombinedMonthlyDetail> combinedMonthlyDetails;
    public ArrayList<CombinedEmiEcsLoanXn> combinedEmiEcsLoanXns;
    public ArrayList<CombinedInvIncomeXn> combinedInvIncomeXns;
    public ArrayList<AccountXn> accountXns;
    @JsonProperty("AdditionalMonthlyDetails") 
    public AdditionalMonthlyDetails additionalMonthlyDetails;
    
}
