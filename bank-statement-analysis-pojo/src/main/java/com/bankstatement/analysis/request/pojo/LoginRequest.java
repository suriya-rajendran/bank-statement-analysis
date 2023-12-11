package com.bankstatement.analysis.request.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class LoginRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3646950357678343855L;
	private String username;
	private String password;

}