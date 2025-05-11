package com.prostaff.service.leave.request.inter_service_communication.dto;

import java.sql.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.prostaff.service.leave.request.inter_service_communication.enums.Gender;

import lombok.Data;

@Data
public class EmployeeRegistrationDetail {

	String email;
	String fullName;
	String adminEmail;
	String organizationName;
	Gender gender;
	List<String> skills;
	String reportingManager;
	String phoneNumber;
	String Address;
	Date dob;
	MultipartFile profileImage;
	Date joiningDate;
	String emergencyContact;
	BankDetail bankDetail;
	String department;
	String designation;
	List<String> teams;
}
