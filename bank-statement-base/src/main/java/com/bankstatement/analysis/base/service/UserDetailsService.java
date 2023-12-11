package com.bankstatement.analysis.base.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bankstatement.analysis.base.security.jwt.JwtUtil;
import com.bankstatement.analysis.request.pojo.CustomException;
import com.base.security.datamodel.User;
import com.base.security.repository.UserRepository;

@Service
public class UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	JwtUtil jwtUtil;

	public ResponseEntity<?> loadUserByUsername(String username) {

		User user = userRepository.findByUsername(username);

		if (user != null) {
			String token = jwtUtil.generateToken(user.getUsername());

			Map<String, String> responseBody = new HashMap<>();
			responseBody.put("Authorization", "Bearer " + token);

			return ResponseEntity.ok(responseBody);
		} else {
			throw new CustomException("500", "Invalid Username");
		}

	}
}
