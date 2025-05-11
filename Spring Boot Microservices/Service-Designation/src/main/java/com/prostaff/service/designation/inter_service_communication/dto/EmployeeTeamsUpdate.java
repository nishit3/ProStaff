package com.prostaff.service.designation.inter_service_communication.dto;

import java.util.List;

import lombok.Data;

@Data
public class EmployeeTeamsUpdate {

	String employeeEmail;
	String adminEmail;
	List<String> teams;
}
