package com.bankstatement.analysis.base.util;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface AsyncThreeStepWorker<R, S, U, V> {

	@Transactional(propagation = Propagation.REQUIRED)
	public R prepareRequestAndSaveInTx();

	public U doIntegration(S request);

	@Transactional(propagation = Propagation.REQUIRED)
	public V doAfterIntegration(U integrationResponse);

	public S getRequest();

	public V getResponse();

}
