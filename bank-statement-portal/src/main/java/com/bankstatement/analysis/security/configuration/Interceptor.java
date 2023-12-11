package com.bankstatement.analysis.security.configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import com.bankstatement.analysis.base.security.jwt.JwtUtil;
import com.bankstatement.analysis.request.pojo.CustomException;

public class Interceptor implements HandlerInterceptor {

	private final JwtUtil jwtUtil;

	@Autowired
	public Interceptor(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		boolean valid = false;
		try {

			String token = extractTokenFromHeader(request.getHeader("Authorization"));

			// Validate the token
			if (jwtUtil.validateToken(token)) {
				valid = true;
			} else {
				throw new CustomException("400", "Invalid Token");
			}

		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			throw new Exception();
		}
		return valid;
	}

	private String extractTokenFromHeader(String header) {

		if (header != null && header.startsWith("Bearer ")) {
			return header.substring(7);
		}
		return null;
	}

}
