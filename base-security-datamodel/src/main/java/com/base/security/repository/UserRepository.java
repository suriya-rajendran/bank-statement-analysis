package com.base.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.base.security.datamodel.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String username);
}
