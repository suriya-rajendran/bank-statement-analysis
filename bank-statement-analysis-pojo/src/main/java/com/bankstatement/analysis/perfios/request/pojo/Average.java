package com.bankstatement.analysis.perfios.request.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Average{
	
    public double balAvg;
    public double balMax;
    public double balMin;
    public int cashDeposits;
    public int cashWithdrawals;
    public int chqDeposits;
    public int chqIssues;
    public int credits;
    public int creditsAPS;
    public int creditsRev;
    public int creditsSC;
    public int creditsSelf;
    public int debits;
    public int debitsAPS;
    public int debitsSC;
    public int debitsSelf;
    public double dpLimit;
    public int invExpenses;
    public int invIncomes;
    public int inwBounces;
    public int inwECSBounces;
    public int inwEMIBounces;
    public int loanDisbursals;
    public int outwBounces;
    public int outwChqBounces;
    public int outwECSBounces;
    public int overdrawnDays;
    public int overdrawnInstances;
    public int salaries;
    public double snLimit;
    public double totalCashDeposit;
    public double totalCashWithdrawal;
    public double totalChqDeposit;
    public double totalChqIssue;
    public double totalCredit;
    public double totalCreditAPS;
    public double totalCreditRev;
    public double totalCreditSC;
    public double totalCreditSelf;
    public double totalDebit;
    public double totalDebitAPS;
    public double totalDebitSC;
    public double totalDebitSelf;
    public double totalInvExpense;
    public double totalInvIncome;
    public double totalInwBounce;
    public double totalInwECSBounce;
    public double totalInwEMIBounce;
    public double totalLoanDisbursal;
    public double totalOutwChqBounce;
    public double totalOutwECSBounce;
    public double totalSalary;
    
}
