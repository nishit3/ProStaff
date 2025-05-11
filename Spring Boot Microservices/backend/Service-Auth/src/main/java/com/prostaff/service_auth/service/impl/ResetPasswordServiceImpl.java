package com.prostaff.service_auth.service.impl;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.prostaff.service_auth.domian.ResetPasswordToken;
import com.prostaff.service_auth.domian.User;
import com.prostaff.service_auth.dto.ResetPasswordRequest;
import com.prostaff.service_auth.dto.ResetPasswordTokenWrapper;
import com.prostaff.service_auth.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service_auth.repository.ResetPasswordTokenRepository;
import com.prostaff.service_auth.repository.UserRepository;
import com.prostaff.service_auth.service.ResetPasswordService;

@Service
public class ResetPasswordServiceImpl implements ResetPasswordService{
	
	@Autowired
	private ResetPasswordTokenRepository token_db;
	
	@Autowired
	private UserRepository user_db;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Override
	public String getToken(EmployeeEmailWrapper employeeEmailWrapper) {
		if(!user_db.existsById(employeeEmailWrapper.getEmployeeEmail())) return "No Such User Exists";
		
		ResetPasswordToken newToken = new ResetPasswordToken();
		newToken.setEmail(user_db.findById(employeeEmailWrapper.getEmployeeEmail()).get().getEmail());
		newToken.setExpirationTime(LocalDateTime.now().plusMinutes(10));
		newToken = token_db.save(newToken);
		System.err.println("Reset Token:"+newToken);
		ResetPasswordTokenWrapper wrapper = new ResetPasswordTokenWrapper(newToken.getEmail(), user_db.findById(employeeEmailWrapper.getEmployeeEmail()).get().getFullName(), newToken.getToken());
		Boolean isMailSent =  restTemplate.postForEntity("http://SERVICE-EMPLOYEE/employee/send-reset-password-mail", wrapper, Boolean.class).getBody();
		
		return isMailSent ? "Password reset link sent to the registered email address." : "Error Occured. Please Try Again Later.";
	}

	@Override
	public Boolean resetPassword(ResetPasswordRequest request) {
		
		if(!token_db.existsById(request.getToken())) return false;
		
		ResetPasswordToken token = token_db.findById(request.getToken()).get();
		
		if(token_db.findById(request.getToken()).get().getExpirationTime()
		.isBefore(LocalDateTime.now()) || !user_db.existsById(token.getEmail()))
		{
			token_db.delete(token);
			return false;
		}
		
		User user = user_db.findById(token.getEmail()).get();
		user.setPassword(encoder.encode(request.getNewPassword()));
		user_db.save(user);
		
		token_db.delete(token); 
		
		return true;
	}

}
