package com.prostaff.service.attendance.service.impl;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
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

import com.prostaff.service.attendance.entity.Attendance;
import com.prostaff.service.attendance.entity.Record;
import com.prostaff.service.attendance.exception.EmployeeNotFoundException;
import com.prostaff.service.attendance.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.attendance.inter_service_communication.dto.EmployeeAdminEmailWrapper;
import com.prostaff.service.attendance.inter_service_communication.dto.EmployeeBasicInformation;
import com.prostaff.service.attendance.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.attendance.inter_service_communication.dto.GrantedLeaveRequest;
import com.prostaff.service.attendance.inter_service_communication.dto.NewLog;
import com.prostaff.service.attendance.inter_service_communication.enums.LogType;
import com.prostaff.service.attendance.inter_service_communication.enums.Status;
import com.prostaff.service.attendance.repository.AttendanceRepo;
import com.prostaff.service.attendance.repository.RecordRepo;
import com.prostaff.service.attendance.service.AttendanceService;



@Service
public class AttendanceServiceImpl implements AttendanceService{

	@Autowired
	private AttendanceRepo db;
	
	@Autowired
	private RecordRepo records_db;
	
	@Autowired
	private RestTemplate restTemplate;
	
	
	@Override
	public Boolean checkIn(EmployeeEmailWrapper employeeEmailWrapper) {
		
		if(!db.existsById(employeeEmailWrapper.getEmployeeEmail())) throw new EmployeeNotFoundException();
		
		Attendance entity = db.findById(employeeEmailWrapper.getEmployeeEmail()).get();
		
		Record rcrd = getTodaysRecord(entity.getAttendance());
		if(rcrd == null) return false;
		
		rcrd.setCheckIn(Time.valueOf(LocalTime.now()));
		rcrd.setStatus(Status.CHECKEDIN);
		
		records_db.save(rcrd);
		
		return true;
	}

	@Override
	public Boolean checkOut(EmployeeEmailWrapper employeeEmailWrapper) {
		
		if(!db.existsById(employeeEmailWrapper.getEmployeeEmail())) throw new EmployeeNotFoundException();
		
		Attendance entity = db.findById(employeeEmailWrapper.getEmployeeEmail()).get();
		
		Record rcrd = getTodaysRecord(entity.getAttendance());
		if(rcrd == null || rcrd.getStatus() != Status.CHECKEDIN) return false;
		
		rcrd.setStatus(Status.PRESENT);
		rcrd.setCheckOut(Time.valueOf(LocalTime.now()));
		
		records_db.save(rcrd);

		return true;
	}

	@Override
	public Boolean addHoliday(Date date, AdminEmailWrapper adminEmailWrapper) {
		
		String adminEmail = adminEmailWrapper.getAdminEmail();
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		
		// get data from organization service
		List<String> organizationEmployees = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-employees/"+organizationName, 
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<String>>() {}).getBody();
		
		
		List<Attendance> employeesAttendance = db.findAllById(organizationEmployees);
		
		for(Attendance attendance : employeesAttendance)
		{
			Record existingRecord =  getRecordWithSameDate(attendance.getAttendance(), date);
			if(existingRecord == null)
			{
				Record neww = new Record();
				neww.setDate(date);
				neww.setStatus(Status.HOLIDAY);
				neww = records_db.save(neww);
				attendance.getAttendance().add(neww);
				db.save(attendance);
			}
			else 
			{
				existingRecord.setStatus(Status.HOLIDAY);
				records_db.save(existingRecord);
			}
		}
		
		
		// add log
		NewLog employeeAddedLog = new NewLog();
		employeeAddedLog.setAdminEmail(adminEmail);
		employeeAddedLog.setType(LogType.HOLIDAY_ADDED);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(adminEmail), String.class);
		String adminName = adminNameRE.getBody();
		employeeAddedLog.setMessage("Holiday set at " + sdf.format(date)+ " by Admin "+adminName+" "+"("+adminEmail+")");
		
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", employeeAddedLog, Boolean.class);
		
		return true;
	}
	
	
	// only removes holiday if date is in future
	@Override
	public Boolean deleteHoliday(Date date, AdminEmailWrapper adminEmailWrapper) {
		
		if(date.compareTo(Date.valueOf(LocalDate.now())) <= 0) return false;
		
		String adminEmail = adminEmailWrapper.getAdminEmail();
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		
		// get data from organization service
		List<String> organizationEmployees = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-employees/"+organizationName, 
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<String>>() {}).getBody();
		
		
		List<Attendance> employeesAttendance = db.findAllById(organizationEmployees);
		
		for(Attendance attendance : employeesAttendance)
		{
			Record existingRecord =  getRecordWithSameDate(attendance.getAttendance(), date);
			if(existingRecord != null)
			{
				attendance.getAttendance().remove(existingRecord);
				db.save(attendance);
				records_db.delete(existingRecord);
			}
		}
		
		return true;
	}

	// used by leave request service
	@Override
	public Boolean permitLeaveRequest(GrantedLeaveRequest grantedLeaveRequest) {
		
		Attendance entity = db.findById(grantedLeaveRequest.getEmployeeEmail()).get();
		Record existingRecord = getRecordWithSameDate(entity.getAttendance(), grantedLeaveRequest.getDate());
		
		if(existingRecord == null)
		{
			Record neww = new Record();
			neww.setDate(grantedLeaveRequest.getDate());
			neww.setStatus(Status.PRESENT);
			neww = records_db.save(neww);
			entity.getAttendance().add(neww);
			db.save(entity);
		}
		
		else
		{
			existingRecord.setStatus(Status.PRESENT);
			records_db.save(existingRecord);
		}
		
		return true;
	}

	@Override
	public List<Record> getEmployeeAttendance(EmployeeEmailWrapper employeeEmailWrapper) {
		
		if(!db.existsById(employeeEmailWrapper.getEmployeeEmail())) throw new EmployeeNotFoundException();
		
		return db.findById(employeeEmailWrapper.getEmployeeEmail()).get().getAttendance();
	}

	@Override
	public Boolean registerEmployee(EmployeeAdminEmailWrapper employeeAdminEmailWrapper) {
		
		Attendance entity = new Attendance();
		entity.setEmployeeEmail(employeeAdminEmailWrapper.getEmployeeEmail());
		Record todaysRecord = new Record();
		
		todaysRecord.setDate(Date.valueOf(LocalDate.now()));
		todaysRecord.setStatus(Status.PENDING);
		todaysRecord = records_db.save(todaysRecord);
		
		List<Record> records = new ArrayList<>();
		records.add(todaysRecord);
		entity.setAttendance(records);
		db.save(entity);
		return true;
	}

	@Override
	public Boolean deleteEmployeeRecords(EmployeeAdminEmailWrapper employeeAdminEmailWrapper) {
		
		if(!db.existsById(employeeAdminEmailWrapper.getEmployeeEmail())) throw new EmployeeNotFoundException();
		
		Attendance entity = db.findById(employeeAdminEmailWrapper.getEmployeeEmail()).get();
		List<Record> recordsBeDeleted = entity.getAttendance();
		
		db.delete(entity);
		
		for(Record rcrd : recordsBeDeleted) records_db.delete(rcrd);
		
		return true;
	}
	
	@Override
	public List<Record> getAllHolidays(AdminEmailWrapper adminEmailWrapper) {
		
		String adminEmail = adminEmailWrapper.getAdminEmail();
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		
		// get data from organization service
		List<String> organizationEmployees = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-employees/"+organizationName, 
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<String>>() {}).getBody();
		
		ResponseEntity<List<EmployeeBasicInformation>> resp1 = restTemplate.exchange(
				"http://SERVICE-EMPLOYEE/employee/get-employees-basic-information", 
				HttpMethod.POST, new HttpEntity<List<String>>(organizationEmployees), 
				new ParameterizedTypeReference<List<EmployeeBasicInformation>>(){});
		
		List<EmployeeBasicInformation> ebi = resp1.getBody();
		// there will always be 1 emp in list as frontend never calls when emp count is 0.
		String anyEmployeeEmail = ebi.get(0).getEmail();
		
		return db.findById(anyEmployeeEmail).get().getAttendance().stream().filter(rcrd -> rcrd.getStatus() == Status.HOLIDAY).collect(Collectors.toList());
	}
	
	
	private Record getTodaysRecord(List<Record> records)
    {
    	return getRecordWithSameDate(records, Date.valueOf(LocalDate.now()));
    }
	
	private Record getRecordWithSameDate(List<Record> records, Date date)
	{
		for(Record record : records)
    	{
    		if(record.getDate().equals(date)) return record;
    	}
    	return null;
	}

}
