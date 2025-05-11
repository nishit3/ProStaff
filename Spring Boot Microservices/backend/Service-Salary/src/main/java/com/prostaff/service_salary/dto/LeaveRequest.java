package com.prostaff.service_salary.dto;

import java.sql.Date;

import com.prostaff.service_salary.dto.enums.LeaveRequestStatus;
import com.prostaff.service_salary.dto.enums.LeaveRequestType;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LeaveRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	
	String employee_email;
	Date requestDate;
	Date leaveDate;
	String reason;
	LeaveRequestStatus status;
	LeaveRequestType type;
}
