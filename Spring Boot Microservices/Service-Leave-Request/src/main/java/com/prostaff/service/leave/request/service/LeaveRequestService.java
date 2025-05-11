package com.prostaff.service.leave.request.service;

import java.util.List;

import com.prostaff.service.leave.request.entity.LeaveRequest;
import com.prostaff.service.leave.request.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.leave.request.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.leave.request.inter_service_communication.dto.EmployeeEmailWrapperWithOnlyPending;
import com.prostaff.service.leave.request.inter_service_communication.dto.LeaveRequestCount;


public interface LeaveRequestService {

	public List<LeaveRequest> getAllLeaveRequests(AdminEmailWrapper adminEmailWrapper);
	public Boolean createRequest(LeaveRequest leaveRequest);
	public List<LeaveRequest> getEmployeeLeaveRequest(EmployeeEmailWrapperWithOnlyPending employeePending);
	public Boolean grant(Long id, AdminEmailWrapper adminEmailWrapper);
	public Boolean deny(Long id, AdminEmailWrapper adminEmailWrapper);
	public LeaveRequestCount getEmployeeLeaveRequestCount(EmployeeEmailWrapper emailWrapper);

}
