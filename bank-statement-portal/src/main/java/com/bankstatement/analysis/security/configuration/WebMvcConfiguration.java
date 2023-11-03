package com.bankstatement.analysis.security.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bankstatement.analysis.base.repo.ProductRepository;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

	@Autowired
	ProductRepository productRepository;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		Interceptor interceptor = new Interceptor(productRepository);
		List<String> pathPatterns = new ArrayList<>();

		pathPatterns.add("/rest/bank/**");
		pathPatterns.add("/rest/profile/**");

		registry.addInterceptor(interceptor).addPathPatterns(pathPatterns);
	}
}