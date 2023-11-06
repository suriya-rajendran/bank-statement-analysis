package com.bankstatement.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = { "com.bankstatement" }, exclude = {
		UserDetailsServiceAutoConfiguration.class })
@ComponentScan("com.bankstatement")
@EnableJpaRepositories(basePackages = "com.bankstatement")
@EntityScan(basePackages = "com.bankstatement")
@EnableJpaAuditing
@EnableAsync(proxyTargetClass = true)
@EnableScheduling
@Retryable
public class BankStatementAnalysisApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankStatementAnalysisApplication.class, args);
	}

}
