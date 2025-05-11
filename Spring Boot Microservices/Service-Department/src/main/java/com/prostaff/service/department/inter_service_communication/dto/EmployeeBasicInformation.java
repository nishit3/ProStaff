package com.prostaff.service.department.inter_service_communication.dto;

import com.prostaff.service.department.inter_service_communication.enums.Gender;

import lombok.Data;

@Data
public class EmployeeBasicInformation {
	
	String fullName; 
	String email; 
	Gender gender; 
	Integer age; 
	byte[] profileImage; 
	String department; 
	String designation;
}
