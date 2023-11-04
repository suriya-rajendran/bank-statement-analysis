package com.bankstatement.analysis.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bankstatement.analysis.request.pojo.CustomException;
import com.bankstatement.analysis.request.pojo.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {

		ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
		String errorCode = "INTERNAL_ERROR";
		String errorMessage = "An internal error occurred. Please contact support.";

		ErrorResponse errorResponse = new ErrorResponse(errorCode, errorMessage);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
