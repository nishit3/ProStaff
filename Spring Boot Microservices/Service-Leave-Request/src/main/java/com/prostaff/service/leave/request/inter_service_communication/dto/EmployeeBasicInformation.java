package com.prostaff.service.leave.request.inter_service_communication.dto;

import org.springframework.core.io.Resource;

import com.prostaff.service.leave.request.inter_service_communication.enums.Gender;

import lombok.Data;

@Data
public class EmployeeBasicInformation {
	
	String fullName; 
	String email; 
	Gender gender; 
	Integer age; 
	Resource profileImage; 
	String department; 
	String designation;
}
