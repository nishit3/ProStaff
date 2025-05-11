package com.prostaff.service.leave.request.entity;

import java.sql.Date;

import com.prostaff.service.leave.request.inter_service_communication.enums.LeaveRequestStatus;
import com.prostaff.service.leave.request.inter_service_communication.enums.LeaveRequestType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
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
