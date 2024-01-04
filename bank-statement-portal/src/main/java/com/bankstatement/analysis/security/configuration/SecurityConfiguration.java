package com.bankstatement.analysis.security.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bankstatement.analysis.base.security.jwt.JwtUtil;
import com.bankstatement.analysis.base.security.util.LoginFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	JwtUtil jwtUtil;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/**").permitAll().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//		http.addFilterBefore(new LoginFilter("/sign/in", jwtUtil, authenticationManager()),
//				UsernamePasswordAuthenticationFilter.class);

	}

}
