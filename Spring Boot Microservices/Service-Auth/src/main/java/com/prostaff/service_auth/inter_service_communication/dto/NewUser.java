package com.prostaff.service_auth.inter_service_communication.dto;

import lombok.Data;

@Data
public class NewUser {
	
	String email; 
	String password;
	String organizationName; 
	String fullName;
}
