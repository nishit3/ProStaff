package com.prostaff.service.attendance.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prostaff.service.attendance.entity.Record;
import com.prostaff.service.attendance.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.attendance.inter_service_communication.dto.EmployeeAdminEmailWrapper;
import com.prostaff.service.attendance.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.attendance.inter_service_communication.dto.GrantedLeaveRequest;
import com.prostaff.service.attendance.service.AttendanceService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/attendance")
@Tag(name = "AttendanceController")
public class AttendanceController {

	@Autowired
	private AttendanceService attendanceService;
	
	@PutMapping("/check-in")
	public ResponseEntity<Boolean> checkIn(@RequestBody EmployeeEmailWrapper employeeEmailWrapper){
		return new ResponseEntity<Boolean>(attendanceService.checkIn(employeeEmailWrapper),HttpStatus.OK);
	}
	
	@PutMapping("/check-out")
	public ResponseEntity<Boolean> checkOut(@RequestBody EmployeeEmailWrapper employeeEmailWrapper){
		return new ResponseEntity<Boolean>(attendanceService.checkOut(employeeEmailWrapper),HttpStatus.OK);
	}
	
	@PostMapping("/add-holiday/{date}")
	public ResponseEntity<Boolean> addHoliday(@PathVariable Date date,@RequestBody AdminEmailWrapper adminEmailWrapper){
		return new ResponseEntity<Boolean>(attendanceService.addHoliday(date, adminEmailWrapper),HttpStatus.CREATED);
	}
	
	@DeleteMapping("/delete-holiday/{date}")
	public ResponseEntity<Boolean> deleteHoliday(@PathVariable Date date,@RequestBody AdminEmailWrapper adminEmailWrapper){
		return new ResponseEntity<Boolean>(attendanceService.deleteHoliday(date, adminEmailWrapper),HttpStatus.OK);
	}
	
	@PutMapping("/permit-leave-request")
	public ResponseEntity<Boolean> permitLeaveRequest(@RequestBody GrantedLeaveRequest grantedLeaveRequest){
		return new ResponseEntity<Boolean>(attendanceService.permitLeaveRequest(grantedLeaveRequest),HttpStatus.OK);
	}
	
	@PostMapping("/get-employee-attendance")
	public ResponseEntity<List<Record>> getEmployeeAttendance(@RequestBody EmployeeEmailWrapper employeeEmailWrapper){
		return new ResponseEntity<List<Record>>(attendanceService.getEmployeeAttendance(employeeEmailWrapper),HttpStatus.OK);
	}
	
	@PostMapping("/register-employee")
	public ResponseEntity<Boolean> registerEmployee(@RequestBody EmployeeAdminEmailWrapper employeeAdminEmailWrapper){
		return new ResponseEntity<Boolean>(attendanceService.registerEmployee(employeeAdminEmailWrapper),HttpStatus.OK);
	}
	
	@PostMapping("/get-all-holidays")
	public ResponseEntity<List<Record>> getAllHolidays(@RequestBody AdminEmailWrapper adminEmailWrapper){
		return new ResponseEntity<List<Record>>(attendanceService.getAllHolidays(adminEmailWrapper), HttpStatus.OK);
	}
	
	@DeleteMapping("/delete-employee-records")
	public ResponseEntity<Boolean> deleteEmployeeRecords(@RequestBody EmployeeAdminEmailWrapper employeeAdminEmailWrapper){
		return new ResponseEntity<Boolean>(attendanceService.deleteEmployeeRecords(employeeAdminEmailWrapper),HttpStatus.OK);
	}
	
}	
