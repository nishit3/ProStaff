package com.prostaff.service.employee.dto;

import java.sql.Date;
import java.util.List;


import com.prostaff.service.employee.inter_service_communication.enums.Gender;

import lombok.Data;

@Data
public class EmployeeDetailsUpdate {
	
	String email;
	String adminEmail;
	Gender gender; 
	List<String> skills; 
	String reportingManager; 
	String phoneNumber; 
	String address; 
	Date dob;
	Date joiningDate; 
	String emergencyContact; 
	BankDetail bankDetail; 
	Rating rating;
}
