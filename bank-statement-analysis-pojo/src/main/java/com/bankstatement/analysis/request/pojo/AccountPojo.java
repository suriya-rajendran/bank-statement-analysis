package com.bankstatement.analysis.request.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AccountPojo implements Serializable {
	/**
		 * 
		 */
	private static final long serialVersionUID = -3370501604321984531L;

	@JsonProperty("acc_web_ref_no")
	private String accWebRefNo;

	@JsonProperty("account_status")
	private ACCOUNT_STATUS_POJO accountStatus;

	public enum ACCOUNT_STATUS_POJO {
		INCLUDED, NOT_INCLUDED
	}

}
