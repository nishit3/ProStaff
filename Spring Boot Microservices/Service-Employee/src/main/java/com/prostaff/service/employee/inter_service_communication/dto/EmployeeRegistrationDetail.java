package com.prostaff.service.employee.inter_service_communication.dto;

import java.sql.Date;
import java.util.List;

import com.prostaff.service.employee.inter_service_communication.enums.Gender;

import lombok.Data;

@Data
public class EmployeeRegistrationDetail {

	String email;
	String fullName;
	
	String adminEmail;
	Long salary;
	Gender gender;
	List<String> skills;
	String reportingManager;
	String phoneNumber;
	String Address;
	Date dob;
	Date joiningDate;
	String emergencyContact;
	BankDetail bankDetail;
	Long department;
	Long designation;
	List<Long> teams;
	
}
