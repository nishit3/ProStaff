package com.prostaff.service_organization.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.prostaff.service_organization.aggregator.HelpDetails;
import com.prostaff.service_organization.domain.FAQ;
import com.prostaff.service_organization.domain.UpcomingEvent;
import com.prostaff.service_organization.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service_organization.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service_organization.inter_service_communication.dto.OrganizationData;

public interface OrganizationService {
	
	public OrganizationData getOrganizationData(EmployeeEmailWrapper wrapper);
	public Boolean addDepartment(String organizationName, Long id);
	public Boolean addDesignation(String organizationName, Long id);
	public Boolean addTeam(String organizationName, Long id);
	public Boolean removeDepartment(String organizationName, Long id);
	public Boolean removeDesignation(String organizationName, Long id);
	public Boolean removeTeam(String organizationName, Long id);
	public Boolean addEmployee(String organizationName, EmployeeEmailWrapper employeeEmailWrapper);
	public Boolean removeEmployee(String organizationName, EmployeeEmailWrapper employeeEmailWrapper);
	public Boolean addNotification(String organizationName, Long id);
	public Boolean removeNotification(String organizationName, Long id);
	public Boolean addAdminLog(String organizationName, Long id);
	public Boolean removeAdminLog(String organizationName, Long id);
	public List<Long> getAllDepartments(String organizationName);
	public List<Long> getAllDesignations(String organizationName);
	public List<Long> getAllteams(String organizationName);
	public List<Long> getAllNotifications(String organizationName);
	public List<String> getAllEmployees(String organizationName);
	public List<Long> getAllAdminLogs(String organizationName);
	public List<FAQ> getAllFAQs(EmployeeEmailWrapper wrapper);
	public List<UpcomingEvent> getAllUpcomingEvents(EmployeeEmailWrapper wrapper);
	public HelpDetails getHelpDetails(EmployeeEmailWrapper wrapper);
	public Boolean updateHelpDetails(AdminEmailWrapper wrapper, HelpDetails newHelpDetails);
	public Boolean removeFAQ(AdminEmailWrapper wrapper, Long id);
	public Boolean removeUpcomingEvent(AdminEmailWrapper wrapper, Long id);
	public Boolean isOrganizationExists(String organizationName);
	public Boolean registerOrganization(String organizationName, MultipartFile organizationImage);
	public Boolean addUpcomingEvent(AdminEmailWrapper wrapper, UpcomingEvent newUpcomingEvent);
	public Boolean updateUpcomingEvent(AdminEmailWrapper wrapper , UpcomingEvent updatedUpcomingEvent);
	public Boolean addFAQ(AdminEmailWrapper wrapper, FAQ newFAQ);
	public Boolean updateFAQ(AdminEmailWrapper wrapper, FAQ updatedFAQ);
	
	
}
