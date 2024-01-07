package com.bankstatement.perfios.impl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;
import com.bankstatement.analysis.base.service.BankStatementImpl;
import com.bankstatement.analysis.request.pojo.InitiateRequestPojo;

@Aspect
@Component
public class PerfiosAspect {

	public final static Logger logger = LoggerFactory.getLogger(PerfiosAspect.class);

	@Autowired
	PerifiosBankStatementServiceImpl perifiosBankStatementServiceImpl;

	@Autowired
	BankStatementImpl bankStatementImpl;

	@Around("execution(* com.bankstatement.perfios.async.AsyncBankStatementService.generateUploadInitiateResponse(..))")
	public void aroundGenerateUploadInitiateResponse(ProceedingJoinPoint joinPoint) throws Throwable {
		logger.info(
				"execution(* com.bankstatement.perfios.async.AsyncBankStatementService.generateUploadInitiateResponse(..))");
		Object result = joinPoint.proceed();
		if (result instanceof InitiateRequestPojo) {
			InitiateRequestPojo initiateRequestPojo = (InitiateRequestPojo) result;

			BankStatementInitiate bankStatementInitiate = bankStatementImpl
					.getBankStatementInitiateByProcessId(initiateRequestPojo.getProcessId());
			if (bankStatementInitiate != null) {
				logger.info("bankStatementInitiate RequestType  : {}",bankStatementInitiate.getRequestType());
				if ("UPLOAD".equalsIgnoreCase(bankStatementInitiate.getRequestType())
						&& !bankStatementInitiate.isScannedDoc())
					perifiosBankStatementServiceImpl.transactionStatus(bankStatementInitiate);
			}
		}

	}

	@Around("execution(* com.bankstatement.perfios.async.AsyncBankStatementService.initiateAsynReport(..))")
	public void aroundInitiateAsynReport(ProceedingJoinPoint joinPoint) throws Throwable {
		logger.info("execution(* com.bankstatement.perfios.async.AsyncBankStatementService.initiateAsynReport(..))");

		Object result = joinPoint.proceed();
		if (result instanceof InitiateRequestPojo) {
			InitiateRequestPojo initiateRequestPojo = (InitiateRequestPojo) result;

			if ("SUCCESS".equalsIgnoreCase(initiateRequestPojo.getStatus())) {
				perifiosBankStatementServiceImpl.constructFeature(initiateRequestPojo);
			}
		}
	}
}
