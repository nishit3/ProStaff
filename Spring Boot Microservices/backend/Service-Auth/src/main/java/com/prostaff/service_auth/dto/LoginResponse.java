package com.prostaff.service_auth.dto;

import com.prostaff.service_auth.inter_service_communication.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
	
	String jwt;
	Role role;
	
}
