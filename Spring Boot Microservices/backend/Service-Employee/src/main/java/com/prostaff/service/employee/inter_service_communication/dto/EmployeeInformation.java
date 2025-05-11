package com.prostaff.service.employee.inter_service_communication.dto;

import java.sql.Date;
import java.util.List;

import lombok.Data;

import com.prostaff.service.employee.inter_service_communication.enums.Gender;

// for employee information page

@Data
public class EmployeeInformation {
	
	String email;
	String fullName; 
	Gender gender; 
	List<String> skills; 
	String reportingManager; 
	String phoneNumber; 
	String Address; 
	Date dob; 
	byte[] profileImage; 
	Date joiningDate; 
	String emergencyContact; 
	BankDetail bankDetail; 
	Rating rating; 
	Integer age; 
	
	DepartmentBasicInformation department;
	DesignationBasicInformation designation; 
	List<TeamBasicInformation> teams;
	
}
