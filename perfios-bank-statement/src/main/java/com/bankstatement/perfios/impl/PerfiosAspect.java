package com.bankstatement.perfios.impl;

import java.util.HashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;
import com.bankstatement.analysis.base.service.BankStatementImpl;
import com.bankstatement.analysis.request.pojo.InitiateRequestPojo;

@Aspect
@Component
public class PerfiosAspect {

	@Autowired
	PerifiosBankStatementServiceImpl perifiosBankStatementServiceImpl;

	@Autowired
	BankStatementImpl bankStatementImpl;

//	@Around("execution(* com.bankstatement.perfios.async.AsyncBankStatementService.generateUploadInitiateResponse(..))")
	public void aroundGenerateUploadInitiateResponse(ProceedingJoinPoint joinPoint) throws Throwable {

		Object result = joinPoint.proceed();
		if (result instanceof InitiateRequestPojo) {
			InitiateRequestPojo initiateRequestPojo = (InitiateRequestPojo) result;

			BankStatementInitiate bankStatementInitiate = bankStatementImpl
					.getBankStatementInitiateByProcessId(initiateRequestPojo.getProcessId());
			if (bankStatementInitiate != null) {
				perifiosBankStatementServiceImpl.transactionStatus(bankStatementInitiate);

			}

		}

	}

	@Around("execution(* com.bankstatement.perfios.async.AsyncBankStatementService.initiateAsynReport(..))")
	public void aroundInitiateAsynReport(ProceedingJoinPoint joinPoint) throws Throwable {

		Object result = joinPoint.proceed();
		if (result instanceof InitiateRequestPojo) {
			InitiateRequestPojo initiateRequestPojo = (InitiateRequestPojo) result;

			if ("SUCCESS".equalsIgnoreCase(initiateRequestPojo.getStatus())) {
				perifiosBankStatementServiceImpl.constructFeature(initiateRequestPojo);
			}
		}
	}
}
