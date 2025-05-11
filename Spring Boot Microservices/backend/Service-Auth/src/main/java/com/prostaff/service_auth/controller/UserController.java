package com.prostaff.service_auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prostaff.service_auth.dto.LoginRequest;
import com.prostaff.service_auth.dto.LoginResponse;
import com.prostaff.service_auth.dto.NewAdmin;
import com.prostaff.service_auth.dto.NewAdminAndOrganizationDetails;
import com.prostaff.service_auth.dto.OrderDetails;
import com.prostaff.service_auth.dto.PaymentDetails;
import com.prostaff.service_auth.dto.ResetPasswordRequest;
import com.prostaff.service_auth.inter_service_communication.dto.AuthRequest;
import com.prostaff.service_auth.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service_auth.inter_service_communication.dto.NewUser;
import com.prostaff.service_auth.service.ResetPasswordService;
import com.prostaff.service_auth.service.UserService;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "AuthController")
public class UserController {
	
	@Autowired
	private UserService service;
	
	@Autowired
	private ResetPasswordService resetPasswordService;
	
	@Hidden
	@PostMapping("/add-user")
	public ResponseEntity<Boolean> addUser(@RequestBody NewUser newUser)
	{
		return new ResponseEntity<>(service.addUser(newUser), null, 200);
	}
	
	@Hidden
	@DeleteMapping("/delete-user")
	public ResponseEntity<Boolean> deleteUser(@RequestBody EmployeeEmailWrapper employeeEmailWrapper)
    {
		return new ResponseEntity<>(service.deleteUser(employeeEmailWrapper), null, 200);
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest)
	{
		return new ResponseEntity<>(service.login(loginRequest), null, 200);
	}
	
	@Hidden
	@PostMapping("/validate")
	public ResponseEntity<Integer> Validate(@RequestBody AuthRequest authRequest)
	{
		return new ResponseEntity<>(service.validate(authRequest), null, 200);
	}
	
	@Hidden
	@PostMapping("/is-user-exist")
	public ResponseEntity<Boolean> isUserExist(@RequestBody EmployeeEmailWrapper employeeEmailWrapper)
	{
		return new ResponseEntity<>(service.isUserExist(employeeEmailWrapper), null, 200);
	}
	
	@Hidden
	@PostMapping("/get-user-organization")
	public ResponseEntity<String> getUserOrganization(@RequestBody EmployeeEmailWrapper employeeEmailWrapper)
	{
		return new ResponseEntity<>(service.getUserOrganization(employeeEmailWrapper), null, 200);
	}
	
	@Hidden
	@PostMapping("/get-user-fullname")
	public ResponseEntity<String> getFullName(@RequestBody EmployeeEmailWrapper employeeEmailWrapper) 
	{
		return new ResponseEntity<String>(service.getFullName(employeeEmailWrapper), null, 200);
	}
	
	@PostMapping("/send-reset-password-mail")
	public ResponseEntity<String> getToken(@RequestBody EmployeeEmailWrapper employeeEmailWrapper) {
		return new ResponseEntity<String>(resetPasswordService.getToken(employeeEmailWrapper), HttpStatus.OK);
	}
	
	@PostMapping("/request-reset-password")
	public ResponseEntity<Boolean> resetPassword(@RequestBody ResetPasswordRequest request) {
		return new ResponseEntity<Boolean>(resetPasswordService.resetPassword(request), HttpStatus.OK);
	}
	
	@PostMapping("/create-order")
	public ResponseEntity<OrderDetails> createOrder()
	{
		return new ResponseEntity<OrderDetails>(service.getOrderInformation(), HttpStatus.OK);
	}
	
	@PostMapping("/verify-payment")
	public ResponseEntity<Boolean> verifyPayment(@RequestPart PaymentDetails details, @RequestPart MultipartFile orgImage)
	{
		return new ResponseEntity<Boolean>(service.verifyPayment(details, orgImage), HttpStatus.OK);
	}
	
	@PostMapping("/verify-new-organization-details")
	public ResponseEntity<Boolean> validateNewAdminAndOrganization(@RequestBody NewAdminAndOrganizationDetails details) 
	{
		return new ResponseEntity<Boolean>(service.validateNewAdminAndOrganization(details), HttpStatus.OK);
	}
	
	@PostMapping("/add-admin")
	public ResponseEntity<Boolean> addAdmin(@RequestBody NewAdmin newAdmin)
	{
		return new ResponseEntity<Boolean>(service.addAdmin(newAdmin), HttpStatus.OK);
	}
	

}
