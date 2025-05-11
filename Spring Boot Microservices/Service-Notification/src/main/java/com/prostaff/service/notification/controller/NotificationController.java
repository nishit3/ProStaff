package com.prostaff.service.notification.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prostaff.service.notification.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.notification.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.notification.inter_service_communication.dto.NewNotification;
import com.prostaff.service.notification.inter_service_communication.dto.NotificationDetails;
import com.prostaff.service.notification.inter_service_communication.dto.NotificationFilter;
import com.prostaff.service.notification.service.NotificationService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/notification")
@Tag(name = "NotificationController")
public class NotificationController {

	@Autowired
	private NotificationService notificationService;
	
	@PostMapping("/add-notification")
	public ResponseEntity<Boolean> addNotification(@RequestBody NewNotification newNotification){
		return new ResponseEntity<Boolean>(notificationService.addNotification(newNotification),HttpStatus.OK);
	}
	
	@DeleteMapping("/deleteNotification/{id}")
	public ResponseEntity<Boolean> deleteNotification(@PathVariable Long id,@RequestBody AdminEmailWrapper adminEmailWrapper){
		return new ResponseEntity<Boolean>(notificationService.deleteNotification(id, adminEmailWrapper),HttpStatus.OK);
	}

	@PostMapping("/get-emplyoyee-notifications")
	public ResponseEntity<List<NotificationDetails>> getEmployeeNotifications(@RequestBody EmployeeEmailWrapper employeeEmailWrapper){
		return new ResponseEntity<List<NotificationDetails>>(notificationService.getEmployeeNotification(employeeEmailWrapper),HttpStatus.OK);
	}
	
	@PostMapping("/get-all-notifications")
	public ResponseEntity<List<NotificationDetails>> getAllNotifications(@RequestBody AdminEmailWrapper adminEmailWrapper){
		return new ResponseEntity<List<NotificationDetails>>(notificationService.getAllNotifications(adminEmailWrapper),HttpStatus.OK);
	}

	@PostMapping("/get-filtered-notifications")
	public ResponseEntity<List<NotificationDetails>> getFilteredNotifications(@RequestBody NotificationFilter notificationFilter){
		return new ResponseEntity<List<NotificationDetails>>(notificationService.getFilteredNotifications(notificationFilter),HttpStatus.OK);
	}
	
}
