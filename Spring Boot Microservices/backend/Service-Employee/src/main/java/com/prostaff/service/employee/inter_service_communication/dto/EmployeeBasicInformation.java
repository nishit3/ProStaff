package com.prostaff.service.employee.inter_service_communication.dto;



import com.prostaff.service.employee.inter_service_communication.enums.Gender;

import lombok.Data;

// for tabular view of all employees for admin

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
