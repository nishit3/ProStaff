package com.prostaff.service.designation.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.prostaff.service.designation.entity.Designation;
import com.prostaff.service.designation.exception.DesignationAlreadyExistsException;
import com.prostaff.service.designation.exception.DesignationtNotFoundException;
import com.prostaff.service.designation.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.designation.inter_service_communication.dto.DesignationBasicInformation;
import com.prostaff.service.designation.inter_service_communication.dto.EmployeeBasicInformation;
import com.prostaff.service.designation.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.designation.inter_service_communication.dto.NewDesignation;
import com.prostaff.service.designation.inter_service_communication.dto.NewLog;
import com.prostaff.service.designation.inter_service_communication.enums.LogType;
import com.prostaff.service.designation.repository.DesignationRepo;
import com.prostaff.service.designation.service.DesignationService;

@Service
public class DesignationServiceImpl implements DesignationService{

	@Autowired
	private DesignationRepo designationRepo;
	
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public List<String> getAllDesignationNames(AdminEmailWrapper adminEmailWrapper) {
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmailWrapper.getAdminEmail()), String.class);
		String organizationName = resp.getBody();
		ResponseEntity<List<Long>> designationIdsRE = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-designations/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});
		ArrayList<String> toBeReturned = new ArrayList<>();
		for(Designation designation : designationRepo.findAllById(designationIdsRE.getBody()))
		{
			toBeReturned.add(designation.getName());
		}
		return toBeReturned;
		
	}

	@Override
	public DesignationBasicInformation getEmployeeDesignation(EmployeeEmailWrapper employeeEmailWrapper) {
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", employeeEmailWrapper, String.class);
		String organizationName = resp.getBody();
		ResponseEntity<List<Long>> designationIdsRE = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-designations/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});
		
		for(Designation designation : designationRepo.findAllById(designationIdsRE.getBody()))
		{
			if(designation.getEmployees().contains(employeeEmailWrapper.getEmployeeEmail()))
			{
				DesignationBasicInformation dbi = new DesignationBasicInformation();
				
				dbi.setDescription(designation.getDescription());
				dbi.setEmployeeCount(Integer.toUnsignedLong(designation.getEmployees().size()));
				dbi.setId(designation.getId());
				dbi.setName(designation.getName());
				return dbi;
			}
		}
		
		throw new DesignationtNotFoundException();
	}

	@Override
	public List<EmployeeBasicInformation> getAllEmployees(Long id, AdminEmailWrapper adminEmailWrapper) {
		
		if(!designationRepo.existsById(id)) throw new DesignationtNotFoundException();
		List<String> allEmployeeEmails = designationRepo.findById(id).get().getEmployees();
		ResponseEntity<List<EmployeeBasicInformation>> resp = restTemplate.exchange("http://SERVICE-EMPLOYEE/employee/get-employees-basic-information", HttpMethod.POST, new HttpEntity<List<String>>(allEmployeeEmails), new ParameterizedTypeReference<List<EmployeeBasicInformation>>(){});
		return resp.getBody();
	}

	@Override
	public List<DesignationBasicInformation> getAllDesignations(AdminEmailWrapper adminEmailWrapper) {
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmailWrapper.getAdminEmail()), String.class);
		String organizationName = resp.getBody();
		ResponseEntity<List<Long>> designationRE = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-designations/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});

		List<DesignationBasicInformation> toBeReturned = new ArrayList<>();
		
		for(Designation designation : designationRepo.findAllById(designationRE.getBody()))
		{
			DesignationBasicInformation dbi = new DesignationBasicInformation();
			
			dbi.setDescription(designation.getDescription());
			dbi.setEmployeeCount(Integer.toUnsignedLong(designation.getEmployees().size()));
			dbi.setId(designation.getId());
			dbi.setName(designation.getName());
			
			toBeReturned.add(dbi);
		}
		
		return toBeReturned;
	}

	@Override
	public Boolean updateEmployees(Long id, List<String> employeelist) {
		
		if(!designationRepo.existsById(id)) throw new DesignationtNotFoundException();
		
		Designation entity = designationRepo.findById(id).get();
		entity.setEmployees(employeelist);
		designationRepo.save(entity);
		
		return true;
	}

	@Override
	public Boolean updateCurrentDesignation(Long newDesignationId, EmployeeEmailWrapper employeeEmailWrapper) {
		
		if(!designationRepo.existsById(newDesignationId)) throw new DesignationtNotFoundException();
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", employeeEmailWrapper, String.class);
		String organizationName = resp.getBody();
		ResponseEntity<List<Long>> designationIdsRE = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-designations/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});
		
		for(Designation designation : designationRepo.findAllById(designationIdsRE.getBody()))
		{
			if(designation.getEmployees().contains(employeeEmailWrapper.getEmployeeEmail()))
			{
				designation.getEmployees().remove(employeeEmailWrapper.getEmployeeEmail());
				designationRepo.save(designation);
				break;
			}
		}
		
		Designation entity = designationRepo.findById(newDesignationId).get();
		entity.getEmployees().add(employeeEmailWrapper.getEmployeeEmail());
		designationRepo.save(entity);
		
		return true;
	}

	@Override
	public Boolean addDesignation(NewDesignation newDesignation) {
		
		String adminEmail = newDesignation.getAdminEmail();
		if(getAllDesignationNames(new AdminEmailWrapper(adminEmail)).contains(newDesignation.getName()))
		{
			throw new DesignationAlreadyExistsException();
		}
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		
		Designation entity = new Designation();
		entity.setDescription(newDesignation.getDescription());
		entity.setEmployees(new ArrayList<String>());
		entity.setName(newDesignation.getName());
		
		entity = designationRepo.save(entity);
		
		// register to organization service
		restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/add-designation/"+organizationName+"/"+entity.getId(), HttpMethod.POST, null, Boolean.class);
		 
		// add admin log
		NewLog designationAddedLog = new NewLog();
		designationAddedLog.setAdminEmail(adminEmail);
		designationAddedLog.setType(LogType.DESIGNATION_ADDED);
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(adminEmail), String.class);
		String adminName = adminNameRE.getBody();
		designationAddedLog.setMessage("Designation "+ entity.getName()+ " Added by Admin "+adminName+" "+"("+adminEmail+")");
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", designationAddedLog, Boolean.class);
		
		return true;
	}

	@Override
	public Boolean deleteDesignation(Long id, AdminEmailWrapper adminEmailWrapper) {
		
		if(!designationRepo.existsById(id)) throw new DesignationtNotFoundException();
		
		String designationName = designationRepo.findById(id).get().getName();
		designationRepo.deleteById(id);
		
		String adminEmail = adminEmailWrapper.getAdminEmail();
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		
		// notify organization service
		restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/remove-designation/"+organizationName+"/"+id, HttpMethod.DELETE, null, Boolean.class);
 
		// add admin log
		NewLog designationDeletedLog = new NewLog();
		designationDeletedLog.setAdminEmail(adminEmail);
		designationDeletedLog.setType(LogType.DESIGNATION_REMOVED);
		
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(adminEmail), String.class);
		String adminName = adminNameRE.getBody();
		designationDeletedLog.setMessage("Designation "+ designationName+ " Deleted by Admin "+adminName+" "+"("+adminEmail+")");
		
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", designationDeletedLog, Boolean.class);
		
		return true;
	}
	

	@Override
	public Boolean deleteEmployee(EmployeeEmailWrapper employeeEmailWrapper) {
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", employeeEmailWrapper, String.class);
		String organizationName = resp.getBody();
		ResponseEntity<List<Long>> designationIdsRE = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-designations/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});
		
		for(Designation designation : designationRepo.findAllById(designationIdsRE.getBody()))
		{
			if(designation.getEmployees().contains(employeeEmailWrapper.getEmployeeEmail()))
			{
				designation.getEmployees().remove(employeeEmailWrapper.getEmployeeEmail());
				designationRepo.save(designation);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public Boolean addEmployeeToDesignation(Long id, EmployeeEmailWrapper employeeEmailWrapper) {
		
		if(!designationRepo.existsById(id)) throw new DesignationtNotFoundException();
		
		Designation entity = designationRepo.findById(id).get();
		entity.getEmployees().add(employeeEmailWrapper.getEmployeeEmail());
		designationRepo.save(entity);
		
		return true;
	}

	
}
