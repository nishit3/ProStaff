package com.prostaff.service.designation.inter_service_communication.dto;

import lombok.Data;

@Data
public class AuthRequest {
	
	String jwtToken;
	String path; 
	
}
