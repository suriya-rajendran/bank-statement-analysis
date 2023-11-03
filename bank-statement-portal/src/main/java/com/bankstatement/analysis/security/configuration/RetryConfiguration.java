package com.bankstatement.analysis.security.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfiguration {

	@Value("${max.attempt:3}")
	private int maxAttempt;

	@Value("${initial.interval:5000}")
	private int inititalInterval;

	@Value("${interval.multiplier:2}")
	private int multiplier;

	@Value("${maximum.interval:50000}")
	private int maximumInterval;

	@Bean
	public RetryTemplate retryTemplate() {
		RetryTemplate retryTemplate = new RetryTemplate();

		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
		retryPolicy.setMaxAttempts(maxAttempt);

		retryTemplate.setThrowLastExceptionOnExhausted(true);

		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(inititalInterval);
		backOffPolicy.setMultiplier(multiplier);
		backOffPolicy.setMaxInterval(maximumInterval);
		retryTemplate.setBackOffPolicy(backOffPolicy);

		retryTemplate.setRetryPolicy(retryPolicy);

		return retryTemplate;
	}
}