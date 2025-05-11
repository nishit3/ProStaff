package com.prostaff.service.attendance.service;

import java.sql.Date;
import java.util.List;

import com.prostaff.service.attendance.entity.Record;
import com.prostaff.service.attendance.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.attendance.inter_service_communication.dto.EmployeeAdminEmailWrapper;
import com.prostaff.service.attendance.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.attendance.inter_service_communication.dto.GrantedLeaveRequest;


public interface AttendanceService {

	public Boolean checkIn(EmployeeEmailWrapper employeeEmailWrapper);
	
	public Boolean checkOut(EmployeeEmailWrapper employeeEmailWrapper);
	
	public Boolean addHoliday(Date date, AdminEmailWrapper adminEmailWrapper);
	
	public Boolean deleteHoliday(Date date, AdminEmailWrapper adminEmailWrapper);
	
	public Boolean permitLeaveRequest(GrantedLeaveRequest grantedLeaveRequest);
	
	public List<Record> getAllHolidays(AdminEmailWrapper adminEmailWrapper);
	
	public List<Record> getEmployeeAttendance(EmployeeEmailWrapper employeeEmailWrapper);
	
	public Boolean registerEmployee(EmployeeAdminEmailWrapper employeeAdminEmailWrapper);
	
	public Boolean deleteEmployeeRecords(EmployeeAdminEmailWrapper employeeAdminEmailWrapper);
}
