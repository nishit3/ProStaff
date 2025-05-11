package com.prostaff.service.notification.service;

import java.util.List;

import com.prostaff.service.notification.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.notification.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.notification.inter_service_communication.dto.NewNotification;
import com.prostaff.service.notification.inter_service_communication.dto.NotificationDetails;
import com.prostaff.service.notification.inter_service_communication.dto.NotificationFilter;


public interface NotificationService {

	public Boolean addNotification(NewNotification newNotification);
	public Boolean deleteNotification(Long id,AdminEmailWrapper adminEmailWrapper);
	public List<NotificationDetails> getEmployeeNotification(EmployeeEmailWrapper employeeEmailWrapper);
	public List<NotificationDetails> getAllNotifications(AdminEmailWrapper adminEmailWrapper);
	public List<NotificationDetails> getFilteredNotifications(NotificationFilter notificationFilter);
}
