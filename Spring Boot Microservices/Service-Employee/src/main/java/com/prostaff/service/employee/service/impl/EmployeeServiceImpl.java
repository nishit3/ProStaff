package com.prostaff.service.employee.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.prostaff.service.employee.aggregators.BankDetail;
import com.prostaff.service.employee.aggregators.Rating;
import com.prostaff.service.employee.dto.EmployeeDetailsUpdate;
import com.prostaff.service.employee.dto.NewAdminDetails;
import com.prostaff.service.employee.dto.ResetPasswordTokenWrapper;
import com.prostaff.service.employee.entity.Employee;
import com.prostaff.service.employee.exception.EmployeeAlreadyExistsException;
import com.prostaff.service.employee.exception.EmployeeNotFoundException;
import com.prostaff.service.employee.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.employee.inter_service_communication.dto.DepartmentBasicInformation;
import com.prostaff.service.employee.inter_service_communication.dto.DesignationBasicInformation;
import com.prostaff.service.employee.inter_service_communication.dto.EmployeeAdminEmailWrapper;
import com.prostaff.service.employee.inter_service_communication.dto.EmployeeBasicInformation;
import com.prostaff.service.employee.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.employee.inter_service_communication.dto.EmployeeInformation;
import com.prostaff.service.employee.inter_service_communication.dto.EmployeeRegistrationDetail;
import com.prostaff.service.employee.inter_service_communication.dto.NewLog;
import com.prostaff.service.employee.inter_service_communication.dto.NewUser;
import com.prostaff.service.employee.inter_service_communication.dto.TeamBasicInformation;
import com.prostaff.service.employee.inter_service_communication.enums.LogType;
import com.prostaff.service.employee.repository.EmployeeRepo;
import com.prostaff.service.employee.service.EmployeeService;
import com.prostaff.service.employee.utils.EmailService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepo employeeRepo;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private BCryptPasswordEncoder encoder;

	@Override
	public Boolean addEmployee(EmployeeRegistrationDetail employeeRegistrationDetail, MultipartFile employeeImage) {
		
		if(employeeRepo.existsById(employeeRegistrationDetail.getEmail())) throw new EmployeeAlreadyExistsException();
		if(employeeRegistrationDetail.getSalary() < 0) return false; 
		
		Employee newEmployee = new Employee();
		
		BankDetail bankDetails = new BankDetail();
		bankDetails.setAccountNumber(employeeRegistrationDetail.getBankDetail().getAccountNumber());
		bankDetails.setBankName(employeeRegistrationDetail.getBankDetail().getBankName());
		bankDetails.setBranch(employeeRegistrationDetail.getBankDetail().getBranch());
		bankDetails.setIfscCode(employeeRegistrationDetail.getBankDetail().getIfscCode());
		
		newEmployee.setAddress(employeeRegistrationDetail.getAddress());
		newEmployee.setBankDetail(bankDetails);
		newEmployee.setDob(employeeRegistrationDetail.getDob());
		newEmployee.setEmail(employeeRegistrationDetail.getEmail());
		newEmployee.setEmergencyContact(employeeRegistrationDetail.getEmergencyContact());
		newEmployee.setFullName(employeeRegistrationDetail.getFullName());
		newEmployee.setGender(employeeRegistrationDetail.getGender());
		newEmployee.setJoiningDate(employeeRegistrationDetail.getJoiningDate());
		newEmployee.setPhoneNumber(employeeRegistrationDetail.getPhoneNumber());
		newEmployee.setReportingManager(employeeRegistrationDetail.getReportingManager());
		newEmployee.setSkills(employeeRegistrationDetail.getSkills());
		
		Rating rating = new Rating();
		rating.setCreativity(5);
		rating.setPerformance(5);
		rating.setPunctuality(5);
		rating.setSoftSkills(5);
		newEmployee.setRating(rating);
		
		
		String img_name = newEmployee.getEmail() +"."+ employeeImage.getContentType().substring(employeeImage.getContentType().lastIndexOf("/") + 1);
		File img = new File("src/main/resources/static/profile_images/" + img_name);
		try {
			Files.copy(employeeImage.getInputStream(), img.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {return false;}
		 
		newEmployee.setProfileImagePath(img_name);
		
		newEmployee = employeeRepo.save(newEmployee);
		
		String adminEmail =  employeeRegistrationDetail.getAdminEmail();

		
		EmployeeEmailWrapper wrapper = new EmployeeEmailWrapper();
		wrapper.setEmployeeEmail(adminEmail);
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", wrapper, String.class);
		String organizationName = resp.getBody();
		System.err.println("Employee ERROR:"+organizationName);
		
		// register with organization
		wrapper.setEmployeeEmail(newEmployee.getEmail());
		restTemplate.postForEntity("http://SERVICE-ORGANIZATION/organization/add-employee/"+organizationName, wrapper, Boolean.class);
		
		
		// register to Auth-Service
		NewUser authEntry = new NewUser();
		authEntry.setEmail(newEmployee.getEmail());
		authEntry.setFullName(newEmployee.getFullName());
		authEntry.setOrganizationName(organizationName);
		
		String randomPassword = generateRandomPassword();
		authEntry.setPassword(encoder.encode(randomPassword));
		restTemplate.postForEntity("http://SERVICE-AUTH/auth/add-user", authEntry, Boolean.class);
		
		System.err.println(authEntry.getEmail() + " " +randomPassword);
		
		
		// update teams, department, designation
		restTemplate.postForEntity("http://SERVICE-DEPARTMENT/department/add-employee-to-department/"+employeeRegistrationDetail.getDepartment(), wrapper, Boolean.class);
		restTemplate.postForEntity("http://SERVICE-DESIGNATION/designation/add-employee-to-designation/"+employeeRegistrationDetail.getDesignation(), wrapper, Boolean.class);
		for(Long teamId : employeeRegistrationDetail.getTeams())
		{
			restTemplate.postForEntity("http://SERVICE-TEAM/team/add-employee-to-team/"+teamId, wrapper, Boolean.class);
		}
		
		
		// create attendance object for new employee
		restTemplate.postForEntity("http://SERVICE-ATTENDANCE/attendance/register-employee", new EmployeeAdminEmailWrapper(newEmployee.getEmail(), adminEmail), Boolean.class);
		
		
		// create log
		
		NewLog employeeAddedLog = new NewLog();
		employeeAddedLog.setAdminEmail(adminEmail);
		employeeAddedLog.setType(LogType.EMPLOYEE_ADDED);
		wrapper.setEmployeeEmail(adminEmail);
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", wrapper, String.class);
		String adminName = adminNameRE.getBody();
		employeeAddedLog.setMessage(newEmployee.getFullName()+" ("+newEmployee.getEmail()+") Added by Admin "+adminName+" "+"("+adminEmail+")");
		
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", employeeAddedLog, Boolean.class);
		
		// create salary entry
		restTemplate.postForEntity("http://SERVICE-SALARY/salary/add-employee-salary/"+employeeRegistrationDetail.getSalary(), new EmployeeEmailWrapper(employeeRegistrationDetail.getEmail()), Boolean.class);
		
		// send welcome email with temporary password
		emailService.sendWelcomeEmail(newEmployee.getEmail(), newEmployee.getFullName(), randomPassword);
		
		return true;
	}

	@Override
	public List<EmployeeBasicInformation> getEmployeesBasicInformation(List<String> employeeEmails) {
		
		List<EmployeeBasicInformation> toBeReturned = new ArrayList<>();
		
		List<Employee> entities = employeeRepo.findAllById(employeeEmails);
		
		for(Employee entity : entities)
		{
			EmployeeBasicInformation ebi = new EmployeeBasicInformation();
			
			ebi.setAge(getAge(entity.getDob()));
			
			
			DepartmentBasicInformation deptBI = restTemplate.postForEntity("http://SERVICE-DEPARTMENT/department/get-employee-department", new EmployeeEmailWrapper(entity.getEmail()), DepartmentBasicInformation.class).getBody();
			DesignationBasicInformation desgBI = restTemplate.postForEntity("http://SERVICE-DESIGNATION/designation/get-employee-designation", new EmployeeEmailWrapper(entity.getEmail()), DesignationBasicInformation.class).getBody();
			
			ebi.setDepartment(deptBI.getName());
			ebi.setDesignation(desgBI.getName());
//			
			ebi.setEmail(entity.getEmail());
			ebi.setFullName(entity.getFullName());
			ebi.setGender(entity.getGender());
			
			File profile_image = new File("src/main/resources/static/profile_images/" + entity.getProfileImagePath());
			
			try {
				ebi.setProfileImage(Files.readAllBytes(profile_image.toPath()));
			} catch (IOException e) {return new ArrayList<>();}
	
			toBeReturned.add(ebi);
		}
		
		return toBeReturned;
	}

	@Override
	public EmployeeInformation getEmployeeInformation(EmployeeEmailWrapper employeeEmailWrapper) {
		if(!employeeRepo.existsById(employeeEmailWrapper.getEmployeeEmail())) throw new EmployeeNotFoundException();
		
		Employee entity = employeeRepo.findById(employeeEmailWrapper.getEmployeeEmail()).get();
		EmployeeInformation info = new EmployeeInformation();
		
		info.setAddress(entity.getAddress());
		info.setAge(getAge(entity.getDob()));
		info.setDob(entity.getDob());
		info.setEmail(entity.getEmail());
		info.setEmergencyContact(entity.getEmergencyContact());
		info.setFullName(entity.getFullName());
		info.setGender(entity.getGender());
		info.setJoiningDate(entity.getJoiningDate());
		info.setPhoneNumber(entity.getPhoneNumber());
		info.setReportingManager(entity.getReportingManager());
		info.setSkills(entity.getSkills());
		
		File profile_image = new File("src/main/resources/static/profile_images/" + entity.getProfileImagePath());
		try {
			info.setProfileImage(Files.readAllBytes(profile_image.toPath()));
		} catch (IOException e) {return null;}
		
		
		// fetch designation, department and teams information
		DepartmentBasicInformation deptBI = restTemplate.postForEntity("http://SERVICE-DEPARTMENT/department/get-employee-department", employeeEmailWrapper, DepartmentBasicInformation.class).getBody();
		info.setDepartment(deptBI);
		
		DesignationBasicInformation desgBI = restTemplate.postForEntity("http://SERVICE-DESIGNATION/designation/get-employee-designation", employeeEmailWrapper, DesignationBasicInformation.class).getBody();
		info.setDesignation(desgBI);
		
		List<TeamBasicInformation> tBI = restTemplate.exchange("http://SERVICE-TEAM/team/get-employee-teams", HttpMethod.POST, new HttpEntity<EmployeeEmailWrapper>(employeeEmailWrapper), new ParameterizedTypeReference<List<TeamBasicInformation>>(){}).getBody();
		info.setTeams(tBI);
		
		
		BankDetail bankDetail = entity.getBankDetail();
		com.prostaff.service.employee.inter_service_communication.dto.BankDetail bankDetail2 = new com.prostaff.service.employee.inter_service_communication.dto.BankDetail();
		bankDetail2.setAccountNumber(bankDetail.getAccountNumber());
		bankDetail2.setBankName(bankDetail.getBankName());
		bankDetail2.setBranch(bankDetail.getBranch());
		bankDetail2.setIfscCode(bankDetail.getIfscCode());
		info.setBankDetail(bankDetail2);
		
		
		Rating rating = entity.getRating();
		com.prostaff.service.employee.inter_service_communication.dto.Rating rating2 = new com.prostaff.service.employee.inter_service_communication.dto.Rating();
		rating2.setCreativity(rating.getCreativity());
		rating2.setPerformance(rating.getPerformance());
		rating2.setPunctuality(rating.getPunctuality());
		rating2.setSoftSkills(rating.getSoftSkills());
		info.setRating(rating2);
		
		return info;
	}

	@Override
	public Boolean updateEmployeeProfileImage(MultipartFile newImage, EmployeeEmailWrapper employeeEmailWrapper) {
		
		if(!employeeRepo.existsById(employeeEmailWrapper.getEmployeeEmail())) throw new EmployeeNotFoundException();
		
		Employee entity = employeeRepo.findById(employeeEmailWrapper.getEmployeeEmail()).get();
		File img = new File("src/main/resources/static/profile_images/" + entity.getProfileImagePath());
		img.delete();
		
		String img_name = entity.getEmail() +"."+ newImage.getContentType().substring(newImage.getContentType().lastIndexOf("/") + 1);
		img = new File("src/main/resources/static/profile_images/" + img_name);
		try {
			Files.copy(newImage.getInputStream(), img.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {return false;}
		
		entity.setProfileImagePath(img_name);
		employeeRepo.save(entity);
		
		try {
			Files.copy(newImage.getInputStream(), img.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {return false;}
		
		
		return true;
	}

	@Override
	public Boolean deleteEmployee(EmployeeAdminEmailWrapper employeeAdminEmailWrapper) {
		
		String adminEmail = employeeAdminEmailWrapper.getAdminEmail();
		String employeeEmail = employeeAdminEmailWrapper.getEmployeeEmail();
		
		if(!employeeRepo.existsById(employeeEmail)) throw new EmployeeNotFoundException();
		
		Employee entity = employeeRepo.findById(employeeEmail).get();
		
		String employeeFullName = entity.getFullName();
		employeeRepo.delete(entity);
		
		
		// REMOVE FROM TEAM, DESIGNATION, DEPARTMENT List<String> employees
		
		restTemplate.exchange("http://SERVICE-TEAM/team/delete-employee-from-all-teams", HttpMethod.DELETE, new HttpEntity<EmployeeEmailWrapper>(new EmployeeEmailWrapper(employeeEmail)), Boolean.class);
		restTemplate.exchange("http://SERVICE-DEPARTMENT/department/delete-employee", HttpMethod.DELETE, new HttpEntity<EmployeeEmailWrapper>(new EmployeeEmailWrapper(employeeEmail)), Boolean.class);
		restTemplate.exchange("http://SERVICE-DESIGNATION/designation/delete-employee", HttpMethod.DELETE, new HttpEntity<EmployeeEmailWrapper>(new EmployeeEmailWrapper(employeeEmail)), Boolean.class);
		
		
		// Remove attendance object of employee
		restTemplate.exchange("http://SERVICE-ATTENDANCE/attendance/delete-employee-records", HttpMethod.DELETE, new HttpEntity<EmployeeAdminEmailWrapper>(new EmployeeAdminEmailWrapper(employeeEmail, adminEmail)), Boolean.class);
		
		
		// notify Service-Admin-Logger
		NewLog employeeRemovedLog = new NewLog();
		employeeRemovedLog.setAdminEmail(adminEmail);
		employeeRemovedLog.setType(LogType.EMPLOYEE_DELETED);
		
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(adminEmail), String.class);
		String adminName = adminNameRE.getBody();
		employeeRemovedLog.setMessage(employeeFullName+" ("+employeeEmail+") Deleted by Admin "+adminName+" "+"("+adminEmail+")");
		
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", employeeRemovedLog, Boolean.class);
		
		// notify Service-Organization
		HttpEntity<EmployeeEmailWrapper> httpEntity = new HttpEntity<>(new EmployeeEmailWrapper(employeeEmail));
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(employeeEmail), String.class);
		String organizationName = resp.getBody();
		restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/remove-employee/"+organizationName, HttpMethod.DELETE, httpEntity, Boolean.class);
		
		// notify Service-Auth
		restTemplate.exchange("http://SERVICE-AUTH/auth/delete-user", HttpMethod.DELETE, httpEntity, Boolean.class);
		
		// delete employees salary record
		restTemplate.exchange("http://SERVICE-SALARY/salary/delete-employee-salary", HttpMethod.DELETE, 
				new HttpEntity<EmployeeEmailWrapper>(new EmployeeEmailWrapper(employeeEmail)), Boolean.class);
		
		return true;
	}

	@Override
	public Boolean isEmployeeExist(EmployeeEmailWrapper employeeEmailWrapper) {

		return employeeRepo.existsById(employeeEmailWrapper.getEmployeeEmail());
	}

	@Override
	public Boolean updateEmployee(EmployeeDetailsUpdate employeeDetailsUpdate) {
		
		String employeeEmail = employeeDetailsUpdate.getEmail();
		if(!employeeRepo.existsById(employeeEmail)) throw new EmployeeNotFoundException();
		
		Employee entity = employeeRepo.findById(employeeEmail).get();
		
		entity.setAddress(employeeDetailsUpdate.getAddress());
		entity.setDob(employeeDetailsUpdate.getDob());
		entity.setEmergencyContact(employeeDetailsUpdate.getEmergencyContact());
		entity.setGender(employeeDetailsUpdate.getGender());
		entity.setJoiningDate(employeeDetailsUpdate.getJoiningDate());
		entity.setPhoneNumber(employeeDetailsUpdate.getPhoneNumber());
		entity.setReportingManager(employeeDetailsUpdate.getReportingManager());
		entity.setSkills(employeeDetailsUpdate.getSkills());
		
		entity.getBankDetail().setAccountNumber(employeeDetailsUpdate.getBankDetail().getAccountNumber());
		entity.getBankDetail().setBankName(employeeDetailsUpdate.getBankDetail().getBankName());
		entity.getBankDetail().setBranch(employeeDetailsUpdate.getBankDetail().getBranch());
		entity.getBankDetail().setIfscCode(employeeDetailsUpdate.getBankDetail().getIfscCode());
		
		entity.getRating().setCreativity(employeeDetailsUpdate.getRating().getCreativity());
		entity.getRating().setPerformance(employeeDetailsUpdate.getRating().getPerformance());
		entity.getRating().setPunctuality(employeeDetailsUpdate.getRating().getPunctuality());
		entity.getRating().setSoftSkills(employeeDetailsUpdate.getRating().getSoftSkills());
		
		employeeRepo.save(entity);
		
		
		// Notify Service-Admin-Logger
		NewLog employeeRemovedLog = new NewLog();
		employeeRemovedLog.setAdminEmail(employeeDetailsUpdate.getAdminEmail());
		employeeRemovedLog.setType(LogType.EMPLOYEE_DETAILS_UPDATED);
		
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(employeeDetailsUpdate.getAdminEmail()), String.class);
		String adminName = adminNameRE.getBody();
		employeeRemovedLog.setMessage(entity.getFullName()+" ("+employeeEmail+") Details Updated by Admin "+adminName+" "+"("+employeeDetailsUpdate.getAdminEmail()+")");
		
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", employeeRemovedLog, Boolean.class);
		
		return true;
	}
	
	@Override
	public List<EmployeeBasicInformation> getAllOrganizationEmployees(AdminEmailWrapper adminEmailWrapper) {
		
		String adminEmail = adminEmailWrapper.getAdminEmail();
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		
		ResponseEntity<List<String>> empResp = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-employees/"+organizationName, 
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<String>>() {});
		
		List<String> organizationEmployees =  empResp.getBody();
		
		return getEmployeesBasicInformation(organizationEmployees);
	}
	
	
	private String generateRandomPassword()
	{
		final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
	    final int PASSWORD_LENGTH = 12;
		
		SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        
        return password.toString();
	}
	
	private Integer getAge(Date dob)
	{
		Date currentDate = new Date(System.currentTimeMillis());
		LocalDate currentLocalDate = currentDate.toLocalDate();
        LocalDate birthDate = dob.toLocalDate();
        
        return Period.between(birthDate, currentLocalDate).getYears();
	}
	
	
	// USED BY SERVICE-AUTH
	@Override
	public Boolean sendResetPasswordLink(ResetPasswordTokenWrapper passwordTokenWrapper) {
		emailService.sendResetPasswordEmail(passwordTokenWrapper.getEmail(), passwordTokenWrapper.getFullName(), passwordTokenWrapper.getToken());
		return true;
	}
	
	// USED BY SERVICE-AUTH
	@Override
	public Boolean sendAdminWelcomeMail(NewAdminDetails details) {
		emailService.sendAdminWelcomeEmail(details.getEmail(), details.getFullName(), details.getPassword());
		return true;
	}

	
}
