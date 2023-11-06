package com.bankstatement.analysis.perfios.response.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionStatusResponse implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2008597286919862061L;
	@JsonProperty("Status") 
    public Status status;
}

