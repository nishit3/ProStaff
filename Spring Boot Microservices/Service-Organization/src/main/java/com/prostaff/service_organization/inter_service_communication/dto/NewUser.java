package com.prostaff.service_organization.inter_service_communication.dto;

import com.prostaff.service_organization.inter_service_communication.enums.Role;

public class NewUser {
	
	String email; 
	String password; 
	Role role; 
	String organizationName; 
	String fullName;
}
