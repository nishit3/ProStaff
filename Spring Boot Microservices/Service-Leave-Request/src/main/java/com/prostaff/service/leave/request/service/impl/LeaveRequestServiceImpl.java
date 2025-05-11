package com.prostaff.service.leave.request.service.impl;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import com.prostaff.service.leave.request.entity.LeaveRequest;
import com.prostaff.service.leave.request.exception.LeaveRequestAlreadyExist;
import com.prostaff.service.leave.request.exception.LeaveRequestNotFoundException;
import com.prostaff.service.leave.request.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.leave.request.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.leave.request.inter_service_communication.dto.EmployeeEmailWrapperWithOnlyPending;
import com.prostaff.service.leave.request.inter_service_communication.dto.GrantedLeaveRequest;
import com.prostaff.service.leave.request.inter_service_communication.dto.LeaveRequestCount;
import com.prostaff.service.leave.request.inter_service_communication.dto.NewLog;
import com.prostaff.service.leave.request.inter_service_communication.enums.LeaveRequestStatus;
import com.prostaff.service.leave.request.inter_service_communication.enums.LeaveRequestType;
import com.prostaff.service.leave.request.inter_service_communication.enums.LogType;
import com.prostaff.service.leave.request.repository.LeaveRequestRepo;
import com.prostaff.service.leave.request.service.LeaveRequestService;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService{

	@Autowired
	private LeaveRequestRepo db;
	
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public List<LeaveRequest> getAllLeaveRequests(AdminEmailWrapper adminEmailWrapper) {
		
		String adminEmail = adminEmailWrapper.getAdminEmail();
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		
		List<String> organizationEmployees = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-employees/"+organizationName, 
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<String>>() {}).getBody();
		
		List<LeaveRequest> toBeReturned = new ArrayList<>();
		 
		for(String employeeEmail : organizationEmployees)
		{
			toBeReturned.addAll(db.findByEmployeeEmail(employeeEmail));
		}
		
		return toBeReturned;
	}

	@Override
	public Boolean createRequest(LeaveRequest leaveRequest) {
		
		if(!isLeaveRequestedAtDate(db.findByEmployeeEmail(leaveRequest.getEmployee_email()), leaveRequest.getLeaveDate()))
		{
			throw new LeaveRequestAlreadyExist();
		}
		
		leaveRequest.setRequestDate(Date.valueOf(LocalDate.now()));
		leaveRequest.setStatus(LeaveRequestStatus.PENDING);
		
		db.save(leaveRequest);
		
		return true;
	}

	@Override
	public List<LeaveRequest> getEmployeeLeaveRequest(EmployeeEmailWrapperWithOnlyPending employeePending) {
		
		List<LeaveRequest> toBeReturned = db.findByEmployeeEmail(employeePending.getEmployeeEmail());
		if(employeePending.getOnlyPending())
		{
			toBeReturned.removeIf(req -> req.getStatus() != LeaveRequestStatus.PENDING);
		}
		return toBeReturned;
	}

	@Override
	public Boolean grant(Long id, AdminEmailWrapper adminEmailWrapper) {
		
		if(!db.existsById(id)) throw new LeaveRequestNotFoundException();
		
		LeaveRequest entity = db.findById(id).get();
		if(entity.getStatus() != LeaveRequestStatus.PENDING) return false;
		
		String adminEmail = adminEmailWrapper.getAdminEmail();
		
		entity.setStatus(LeaveRequestStatus.ACCEPTED);
		db.save(entity);
		
		GrantedLeaveRequest grant = new GrantedLeaveRequest();
		grant.setAdminEmail(adminEmail);
		grant.setDate(entity.getLeaveDate());
		grant.setEmployeeEmail(entity.getEmployee_email());
		
		restTemplate.exchange("http://SERVICE-ATTENDANCE/attendance/permit-leave-request", HttpMethod.PUT,
				new HttpEntity<GrantedLeaveRequest>(grant), Boolean.class);
		 
		
		NewLog leaveReqApproved = new NewLog();
		leaveReqApproved.setAdminEmail(adminEmail);
		leaveReqApproved.setType(LogType.LEAVE_REQUEST_APPROVED);
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(adminEmail), String.class);
		String adminName = adminNameRE.getBody();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		leaveReqApproved.setMessage("Leave Request of"+ entity.getEmployee_email() + "for "+ sdf.format(entity.getLeaveDate()) +" , Approved by Admin "+adminName+" "+"("+adminEmail+")");
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", leaveReqApproved, Boolean.class);
		 
		return true;
	}

	@Override
	public Boolean deny(Long id, AdminEmailWrapper adminEmailWrapper) {
		
		if(!db.existsById(id)) throw new LeaveRequestNotFoundException();
		
		LeaveRequest entity = db.findById(id).get();
		if(entity.getStatus() != LeaveRequestStatus.PENDING) return false;
		
		String adminEmail = adminEmailWrapper.getAdminEmail();
		entity.setStatus(LeaveRequestStatus.REJECTED);
		db.save(entity);
		 
		NewLog leaveReqDenied = new NewLog();
		leaveReqDenied.setAdminEmail(adminEmail);
		leaveReqDenied.setType(LogType.LEAVE_REQUEST_DENIED);
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(adminEmail), String.class);
		String adminName = adminNameRE.getBody();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		leaveReqDenied.setMessage("Leave Request of"+ entity.getEmployee_email() + "for "+ sdf.format(entity.getLeaveDate()) +" , Denied by Admin "+adminName+" "+"("+adminEmail+")");
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", leaveReqDenied, Boolean.class);
		
		return true;
	}
	
	private boolean isLeaveRequestedAtDate(List<LeaveRequest> requests, Date date)
	{
		for(LeaveRequest req : requests)
		{
			if(req.getLeaveDate().equals(date)) return false;
		}
		return true;
	}

	@Override
	public LeaveRequestCount getEmployeeLeaveRequestCount(EmployeeEmailWrapper emailWrapper) {
		List <LeaveRequest> allLeaves = db.findByEmployeeEmail(emailWrapper.getEmployeeEmail());
		
		LocalDate today = LocalDate.now();
		List<LeaveRequest> filteredLeaves = allLeaves.stream()
		    .filter(leave -> {
		        LocalDate leaveLocalDate = leave.getLeaveDate().toLocalDate();
		        return !leaveLocalDate.isBefore(today.withDayOfYear(1));
		    })
		    .collect(Collectors.toList());
		
		LeaveRequestCount count = new LeaveRequestCount(0, 0, 0);
		
		for(LeaveRequest req : filteredLeaves)
		{
			if(req.getStatus() == LeaveRequestStatus.ACCEPTED)
			{ 
				if(req.getType() == LeaveRequestType.PAID) count.setPaidLeaves(count.getPaidLeaves() + 1);
				if(req.getType() == LeaveRequestType.UNPAID) count.setUnPaidLeaves(count.getUnPaidLeaves() + 1);
				if(req.getType() == LeaveRequestType.SICK) count.setSickLeaves(count.getSickLeaves() + 1);
			}
		}
		
		return count;
	}
}
