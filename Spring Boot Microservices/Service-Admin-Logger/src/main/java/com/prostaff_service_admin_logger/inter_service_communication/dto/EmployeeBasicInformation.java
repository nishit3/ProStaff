package com.prostaff_service_admin_logger.inter_service_communication.dto;

import org.springframework.core.io.Resource;

import com.prostaff_service_admin_logger.inter_service_communication.enums.Gender;

public class EmployeeBasicInformation {
	
	String fullName; 
	String email; 
	Gender gender; 
	Integer age; 
	Resource profileImage; 
	String department; 
	String designation;
}
