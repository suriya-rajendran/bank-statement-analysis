package com.bankstatement.analysis.security.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bankstatement.analysis.base.repo.ProductRepository;
import com.bankstatement.analysis.base.security.jwt.JwtUtil;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

	@Autowired
	JwtUtil jwtUtil;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		Interceptor interceptor = new Interceptor(jwtUtil);

		List<String> pathPatterns = new ArrayList<>();
		pathPatterns.add("/login");

		pathPatterns.add("/uploadFile");

		registry.addInterceptor(interceptor).excludePathPatterns(pathPatterns);
		// .addPathPatterns(pathPatterns);
	}
}