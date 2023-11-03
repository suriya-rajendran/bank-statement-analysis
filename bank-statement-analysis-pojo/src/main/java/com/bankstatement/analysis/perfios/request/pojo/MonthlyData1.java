package com.bankstatement.analysis.perfios.request.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonthlyData1{
	
    public String monthName;
    public double businessCreditToTotalCreditPercent;
    public double cashDepositToTotalCreditPercent;
    public double groupCreditToTotalCreditPercent;
    public double loanCreditToTotalCreditPercent;
    public double selfCreditToTotalCreditPercent;
    public double invIncomeToTotalCreditPercent;
    public double reversalCreditToTotalCreditPercent;
    public double cashWithdrawalToTotalDebitPercent;
    public double inwardChequeBouncePercent;
    public double outwardChequeBouncePercent;
    public double businessCredits;
    public double totalBusinessCredits;
    
}
