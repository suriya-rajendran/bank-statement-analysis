package com.bankstatement.analysis.perfios.request.pojo;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountAnalysis{
	
    public String accountNo;
    public String accountType;
    public SummaryInfo summaryInfo;
    public ArrayList<MonthlyDetail> monthlyDetails;
    public ArrayList<EODBalance> eODBalances;
    public ArrayList<Top5FundsReceived> top5FundsReceived;
    public ArrayList<Top5FundsTransferred> top5FundsTransferred;
    public FCUAnalysis fCUAnalysis;
    
}
