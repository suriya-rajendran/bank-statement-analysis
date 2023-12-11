package com.bankstatement.analysis.base.security.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.bankstatement.analysis.base.security.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet Filter implementation class StatelessLoginFilter
 */

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

	private final AuthenticationManager authenticationManager;

	private final JwtUtil jwtUtil;

	public LoginFilter(String urlMapping, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(urlMapping, "POST"));

	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		logger.info("attempt authentication login");

		String username = request.getParameter("username");

		Authentication authentication = new UsernamePasswordAuthenticationToken(username, username);

		return authenticationManager.authenticate(authentication);
		
//		return new UsernamePasswordAuthenticationToken(user.getUsername(), authentication.getCredentials(),
//				user.getAuthorities());
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {
		String token = jwtUtil.generateToken(request.getParameter("username"));

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		Map<String, String> responseBody = new HashMap<>();
		responseBody.put("Authorization", "Bearer " + token);

		response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));

	}
}