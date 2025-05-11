package com.prostaff.service.notification.inter_service_communication.dto;

import java.util.List;

import lombok.Data;

@Data
public class NotificationFilter {
	
	String adminEmail;
	List<Long> teams;
	List<Long> departments;
	List<Long> designations;
}
