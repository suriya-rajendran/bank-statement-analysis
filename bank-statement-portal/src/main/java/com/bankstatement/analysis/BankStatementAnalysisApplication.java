package com.bankstatement.analysis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.bankstatement.analysis.base.util.UploadFile;

@SpringBootApplication(scanBasePackages = { "com" }, exclude = { UserDetailsServiceAutoConfiguration.class })
@ComponentScan("com")
@EnableJpaRepositories(basePackages = "com")
@EntityScan(basePackages = "com")
@EnableJpaAuditing
@EnableAsync(proxyTargetClass = true)
@EnableScheduling
@Retryable
public class BankStatementAnalysisApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankStatementAnalysisApplication.class, args);
	}

	@Bean
	public ServletRegistrationBean<UploadFile> uploadFile(
			@Value("${proofdocuments.upload_file_path:/media/sf_workspace/analyser}") String rootPath,
			@Value("${upload.whitelist.ext:.pdf}") String allowedExtensions) {
		return new ServletRegistrationBean<UploadFile>(new UploadFile(rootPath, allowedExtensions), "/uploadfile");
	}
}
