package com.prostaff.service_auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginRequest {
	
	String email; 
	String password;
	
}
