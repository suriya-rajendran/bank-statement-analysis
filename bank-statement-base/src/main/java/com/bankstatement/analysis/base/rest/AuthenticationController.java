package com.bankstatement.analysis.base.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bankstatement.analysis.base.security.jwt.JwtUtil;
import com.bankstatement.analysis.base.service.UserDetailsService;
import com.bankstatement.analysis.request.pojo.LoginRequest;

@RestController
public class AuthenticationController {

	@Autowired
	UserDetailsService userDetailsService;

	@PostMapping("/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest authenticationRequest) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        authenticationRequest.getUsername(),
//                        authenticationRequest.getPassword()
//                )
//        );

		return userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

	}
}
