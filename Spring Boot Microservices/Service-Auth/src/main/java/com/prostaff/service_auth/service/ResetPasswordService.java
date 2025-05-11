package com.prostaff.service_auth.service;

import com.prostaff.service_auth.dto.ResetPasswordRequest;
import com.prostaff.service_auth.inter_service_communication.dto.EmployeeEmailWrapper;

public interface ResetPasswordService {
	
	public String getToken(EmployeeEmailWrapper employeeEmailWrapper);
	public Boolean resetPassword(ResetPasswordRequest request);
	
}
