package com.prostaff.service.department.service;

import java.util.List;

import com.prostaff.service.department.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.department.inter_service_communication.dto.DepartmentBasicInformation;
import com.prostaff.service.department.inter_service_communication.dto.EmployeeBasicInformation;
import com.prostaff.service.department.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.department.inter_service_communication.dto.NewDepartment;

public interface DepartmentService {

	
	public List<String> getAllDepartmentNames(AdminEmailWrapper adminEmailWrapper);

	public DepartmentBasicInformation getEmployeeDepartment(EmployeeEmailWrapper employeeEmailWrapper);

	public Boolean addDepartmanet(NewDepartment newDepartment);

	public List<EmployeeBasicInformation> getAllEmployees(Long id);

	public List<DepartmentBasicInformation> getAllDepartments(AdminEmailWrapper adminEmailWrapper);

	public Boolean updateEmployeesOfDepartment(Long departmentId, List<String> employeeEmails);

	public Boolean updateCurrentDepartmentOfEmployee(Long departmentId, EmployeeEmailWrapper employeeEmailWrapper);

	public Boolean deleteDepartment(Long departmentId, AdminEmailWrapper adminEmailWrapper);
	
	public Boolean deleteEmployee(EmployeeEmailWrapper employeeEmailWrapper);
	
	public Boolean addEmployeeToDepartment(Long id, EmployeeEmailWrapper employeeEmailWrapper);
	
}
