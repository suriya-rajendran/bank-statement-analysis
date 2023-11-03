package com.bankstatement.analysis.perfios.request.pojo;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdditionalMonthlyDetails{
	
    @JsonProperty("MonthlyData1") 
    public ArrayList<MonthlyData1> monthlyData1;
}
