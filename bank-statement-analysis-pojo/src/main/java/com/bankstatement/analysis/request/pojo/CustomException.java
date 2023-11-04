package com.bankstatement.analysis.request.pojo;

public class CustomException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4503839461748601315L;
	private String errorCode;
	private String errorMessage;

	public CustomException(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
