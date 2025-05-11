package com.prostaff.service_organization.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.prostaff.service_organization.aggregator.HelpDetails;
import com.prostaff.service_organization.domain.FAQ;
import com.prostaff.service_organization.domain.Organization;
import com.prostaff.service_organization.domain.UpcomingEvent;
import com.prostaff.service_organization.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service_organization.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service_organization.inter_service_communication.dto.NewLog;
import com.prostaff.service_organization.inter_service_communication.dto.OrganizationData;
import com.prostaff.service_organization.inter_service_communication.enums.LogType;
import com.prostaff.service_organization.repository.OrganizationRepository;
import com.prostaff.service_organization.service.OrganizationService;

@Service
public class OrganizationServiceImpl implements OrganizationService{

	@Autowired
	private OrganizationRepository db;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public OrganizationData getOrganizationData(EmployeeEmailWrapper wrapper) {
		
		OrganizationData organizationData = new OrganizationData();
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", wrapper, String.class);
		String organizationName = resp.getBody();
		
		Organization entity = db.findById(organizationName).get();
		
		File organization_image = new File("src/main/resources/static/organization_images/" + entity.getImagePath());
		
		try {
			organizationData.setImage(Files.readAllBytes(organization_image.toPath()));
		} catch (IOException e) {}
		
		organizationData.setOrganizationName(entity.getOrganizationName());
		
		return organizationData;
	}

	@Override
	public Boolean addDepartment(String organizationName, Long id) {
		
		Organization entity = db.findById(organizationName).get();
		entity.getDepartments().add(id);
		db.save(entity);
		return true;
	}

	@Override
	public Boolean addDesignation(String organizationName, Long id) {
		
		Organization entity = db.findById(organizationName).get();
		entity.getDesignations().add(id);
		db.save(entity);
		return true;
	}

	@Override
	public Boolean addTeam(String organizationName, Long id) {
		
		Organization entity = db.findById(organizationName).get();
		entity.getTeams().add(id);
		db.save(entity);
		return true;
	}

	@Override
	public Boolean removeDepartment(String organizationName, Long id) {
		
		Organization entity = db.findById(organizationName).get();
		entity.getDepartments().removeIf(dept -> dept.equals(id));
		db.save(entity);
		return true;
	}

	@Override
	public Boolean removeDesignation(String organizationName, Long id) {
		
		Organization entity = db.findById(organizationName).get();
		entity.getDesignations().removeIf(dept -> dept.equals(id));
		db.save(entity);
		return true;
	}

	@Override
	public Boolean removeTeam(String organizationName, Long id) {
		Organization entity = db.findById(organizationName).get();
		entity.getTeams().removeIf(dept -> dept.equals(id));
		db.save(entity);
		return true;
	}

	@Override
	public Boolean addEmployee(String organizationName, EmployeeEmailWrapper employeeEmailWrapper) {
		Organization entity = db.findById(organizationName).get();
		entity.getEmployees().add(employeeEmailWrapper.getEmployeeEmail());
		db.save(entity);
		return true;
	}

	@Override
	public Boolean removeEmployee(String organizationName, EmployeeEmailWrapper employeeEmailWrapper) {
		Organization entity = db.findById(organizationName).get();
		entity.getEmployees().remove(employeeEmailWrapper.getEmployeeEmail());
		db.save(entity);
		return true;
	}

	@Override
	public Boolean addNotification(String organizationName, Long id) {
		Organization entity = db.findById(organizationName).get();
		entity.getNotifications().add(id);
		db.save(entity);
		return true;
	}

	@Override
	public Boolean removeNotification(String organizationName, Long id) {
		
		Organization entity = db.findById(organizationName).get();
		entity.getNotifications().remove(id);
		db.save(entity);
		return true;
		
	}

	@Override
	public Boolean addAdminLog(String organizationName, Long id) {
		
		Organization entity = db.findById(organizationName).get();
		entity.getAdminLogs().add(id);
		db.save(entity);
		return true;
	}

	@Override
	public Boolean removeAdminLog(String organizationName, Long id) {
		
		Organization entity = db.findById(organizationName).get();
		entity.getAdminLogs().remove(id);
		db.save(entity);
		return true;
	}

	@Override
	public List<Long> getAllDepartments(String organizationName) {
		return db.findById(organizationName).get().getDepartments();
	}

	@Override
	public List<Long> getAllDesignations(String organizationName) {
		return db.findById(organizationName).get().getDesignations();
	}

	@Override
	public List<Long> getAllteams(String organizationName) {
		return db.findById(organizationName).get().getTeams();
	}

	@Override
	public List<Long> getAllNotifications(String organizationName) {
		return db.findById(organizationName).get().getNotifications();
	}

	@Override
	public List<Long> getAllAdminLogs(String organizationName) {
		return db.findById(organizationName).get().getAdminLogs();
	}

	@Override
	public List<FAQ> getAllFAQs(EmployeeEmailWrapper wrapper) {
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", wrapper, String.class);
		String organizationName = resp.getBody();
		return db.findById(organizationName).get().getFaqs();
	}

	@Override
	public List<UpcomingEvent> getAllUpcomingEvents(EmployeeEmailWrapper wrapper) {
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", wrapper, String.class);
		String organizationName = resp.getBody();
		return db.findById(organizationName).get().getUpcomingEvents();
	}

	@Override
	public HelpDetails getHelpDetails(EmployeeEmailWrapper wrapper) {
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", wrapper, String.class);
		String organizationName = resp.getBody();
		return db.findById(organizationName).get().getHelpDetails();
	}

	@Override
	public Boolean updateHelpDetails(AdminEmailWrapper wrapper, HelpDetails newHelpDetails) {
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(wrapper.getAdminEmail()), String.class);
		String organizationName = resp.getBody();
		Organization entity =  db.findById(organizationName).get();
		
		entity.getHelpDetails().setEmail(newHelpDetails.getEmail());
		entity.getHelpDetails().setPhoneNumber(newHelpDetails.getPhoneNumber());
		
		db.save(entity);
		return true;
	}

	@Override
	public Boolean removeFAQ(AdminEmailWrapper wrapper, Long id) {
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(wrapper.getAdminEmail()), String.class);
		String organizationName = resp.getBody();
		Organization entity = db.findById(organizationName).get();
		entity.getFaqs().removeIf(FAQ -> FAQ.getId().equals(id));
		db.save(entity);
		 
		NewLog log = new NewLog();
		log.setAdminEmail(wrapper.getAdminEmail());
		log.setType(LogType.FAQ_REMOVED);
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(wrapper.getAdminEmail()), String.class);
		String adminName = adminNameRE.getBody();
		log.setMessage("FAQ removed by Admin "+adminName+" "+"("+wrapper.getAdminEmail()+")");
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", log, Boolean.class);
		
		return true;
	}

	@Override
	public Boolean removeUpcomingEvent(AdminEmailWrapper wrapper, Long id) {
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(wrapper.getAdminEmail()), String.class);
		String organizationName = resp.getBody();
		Organization entity = db.findById(organizationName).get();
		entity.getUpcomingEvents().removeIf(event -> event.getId().equals(id));
		db.save(entity);
		
		NewLog log = new NewLog();
		log.setAdminEmail(wrapper.getAdminEmail());
		log.setType(LogType.UPCOMING_EVENT_REMOVED);
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(wrapper.getAdminEmail()), String.class);
		String adminName = adminNameRE.getBody();
		log.setMessage("Upcoming Event removed by Admin "+adminName+" "+"("+wrapper.getAdminEmail()+")");
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", log, Boolean.class);
		
		return true;
	}

	@Override
	public Boolean isOrganizationExists(String organizationName) {
		return db.existsById(organizationName);
	}

	@Override
	public Boolean registerOrganization(String organizationName, MultipartFile organizationImage) {
		
		Organization newOrg = new Organization();
		
		newOrg.setOrganizationName(organizationName);
		newOrg.setAdminLogs(new ArrayList<>());
		newOrg.setDepartments(new ArrayList<>());
		newOrg.setDesignations(new ArrayList<>());
		newOrg.setEmployees(new ArrayList<>());
		newOrg.setFaqs(new ArrayList<>());
		newOrg.setTeams(new ArrayList<>());
		newOrg.setUpcomingEvents(new ArrayList<>());
		newOrg.setNotifications(new ArrayList<>());
		
		String img_name = organizationName +"."+ organizationImage.getContentType().substring(organizationImage.getContentType().lastIndexOf("/") + 1);
		File img = new File("src/main/resources/static/organization_images/" + img_name);
		
		try {
			Files.copy(organizationImage.getInputStream(), img.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {return false;}
		
		newOrg.setImagePath(img_name);
		
		HelpDetails helpDetails = new HelpDetails();
		helpDetails.setEmail("-");
		helpDetails.setPhoneNumber("-");
		newOrg.setHelpDetails(helpDetails);
		
		db.save(newOrg);
		return true;
	}

	@Override
	public Boolean addUpcomingEvent(AdminEmailWrapper wrapper, UpcomingEvent newUpcomingEvent) {
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(wrapper.getAdminEmail()), String.class);
		String organizationName = resp.getBody();
		Organization entity = db.findById(organizationName).get();
		entity.getUpcomingEvents().add(newUpcomingEvent);
		db.save(entity);
		
		NewLog log = new NewLog();
		log.setAdminEmail(wrapper.getAdminEmail());
		log.setType(LogType.UPCOMING_EVENT_ADDED);
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(wrapper.getAdminEmail()), String.class);
		String adminName = adminNameRE.getBody();
		log.setMessage("Upcoming Event added by Admin "+adminName+" "+"("+wrapper.getAdminEmail()+")");
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", log, Boolean.class);
		
		return true;
	}

	@Override
	public Boolean addFAQ(AdminEmailWrapper wrapper, FAQ newFAQ) {
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(wrapper.getAdminEmail()), String.class);
		String organizationName = resp.getBody();
		Organization entity = db.findById(organizationName).get();
		entity.getFaqs().add(newFAQ);
		db.save(entity);
		
		NewLog log = new NewLog();
		log.setAdminEmail(wrapper.getAdminEmail());
		log.setType(LogType.FAQ_ADDED);
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(wrapper.getAdminEmail()), String.class);
		String adminName = adminNameRE.getBody();
		log.setMessage("FAQ added by Admin "+adminName+" "+"("+wrapper.getAdminEmail()+")");
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", log, Boolean.class);
		
		return true;
	}

	@Override
	public List<String> getAllEmployees(String organizationName) {
		return db.findById(organizationName).get().getEmployees();
	}

	
	@Override
	public Boolean updateUpcomingEvent(AdminEmailWrapper wrapper, UpcomingEvent updatedUpcomingEvent) {
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(wrapper.getAdminEmail()), String.class);
		String organizationName = resp.getBody();
		Organization entity = db.findById(organizationName).get();
		try {
			UpcomingEvent event = entity.getUpcomingEvents().stream().filter(x -> x.getId() == updatedUpcomingEvent.getId()).collect(Collectors.toList()).get(0);
			event.setDate(updatedUpcomingEvent.getDate());
			event.setDescription(updatedUpcomingEvent.getDescription());
			event.setName(updatedUpcomingEvent.getName());
			db.save(entity);
		} catch (Exception e) {return false;}
		return true;
	}

	@Override
	public Boolean updateFAQ(AdminEmailWrapper wrapper, FAQ updatedFAQ) {
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(wrapper.getAdminEmail()), String.class);
		String organizationName = resp.getBody();
		Organization entity = db.findById(organizationName).get();
		try {
			FAQ faq = entity.getFaqs().stream().filter(x -> x.getId() == updatedFAQ.getId()).collect(Collectors.toList()).get(0);
			faq.setAnswer(updatedFAQ.getAnswer());
			faq.setQuestion(updatedFAQ.getQuestion());
			db.save(entity);
		} catch (Exception e) {return false;}
		
		return true;
	}

}
