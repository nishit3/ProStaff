package com.prostaff.service.notification.service.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.prostaff.service.notification.entity.Notification;
import com.prostaff.service.notification.exception.NotificationNotFoundException;
import com.prostaff.service.notification.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.notification.inter_service_communication.dto.DepartmentBasicInformation;
import com.prostaff.service.notification.inter_service_communication.dto.DesignationBasicInformation;
import com.prostaff.service.notification.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.notification.inter_service_communication.dto.NewLog;
import com.prostaff.service.notification.inter_service_communication.dto.NewNotification;
import com.prostaff.service.notification.inter_service_communication.dto.NotificationDetails;
import com.prostaff.service.notification.inter_service_communication.dto.NotificationFilter;
import com.prostaff.service.notification.inter_service_communication.dto.TeamBasicInformation;
import com.prostaff.service.notification.inter_service_communication.enums.LogType;
import com.prostaff.service.notification.repository.NotificationRepository;
import com.prostaff.service.notification.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService{

	@Autowired
	private NotificationRepository db;
	
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public Boolean addNotification(NewNotification newNotification) {
		
		String adminEmail =  newNotification.getAdminEmail();
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		
		Notification entity = new Notification();
		entity.setDate(Date.valueOf(LocalDate.now()));
		entity.setDepartments(newNotification.getDepartments());
		entity.setDesignations(newNotification.getDesignations());
		entity.setTeams(newNotification.getTeams());
		entity.setMessage(newNotification.getMessage());
		
		entity = db.save(entity);
		
		restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/add-notification/"+organizationName+"/"+entity.getId(), HttpMethod.POST, null, Boolean.class);
		
		 
		NewLog notificationAdded = new NewLog();
		notificationAdded.setAdminEmail(adminEmail);
		notificationAdded.setType(LogType.NOTIFICATION_ADDED);
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(adminEmail), String.class);
		String adminName = adminNameRE.getBody();
		notificationAdded.setMessage("New Notification Added by Admin "+adminName+" "+"("+adminEmail+")");
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", notificationAdded, Boolean.class);
		
		return true;
	}

	@Override
	public Boolean deleteNotification(Long id, AdminEmailWrapper adminEmailWrapper) {
		
		if(!db.existsById(id)) throw new NotificationNotFoundException();
		
		String adminEmail = adminEmailWrapper.getAdminEmail();
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		
		db.deleteById(id);
		
		restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/remove-notification/"+organizationName+"/"+id, HttpMethod.DELETE, null, Boolean.class);
		
		NewLog notificationDeleted = new NewLog();
		notificationDeleted.setAdminEmail(adminEmail);
		notificationDeleted.setType(LogType.NOTIFICATION_REMOVED);
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(adminEmail), String.class);
		String adminName = adminNameRE.getBody();
		notificationDeleted.setMessage("Notification Deleted by Admin "+adminName+" "+"("+adminEmail+")");
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", notificationDeleted, Boolean.class);
		
		return true;
	}

	@Override
	public List<NotificationDetails> getEmployeeNotification(EmployeeEmailWrapper employeeEmailWrapper) {
		
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", employeeEmailWrapper, String.class);
		String organizationName = resp.getBody();
		
		List<NotificationDetails> toBeReturned = new ArrayList<>();
		List<Long> notifIds = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-notifications/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {}).getBody();
		
		
		Long departmentId = restTemplate.postForEntity("http://SERVICE-DEPARTMENT/department/get-employee-department", employeeEmailWrapper, DepartmentBasicInformation.class).getBody().getId();
		Long designationId = restTemplate.postForEntity("http://SERVICE-DESIGNATION/designation/get-employee-designation", employeeEmailWrapper, DesignationBasicInformation.class).getBody().getId();
				
		List<TeamBasicInformation> tbi = restTemplate.exchange("http://SERVICE-TEAM/team/get-employee-teams", HttpMethod.POST, new HttpEntity<EmployeeEmailWrapper>(employeeEmailWrapper), new ParameterizedTypeReference<List<TeamBasicInformation>>(){}).getBody();
		List<Long> teamIds = tbi.stream().map(x -> x.getId()).collect(Collectors.toList());
		
		for(Notification notification : db.findAllById(notifIds))
		{
			if(isRequested(notification, List.of(departmentId), List.of(designationId), teamIds))
			{
				NotificationDetails nd = new NotificationDetails();
				nd.setDate(notification.getDate());
				nd.setId(notification.getId());
				nd.setMessage(notification.getMessage());
				toBeReturned.add(nd);
			}
		}
		
		return toBeReturned;
	}

	@Override
	public List<NotificationDetails> getAllNotifications(AdminEmailWrapper adminEmailWrapper) {
		
		String adminEmail = adminEmailWrapper.getAdminEmail();
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		
		List<NotificationDetails> toBeReturned = new ArrayList<>();
		
		List<Long> notifIds = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-notifications/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {}).getBody();
		
		for(Notification notification : db.findAllById(notifIds))
		{
			NotificationDetails nd = new NotificationDetails();
			nd.setDate(notification.getDate());
			nd.setId(notification.getId());
			nd.setMessage(notification.getMessage());
			toBeReturned.add(nd);
		}
		
		return toBeReturned;
	}

	
	@Override
	public List<NotificationDetails> getFilteredNotifications(NotificationFilter notificationFilter) {
		
		String adminEmail = notificationFilter.getAdminEmail();
		
		List<Long> depts = notificationFilter.getDepartments();
		List<Long> desigs = notificationFilter.getDesignations();
		List<Long> teams = notificationFilter.getTeams();
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		
		List<NotificationDetails> toBeReturned = new ArrayList<>();
		
		List<Long> notifIds = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-notifications/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {}).getBody();
		
		for(Notification notification : db.findAllById(notifIds))
		{
			 if(isRequested(notification, depts, desigs, teams))
			{
				NotificationDetails nd = new NotificationDetails();
				nd.setDate(notification.getDate());
				nd.setId(notification.getId());
				nd.setMessage(notification.getMessage());
				toBeReturned.add(nd);
			}
		}
		
		return toBeReturned;
	}

	
	private boolean isRequested(Notification notif, List<Long> depts, List<Long> desigs, List<Long> teams)
	{
		for(Long dept : depts)
		{
			if(notif.getDepartments().contains(dept)) return true;
		}
		
		for(Long team : teams)
		{
			if(notif.getTeams().contains(team)) return true;
		}
		
		for(Long desig : desigs)
		{
			if(notif.getDesignations().contains(desig)) return true;
		}
		
		return false;
	}
}
