package com.bankstatement.analysis.transaction.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class Xn implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5417663063379981858L;
	public String date;
	public String chqNo;
	public String narration;
	public double amount;
	public String category;
	public double balance;
}
