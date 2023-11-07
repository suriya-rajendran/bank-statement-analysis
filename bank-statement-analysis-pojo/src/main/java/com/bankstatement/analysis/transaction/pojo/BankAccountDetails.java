package com.bankstatement.analysis.transaction.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class BankAccountDetails implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8357550841196230229L;
	public String accountNo;
	public String accountType;
	public List<Xn> xns = new ArrayList<>();
}
