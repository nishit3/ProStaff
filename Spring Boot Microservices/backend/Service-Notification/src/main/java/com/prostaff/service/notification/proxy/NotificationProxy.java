package com.prostaff.service.notification.proxy;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationProxy {

	Long id;
	List<Long> departments;
	List<Long> designations;
	List<Long> teams;
	Date date;
	String message;
	
}
