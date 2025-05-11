package com.prostaff.service_auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prostaff.service_auth.domian.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
	
}
