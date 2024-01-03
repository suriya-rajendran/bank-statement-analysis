package com.bankstatement.analysis.perfios.response.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankAccounts{
    public BankAccount bankAccount;
}