package com.bankstatement.analysis.perfios.request.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FCUAnalysis{
	
    public PossibleFraudIndicators possibleFraudIndicators;
    public BehaviouralTransactionalIndicators behaviouralTransactionalIndicators;
    
}
