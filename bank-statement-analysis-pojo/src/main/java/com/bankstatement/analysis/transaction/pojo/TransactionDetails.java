package com.bankstatement.analysis.transaction.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class TransactionDetails implements Serializable { 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8055012901911373108L; 
	public String date;
	public String chqNo;
	public String narration;
	public double amount;
	public String category;
	public double balance;
}
