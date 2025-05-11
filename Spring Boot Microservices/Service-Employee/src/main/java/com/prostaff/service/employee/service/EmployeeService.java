package com.prostaff.service.employee.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.prostaff.service.employee.dto.EmployeeDetailsUpdate;
import com.prostaff.service.employee.dto.NewAdminDetails;
import com.prostaff.service.employee.dto.ResetPasswordTokenWrapper;
import com.prostaff.service.employee.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.employee.inter_service_communication.dto.EmployeeAdminEmailWrapper;
import com.prostaff.service.employee.inter_service_communication.dto.EmployeeBasicInformation;
import com.prostaff.service.employee.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.employee.inter_service_communication.dto.EmployeeInformation;
import com.prostaff.service.employee.inter_service_communication.dto.EmployeeRegistrationDetail;


public interface EmployeeService {

	public Boolean addEmployee(EmployeeRegistrationDetail employeeRegistrationDetail, MultipartFile employeeImage);
	public List<EmployeeBasicInformation> getEmployeesBasicInformation(List<String>employeeEmails);
	public List<EmployeeBasicInformation> getAllOrganizationEmployees(AdminEmailWrapper adminEmailWrapper);
	public EmployeeInformation getEmployeeInformation(EmployeeEmailWrapper employeeEmailWrapper);
	public Boolean updateEmployeeProfileImage(MultipartFile newImage, EmployeeEmailWrapper employeeEmailWrapper);
	public Boolean deleteEmployee(EmployeeAdminEmailWrapper employeeAdminEmailWrapper);
	public Boolean isEmployeeExist(EmployeeEmailWrapper employeeEmailWrapper);
	public Boolean updateEmployee(EmployeeDetailsUpdate employeeDetailsUpdate);
	// Used by Service-Auth
	public Boolean sendResetPasswordLink(ResetPasswordTokenWrapper passwordTokenWrapper);
	public Boolean sendAdminWelcomeMail(NewAdminDetails details);
}
