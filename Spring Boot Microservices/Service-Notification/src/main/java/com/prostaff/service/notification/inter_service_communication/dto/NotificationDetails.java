package com.prostaff.service.notification.inter_service_communication.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class NotificationDetails {

	Long id;
	Date date;
	String message;
}
