package com.prostaff.service.leave.request.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prostaff.service.leave.request.entity.LeaveRequest;
import com.prostaff.service.leave.request.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.leave.request.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.leave.request.inter_service_communication.dto.EmployeeEmailWrapperWithOnlyPending;
import com.prostaff.service.leave.request.inter_service_communication.dto.LeaveRequestCount;
import com.prostaff.service.leave.request.service.LeaveRequestService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/leave-request")
@Tag(name = "LeaveRequestController")
public class LeaveRequestController {
	
	@Autowired
	private LeaveRequestService leaveRequestService;
	
	@PostMapping("/get-all-leave-request")
	public ResponseEntity<List<LeaveRequest>> getAllLeaveRequest(@RequestBody AdminEmailWrapper adminEmailWrapper){
		return new ResponseEntity<List<LeaveRequest>>(leaveRequestService.getAllLeaveRequests(adminEmailWrapper),HttpStatus.OK);
	}
	
	@PostMapping("/create-request")
	public ResponseEntity<Boolean> createRequest(@RequestBody LeaveRequest leaveRequest){
		return new ResponseEntity<Boolean>(leaveRequestService.createRequest(leaveRequest),HttpStatus.CREATED);
	}
	
	@PostMapping("/get-employee-leave-request")
	public ResponseEntity<List<LeaveRequest>> getEmployeeLeaveRequest(@RequestBody EmployeeEmailWrapperWithOnlyPending employeepending){
		return new ResponseEntity<List<LeaveRequest>>(leaveRequestService.getEmployeeLeaveRequest(employeepending),HttpStatus.OK);
	}
	
	@PutMapping("/grant/{id}")
	public ResponseEntity<Boolean> grant(@PathVariable Long id,@RequestBody AdminEmailWrapper adminEmailWrapper){
		return new ResponseEntity<Boolean>(leaveRequestService.grant(id, adminEmailWrapper),HttpStatus.OK);
	}
	
	@PutMapping("/deny/{id}")
	public ResponseEntity<Boolean> deny(@PathVariable Long id,@RequestBody AdminEmailWrapper adminEmailWrapper){
		return new ResponseEntity<Boolean>(leaveRequestService.deny(id, adminEmailWrapper),HttpStatus.OK);
	}
	
	@PostMapping("/get-leave-requests-count")
	public ResponseEntity<LeaveRequestCount> getEmployeeLeaveRequestCount(@RequestBody EmployeeEmailWrapper emailWrapper) {
		return new ResponseEntity<LeaveRequestCount>(leaveRequestService.getEmployeeLeaveRequestCount(emailWrapper),HttpStatus.OK);
	}
}
