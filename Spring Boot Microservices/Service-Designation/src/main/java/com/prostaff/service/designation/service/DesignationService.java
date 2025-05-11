package com.prostaff.service.designation.service;

import java.util.List;

import com.prostaff.service.designation.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.designation.inter_service_communication.dto.DesignationBasicInformation;
import com.prostaff.service.designation.inter_service_communication.dto.EmployeeBasicInformation;
import com.prostaff.service.designation.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.designation.inter_service_communication.dto.NewDesignation;

public interface DesignationService {


	public List<String> getAllDesignationNames(AdminEmailWrapper adminEmailWrapper);

	public DesignationBasicInformation getEmployeeDesignation(EmployeeEmailWrapper employeeEmailWrapper);

	public List<EmployeeBasicInformation> getAllEmployees(Long id, AdminEmailWrapper adminEmailWrapper);

	public List<DesignationBasicInformation> getAllDesignations(AdminEmailWrapper adminEmailWrapper);

	public Boolean updateEmployees(Long id, List<String> employeelist);

	public Boolean updateCurrentDesignation(Long newDesignationId, EmployeeEmailWrapper employeeEmailWrapper);

	public Boolean addDesignation(NewDesignation newDesignation);

	public Boolean deleteDesignation(Long id, AdminEmailWrapper adminEmailWrapper);
	
	public Boolean deleteEmployee(EmployeeEmailWrapper employeeEmailWrapper);
	
	public Boolean addEmployeeToDesignation(Long id, EmployeeEmailWrapper employeeEmailWrapper);

}
