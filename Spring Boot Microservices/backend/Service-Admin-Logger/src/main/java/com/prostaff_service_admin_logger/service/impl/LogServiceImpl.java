package com.prostaff_service_admin_logger.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.prostaff_service_admin_logger.entity.Log;
import com.prostaff_service_admin_logger.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff_service_admin_logger.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff_service_admin_logger.inter_service_communication.dto.NewLog;
import com.prostaff_service_admin_logger.inter_service_communication.enums.LogType;
import com.prostaff_service_admin_logger.repository.LogRepo;
import com.prostaff_service_admin_logger.service.LogService;

@Service
public class LogServiceImpl implements LogService {

	@Autowired
	private LogRepo logRepo;
	
	@Autowired 
	private RestTemplate restTemplate;

	
	@Override
	public List<Log> getAllLogs(AdminEmailWrapper adminEmailWrapper){
		
		EmployeeEmailWrapper reqBody = new EmployeeEmailWrapper(adminEmailWrapper.getAdminEmail());
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", reqBody, String.class);
		String organizationName = resp.getBody();
		
		ResponseEntity<List<Long>> logResp = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-admin-logs/"+organizationName, 
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<Long>>() {});
		
		List<Long> idList = logResp.getBody();
		List<Log> organizationLogs = logRepo.findAllById(idList);
		return organizationLogs; 
	}

	@Override
	public Boolean addLog(NewLog newLog) {
		
		Log entity = new Log();
		entity.setAdminEmail(newLog.getAdminEmail());
		
		EmployeeEmailWrapper reqBody1 = new EmployeeEmailWrapper(newLog.getAdminEmail());
		ResponseEntity<String> resp1 = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", reqBody1, String.class);
		String fullName = resp1.getBody();
		
		entity.setAdminFullName(fullName);
		entity.setMessage(newLog.getMessage());
		entity.setType(newLog.getType());
		
		entity = logRepo.save(entity);
		EmployeeEmailWrapper reqBody = new EmployeeEmailWrapper(newLog.getAdminEmail());
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", reqBody, String.class);
		String organizationName = resp.getBody();
		
		restTemplate.postForEntity("http://SERVICE-ORGANIZATION/organization/add-admin-log/"+organizationName+"/"+entity.getId(), null, Boolean.class);
		
		return true;
	}

	@Override
	public List<Log> getAllLogsAsPerType(AdminEmailWrapper adminEmailWrapper, LogType type) {
		
		EmployeeEmailWrapper reqBody = new EmployeeEmailWrapper(adminEmailWrapper.getAdminEmail());
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", reqBody, String.class);
		String organizationName = resp.getBody();
		
		ResponseEntity<List<Long>> logResp = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-admin-logs/"+organizationName, 
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<Long>>() {});
		
		List<Long> idList = logResp.getBody();
		List<Log> organizationLogs = logRepo.findAllById(idList);
		organizationLogs.removeIf(log -> !log.getType().equals(type));
		return organizationLogs;
	}

	

}
