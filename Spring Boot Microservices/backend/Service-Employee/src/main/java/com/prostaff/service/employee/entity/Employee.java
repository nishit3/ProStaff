package com.prostaff.service.employee.entity;

import java.sql.Date;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.prostaff.service.employee.aggregators.BankDetail;
import com.prostaff.service.employee.aggregators.Rating;
import com.prostaff.service.employee.inter_service_communication.enums.Gender;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Employee {

	@Id
	String email;
	
	String fullName;
	
	@Enumerated(EnumType.STRING)
	Gender gender;
	
	@Lob
    @JdbcTypeCode(SqlTypes.JSON)
	List<String> skills;
	
	String reportingManager;
	
	String phoneNumber;
	
	String Address;
	
	Date dob;
	
	String profileImagePath; //(saved as email + extension)
	
	Date joiningDate;
	
	String emergencyContact;
	
	@Embedded
	BankDetail bankDetail;
	
	@Embedded
	Rating rating;
}
