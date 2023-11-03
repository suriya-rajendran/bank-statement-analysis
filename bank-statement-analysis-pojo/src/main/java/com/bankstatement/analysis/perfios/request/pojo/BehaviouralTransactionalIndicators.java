package com.bankstatement.analysis.perfios.request.pojo;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BehaviouralTransactionalIndicators{
	
	@JsonProperty("bigDebitAfterSalaryXns")
    public BigDebitAfterSalaryXns bigDebitAfterSalaryXns;
    public EqualCreditDebitXns equalCreditDebitXns;
    public ArrayList<IrregularSalaryCreditXn> irregularSalaryCreditXns;
}
