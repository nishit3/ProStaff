package com.prostaff.service.team.inter_service_communication.dto;

import java.util.List;

import lombok.Data;

@Data
public class EmployeeTeamsUpdate {

	String employeeEmail;
	String adminEmail;
	List<Long> teams;
}
