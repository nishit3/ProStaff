package com.prostaff.service.employee.inter_service_communication.dto;

import lombok.Data;

@Data
public class NewUser {
	
	String email; 
	String password;  
	String organizationName; 
	String fullName;
}
