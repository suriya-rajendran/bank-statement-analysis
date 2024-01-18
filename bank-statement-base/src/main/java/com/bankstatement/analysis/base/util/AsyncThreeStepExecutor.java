package com.bankstatement.analysis.base.util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AsyncThreeStepExecutor {

	@Autowired
	ApplicationContext context;

	@Transactional(propagation = Propagation.REQUIRED)
	public <R, S, U, V> R execute(AsyncThreeStepWorker<R, S, U, V> worker) {
		R correlationId = worker.prepareRequestAndSaveInTx();

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@SuppressWarnings("unchecked")
			@Override
			public void afterCommit() {
				AsyncThreeStepExecutorBeanWrapper<R, S, U, V> asyncThreeStepExecutorBeanWrapper = context.getBean(AsyncThreeStepExecutorBeanWrapper.class);
				asyncThreeStepExecutorBeanWrapper.execute(worker);
			}
		});
		return correlationId;
	}

}