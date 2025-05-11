package com.prostaff_service_admin_logger.inter_service_communication.dto;

import java.sql.Date;
import java.util.List;
import com.prostaff_service_admin_logger.inter_service_communication.enums.Gender;

import org.springframework.core.io.Resource;

public class EmployeeInformation {
	
	String email;
	String fullName; 
	Gender gender; 
	List<String> skills; 
	String reportingManager; 
	String phoneNumber; 
	String Address; 
	Date dob; 
	Resource profileImage; 
	Date joiningDate; 
	String emergencyContact; 
	BankDetail bankDetail; 
	Rating rating; 
	Integer age; 
	String department; 
	String designation; 
	List<String> teams;
}

class BankDetail
{
	String bankName; 
	String ifscCode; 
	String accountNumber; 
	String branch;
}


class Rating
{
	Integer punctuality; 
	Integer performance; 
	Integer softSkills; 
	Integer creativity;
}
