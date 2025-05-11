package com.prostaff.service_organization.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prostaff.service_organization.aggregator.HelpDetails;
import com.prostaff.service_organization.domain.FAQ;
import com.prostaff.service_organization.domain.UpcomingEvent;
import com.prostaff.service_organization.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service_organization.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service_organization.inter_service_communication.dto.OrganizationData;
import com.prostaff.service_organization.service.OrganizationService;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/organization")
@Tag(name = "OrganizationController")
public class OrganizationController {
	
	@Autowired
	private OrganizationService service;
	
	
	//image and name mate
	@PostMapping("/get-organization-data")
	public ResponseEntity<OrganizationData> getOrganizationData(@RequestBody EmployeeEmailWrapper wrapper)
	{	
		return new ResponseEntity<OrganizationData>(service.getOrganizationData(wrapper), null, 200);
	}
	
	@Hidden
	@PostMapping("/add-department/{organizationName}/{id}")
	public ResponseEntity<Boolean> addDepartment(@PathVariable String organizationName, @PathVariable Long id)
	{
		return new ResponseEntity<Boolean>(service.addDepartment(organizationName, id), null, 200);
	}
	
	@Hidden
	@PostMapping("/add-designation/{organizationName}/{id}")
	public ResponseEntity<Boolean> addDesignation(@PathVariable String organizationName, @PathVariable Long id)
	{
		return new ResponseEntity<Boolean>(service.addDesignation(organizationName, id), null, 200);
	}
	
	@Hidden
	@PostMapping("/add-team/{organizationName}/{id}")
	public ResponseEntity<Boolean> addTeam( @PathVariable String organizationName, @PathVariable Long id)
	{
		return new ResponseEntity<Boolean>(service.addTeam(organizationName, id), null, 200);
	}
	
	@Hidden
	@DeleteMapping("/remove-department/{organizationName}/{id}")
	public ResponseEntity<Boolean> removeDepartment(@PathVariable String organizationName,@PathVariable Long id)
	{
		return new ResponseEntity<Boolean>(service.removeDepartment(organizationName, id), null, 200);
	}
	
	@Hidden
	@DeleteMapping("/remove-designation/{organizationName}/{id}")
	public ResponseEntity<Boolean> removeDesignation(@PathVariable String organizationName,@PathVariable Long id)
	{
		return new ResponseEntity<Boolean>(service.removeDesignation(organizationName, id), null, 200);
	}
	
	@Hidden
	@DeleteMapping("/remove-team/{organizationName}/{id}")
	public ResponseEntity<Boolean> removeTeam(@PathVariable String organizationName, @PathVariable Long id)
	{
		return new ResponseEntity<Boolean>(service.removeTeam(organizationName, id), null, 200);
	}
	
	@Hidden
	@PostMapping("/add-employee/{organizationName}")
	public ResponseEntity<Boolean> addEmployee(@PathVariable String organizationName, @RequestBody EmployeeEmailWrapper employeeEmailWrapper)
	{
		return new ResponseEntity<Boolean>(service.addEmployee(organizationName, employeeEmailWrapper), null, 200);
	}
	
	@Hidden
	@GetMapping("/get-employees/{organizationName}")
	public ResponseEntity<List<String>> getAllEmployees(@PathVariable String organizationName) {
		return new ResponseEntity<List<String>>(service.getAllEmployees(organizationName), null, 200);
	}
	
	@Hidden
	@DeleteMapping("/remove-employee/{organizationName}")
	public ResponseEntity<Boolean> removeEmployee(@PathVariable String organizationName, @RequestBody EmployeeEmailWrapper employeeEmailWrapper)
	{
		return new ResponseEntity<Boolean>(service.removeEmployee(organizationName, employeeEmailWrapper), null, 200);
	}
	
	@Hidden
	@PostMapping("/add-notification/{organizationName}/{id}")
	public ResponseEntity<Boolean> addNotification(@PathVariable String organizationName, @PathVariable Long id)
	{
		return new ResponseEntity<Boolean>(service.addNotification(organizationName, id), null, 200);
	}
	
	@Hidden
	@DeleteMapping("/remove-notification/{organizationName}/{id}")
	public ResponseEntity<Boolean> removeNotification(@PathVariable String organizationName, @PathVariable Long id)
	{
		return new ResponseEntity<Boolean>(service.removeNotification(organizationName, id), null, 200);
	}
	
	@Hidden
	@PostMapping("/add-admin-log/{organizationName}/{id}")
	public ResponseEntity<Boolean> addAdminLog(@PathVariable String organizationName, @PathVariable Long id)
	{
		return new ResponseEntity<Boolean>(service.addAdminLog(organizationName, id), null, 200);
	}
	
	@Hidden
	@DeleteMapping("/remove-admin-log/{organizationName}/{id}")
	public ResponseEntity<Boolean> removeAdminLog(@PathVariable String organizationName, @PathVariable Long id)
	{
		return new ResponseEntity<Boolean>(service.removeAdminLog(organizationName, id), null, 200);
	}
	
	@Hidden
	@GetMapping("/get-departments/{organizationName}")
	public ResponseEntity<List<Long>> getAllDepartments(@PathVariable String organizationName)
	{
		return new ResponseEntity<List<Long>>(service.getAllDepartments(organizationName), null, 200);
	}
	
	@Hidden
	@GetMapping("/get-designations/{organizationName}")
	public ResponseEntity<List<Long>> getAllDesignations(@PathVariable String organizationName)
	{
		return new ResponseEntity<List<Long>>(service.getAllDesignations(organizationName), null, 200);
	}
	
	@Hidden
	@GetMapping("/get-teams/{organizationName}")
	public ResponseEntity<List<Long>> getAllteams(@PathVariable String organizationName)
	{
		return new ResponseEntity<List<Long>>(service.getAllteams(organizationName), null, 200);
	}
	
	@Hidden
	@GetMapping("/get-notifications/{organizationName}")
	public ResponseEntity<List<Long>> getAllNotifications(@PathVariable String organizationName)
	{
		return new ResponseEntity<List<Long>>(service.getAllNotifications(organizationName), null, 200);
	}
	
	@Hidden
	@GetMapping("/get-admin-logs/{organizationName}")
	public ResponseEntity<List<Long>> getAllAdminLogs(@PathVariable String organizationName)
	{
		return new ResponseEntity<List<Long>>(service.getAllAdminLogs(organizationName), null, 200);
	}
	
	
	@PostMapping("/get-faqs")
	public ResponseEntity<List<FAQ>> getAllFAQs(@RequestBody EmployeeEmailWrapper wrapper)
	{
		return new ResponseEntity<List<FAQ>>(service.getAllFAQs(wrapper), null, 200);
	}
	
	@PostMapping("/get-upcoming-events")
	public ResponseEntity<List<UpcomingEvent>> getAllUpcomingEvents(@RequestBody EmployeeEmailWrapper wrapper)
	{
		return new ResponseEntity<List<UpcomingEvent>>(service.getAllUpcomingEvents(wrapper), null, 200);
	}
	
	@PostMapping("/get-help-details")
	public ResponseEntity<HelpDetails> getHelpDetails(@RequestBody EmployeeEmailWrapper wrapper)
	{
		return new ResponseEntity<HelpDetails>(service.getHelpDetails(wrapper), null, 200);
	}
	
	@PutMapping("/update-help-details")
	public ResponseEntity<Boolean> updateHelpDetails(@RequestPart AdminEmailWrapper wrapper, @RequestPart HelpDetails updatedHelpDetails)
	{
		return new ResponseEntity<Boolean>(service.updateHelpDetails(wrapper, updatedHelpDetails), null, 200);
	}
	
	@DeleteMapping("/remove-faq/{id}")
	public ResponseEntity<Boolean> removeFAQ(@RequestBody AdminEmailWrapper wrapper, @PathVariable Long id)
	{
		return new ResponseEntity<Boolean>( service.removeFAQ(wrapper, id), null, 200);
	}
	
	@DeleteMapping("/remove-upcoming-event/{id}")
	public ResponseEntity<Boolean> removeUpcomingEvent(@RequestBody AdminEmailWrapper wrapper, @PathVariable Long id)
	{
		return new ResponseEntity<Boolean>(service.removeUpcomingEvent(wrapper, id), null, 200);
	}
	
	@Hidden
	@GetMapping("/is-organization-exist/{organizationName}")
	public ResponseEntity<Boolean> isOrganizationExists(@PathVariable String organizationName)
	{
		return new ResponseEntity<Boolean>(service.isOrganizationExists(organizationName), null, 200);
	}
	
	@PostMapping("/add-upcoming-event")
	public ResponseEntity<Boolean> addUpcomingEvent(@RequestPart AdminEmailWrapper wrapper, @RequestPart UpcomingEvent newUpcomingEvent)
	{
		return new ResponseEntity<Boolean>(service.addUpcomingEvent(wrapper, newUpcomingEvent), null, 200);
	}
	
	@PostMapping("/add-faq")
	public ResponseEntity<Boolean> addFAQ(@RequestPart AdminEmailWrapper wrapper, @RequestPart FAQ newFAQ) 
	{
		return new ResponseEntity<Boolean>(service.addFAQ(wrapper, newFAQ), null, 200);
	}
	
	@PutMapping("/update-upcoming-event")
	public ResponseEntity<Boolean> updateUpcomingEvent(@RequestPart AdminEmailWrapper wrapper, @RequestPart UpcomingEvent updatedUpcomingEvent)
	{
		return new ResponseEntity<Boolean>(service.updateUpcomingEvent(wrapper, updatedUpcomingEvent), null, 200);
	}
	
	@PutMapping("/update-faq")
	public ResponseEntity<Boolean> updateFAQ(@RequestPart AdminEmailWrapper wrapper, @RequestPart FAQ updatedFAQ) 
	{
		return new ResponseEntity<Boolean>(service.updateFAQ(wrapper, updatedFAQ), null, 200);
	}
	
	// to be strictly used only by devs when registering new organization
	@PostMapping("/register-organization/{organizationName}")
	@Hidden
	public ResponseEntity<Boolean> registerOrganization(@PathVariable String organizationName, @RequestPart MultipartFile organizationImage)
	{
		return new ResponseEntity<Boolean>(service.registerOrganization(organizationName, organizationImage), null, 200);
	}
	
}
