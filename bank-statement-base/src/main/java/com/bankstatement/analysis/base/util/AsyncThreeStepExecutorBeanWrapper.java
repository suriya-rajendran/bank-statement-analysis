package com.bankstatement.analysis.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AsyncThreeStepExecutorBeanWrapper<R, S, U, V> {
	
	Logger logger = LoggerFactory.getLogger(this.getClass().getName());


	@Async
	public void execute(AsyncThreeStepWorker<R, S, U, V> worker) {
		long startTime = System.currentTimeMillis();
		S request = worker.getRequest();
		logger.debug("Initiating Request:" + request);
		U integrationResponse = worker.doIntegration(request); 
		logger.info("Request {} took {} millseconds to complete.", request, System.currentTimeMillis() - startTime);
		V afterIntegrationResponse = worker.doAfterIntegration(integrationResponse);
		logger.debug("Completed Integration and Returning {} for request {}", afterIntegrationResponse, request);
	}

}
