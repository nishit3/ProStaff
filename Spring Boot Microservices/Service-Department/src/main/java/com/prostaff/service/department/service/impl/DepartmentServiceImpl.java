package com.prostaff.service.department.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.prostaff.service.department.entity.Department;
import com.prostaff.service.department.exception.DepartmentAlreadyExistsException;
import com.prostaff.service.department.exception.DepartmentNotFoundException;
import com.prostaff.service.department.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.department.inter_service_communication.dto.DepartmentBasicInformation;
import com.prostaff.service.department.inter_service_communication.dto.EmployeeBasicInformation;
import com.prostaff.service.department.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.department.inter_service_communication.dto.NewDepartment;
import com.prostaff.service.department.inter_service_communication.dto.NewLog;
import com.prostaff.service.department.inter_service_communication.enums.LogType;
import com.prostaff.service.department.repository.DepartmentRepo;
import com.prostaff.service.department.service.DepartmentService;



@Service
public class DepartmentServiceImpl implements DepartmentService{

	@Autowired
	private DepartmentRepo departmentRepo;
	
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public List<String> getAllDepartmentNames(AdminEmailWrapper adminEmailWrapper) {
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmailWrapper.getAdminEmail()), String.class);
		String organizationName = resp.getBody();
		ResponseEntity<List<Long>> departmentIdsRE = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-departments/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});
		ArrayList<String> toBeReturned = new ArrayList<>();
		for(Department department : departmentRepo.findAllById(departmentIdsRE.getBody()))
		{
			toBeReturned.add(department.getName());
		}
		return toBeReturned;
	}

	@Override
	public DepartmentBasicInformation getEmployeeDepartment(EmployeeEmailWrapper employeeEmailWrapper) {
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", employeeEmailWrapper, String.class);
		String organizationName = resp.getBody();
		ResponseEntity<List<Long>> departmentIdsRE = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-departments/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});
		
		for(Department department : departmentRepo.findAllById(departmentIdsRE.getBody()))
		{
			if(department.getEmployees().contains(employeeEmailWrapper.getEmployeeEmail()))
			{
				DepartmentBasicInformation dbi = new DepartmentBasicInformation();
				
				dbi.setDescription(department.getDescription());
				dbi.setEmployeeCount(Integer.toUnsignedLong(department.getEmployees().size()));
				dbi.setId(department.getId());
				dbi.setName(department.getName());
				return dbi;
			}
		}
		
		throw new DepartmentNotFoundException();
	}

	@Override
	public Boolean addDepartmanet(NewDepartment newDepartment) {
		
		String adminEmail = newDepartment.getAdminEmail();
		
		if(getAllDepartmentNames(new AdminEmailWrapper(adminEmail)).contains(newDepartment.getName()))
		{
			throw new DepartmentAlreadyExistsException();
		}
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		
		
		
		Department entity = new Department();
		entity.setDescription(newDepartment.getDescription());
		entity.setEmployees(new ArrayList<String>());
		entity.setName(newDepartment.getName());
		
		entity = departmentRepo.save(entity);
		
		// register to organization service
		restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/add-department/"+organizationName+"/"+entity.getId(), HttpMethod.POST, null, Boolean.class);
		
		// add admin log
		NewLog departmentAddedLog = new NewLog();
		departmentAddedLog.setAdminEmail(adminEmail);
		departmentAddedLog.setType(LogType.DEPARTMENT_ADDED);
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(adminEmail), String.class);
		String adminName = adminNameRE.getBody();
		departmentAddedLog.setMessage("Department "+ entity.getName()+ " Added by Admin "+adminName+" "+"("+adminEmail+")");
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", departmentAddedLog, Boolean.class);
		
		return true;
	}

	@Override
	public List<EmployeeBasicInformation> getAllEmployees(Long id) {
		
		if(!departmentRepo.existsById(id)) throw new DepartmentNotFoundException();
		
		List<String> allEmployeeEmails = departmentRepo.findById(id).get().getEmployees();
		
		ResponseEntity<List<EmployeeBasicInformation>> resp = restTemplate.exchange("http://SERVICE-EMPLOYEE/employee/get-employees-basic-information", HttpMethod.POST, new HttpEntity<List<String>>(allEmployeeEmails), new ParameterizedTypeReference<List<EmployeeBasicInformation>>(){});
		
		return resp.getBody();
	}

	@Override
	public List<DepartmentBasicInformation> getAllDepartments(AdminEmailWrapper adminEmailWrapper) {
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmailWrapper.getAdminEmail()), String.class);
		String organizationName = resp.getBody();
		ResponseEntity<List<Long>> departmentIdsRE = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-departments/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});

		List<DepartmentBasicInformation> toBeReturned = new ArrayList<>();
		
		for(Department department : departmentRepo.findAllById(departmentIdsRE.getBody()))
		{
			DepartmentBasicInformation dbi = new DepartmentBasicInformation();
			
			dbi.setDescription(department.getDescription());
			dbi.setEmployeeCount(Integer.toUnsignedLong(department.getEmployees().size()));
			dbi.setId(department.getId());
			dbi.setName(department.getName());
			
			toBeReturned.add(dbi);
		}
		
		return toBeReturned;
	}

	@Override
	public Boolean updateEmployeesOfDepartment(Long departmentId, List<String> employeeEmails) {
		
		if(!departmentRepo.existsById(departmentId)) throw new DepartmentNotFoundException();
		
		Department entity = departmentRepo.findById(departmentId).get();
		entity.setEmployees(employeeEmails);
		departmentRepo.save(entity);
		
		return true;
	}

	@Override
	public Boolean updateCurrentDepartmentOfEmployee(Long departmentId, EmployeeEmailWrapper employeeEmailWrapper) {
		
		if(!departmentRepo.existsById(departmentId)) throw new DepartmentNotFoundException();
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", employeeEmailWrapper, String.class);
		String organizationName = resp.getBody();
		ResponseEntity<List<Long>> departmentIdsRE = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-departments/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});
		
		for(Department department : departmentRepo.findAllById(departmentIdsRE.getBody()))
		{
			if(department.getEmployees().contains(employeeEmailWrapper.getEmployeeEmail()))
			{
				department.getEmployees().remove(employeeEmailWrapper.getEmployeeEmail());
				departmentRepo.save(department);
				break;
			}
		}
		
		Department entity = departmentRepo.findById(departmentId).get();
		entity.getEmployees().add(employeeEmailWrapper.getEmployeeEmail());
		departmentRepo.save(entity);
		
		return true;
	}

	@Override
	public Boolean deleteDepartment(Long departmentId, AdminEmailWrapper adminEmailWrapper) {
		
		if(!departmentRepo.existsById(departmentId)) throw new DepartmentNotFoundException();
		
		String departmentName = departmentRepo.findById(departmentId).get().getName();
		departmentRepo.deleteById(departmentId);
		
		String adminEmail = adminEmailWrapper.getAdminEmail();
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		
		// notify organization service
		restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/remove-department/"+organizationName+"/"+departmentId, HttpMethod.DELETE, null, Boolean.class);

		// add admin log
		NewLog departmentDeletedLog = new NewLog();
		departmentDeletedLog.setAdminEmail(adminEmail);
		departmentDeletedLog.setType(LogType.DEPARTMENT_REMOVED);
		
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(adminEmail), String.class);
		String adminName = adminNameRE.getBody();
		departmentDeletedLog.setMessage("Department "+ departmentName+ " Deleted by Admin "+adminName+" "+"("+adminEmail+")");
		
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", departmentDeletedLog, Boolean.class);
		
		return true;
	}

	@Override
	public Boolean deleteEmployee(EmployeeEmailWrapper employeeEmailWrapper) {
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", employeeEmailWrapper, String.class);
		String organizationName = resp.getBody();
		ResponseEntity<List<Long>> departmentIdsRE = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-departments/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});
		
		for(Department department : departmentRepo.findAllById(departmentIdsRE.getBody()))
		{
			if(department.getEmployees().contains(employeeEmailWrapper.getEmployeeEmail()))
			{
				department.getEmployees().remove(employeeEmailWrapper.getEmployeeEmail());
				departmentRepo.save(department);
				return true;
			}
		}
		
		return false;
	}

	@Override
	public Boolean addEmployeeToDepartment(Long id, EmployeeEmailWrapper employeeEmailWrapper) {
		
		if(!departmentRepo.existsById(id)) throw new DepartmentNotFoundException();
		
		Department entity = departmentRepo.findById(id).get();
		entity.getEmployees().add(employeeEmailWrapper.getEmployeeEmail());
		departmentRepo.save(entity);
		
		return true;
	}

	
}
