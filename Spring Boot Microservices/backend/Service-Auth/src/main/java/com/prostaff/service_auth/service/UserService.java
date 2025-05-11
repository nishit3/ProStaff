package com.prostaff.service_auth.service;


import org.springframework.web.multipart.MultipartFile;

import com.prostaff.service_auth.dto.LoginRequest;
import com.prostaff.service_auth.dto.LoginResponse;
import com.prostaff.service_auth.dto.NewAdmin;
import com.prostaff.service_auth.dto.NewAdminAndOrganizationDetails;
import com.prostaff.service_auth.dto.OrderDetails;
import com.prostaff.service_auth.dto.PaymentDetails;
import com.prostaff.service_auth.inter_service_communication.dto.AuthRequest;
import com.prostaff.service_auth.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service_auth.inter_service_communication.dto.NewUser;

public interface UserService {
	
	public Boolean addUser(NewUser newUser);
	public Boolean deleteUser(EmployeeEmailWrapper employeeEmailWrapper);
	public LoginResponse login(LoginRequest loginRequest);
	public Integer validate(AuthRequest authRequest);
	public Boolean isUserExist(EmployeeEmailWrapper employeeEmailWrapper);
	public String getUserOrganization(EmployeeEmailWrapper employeeEmailWrapper);
	public String getFullName(EmployeeEmailWrapper employeeEmailWrapper);
	public OrderDetails getOrderInformation();
	public Boolean verifyPayment(PaymentDetails details, MultipartFile orgImage);
	public Boolean validateNewAdminAndOrganization(NewAdminAndOrganizationDetails details);
	public Boolean addAdmin(NewAdmin newAdmin);
	
}
