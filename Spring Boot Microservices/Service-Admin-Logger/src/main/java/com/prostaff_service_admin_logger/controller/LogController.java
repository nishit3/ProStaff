package com.prostaff_service_admin_logger.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.prostaff_service_admin_logger.entity.Log;
import com.prostaff_service_admin_logger.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff_service_admin_logger.inter_service_communication.dto.NewLog;
import com.prostaff_service_admin_logger.inter_service_communication.enums.LogType;
import com.prostaff_service_admin_logger.service.LogService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin-logs")
@Tag(name = "LogController")
public class LogController {

	@Autowired
	private LogService service;
	
	@PostMapping("/get-all-logs")
	public ResponseEntity<List<Log>> getAllLogs(@RequestBody AdminEmailWrapper adminEmailWrapper)
	{
		return new ResponseEntity<List<Log>>(service.getAllLogs(adminEmailWrapper), null, 200);
	}
	
	@PostMapping("/get-all-specific-logs/{type}")
	public ResponseEntity<List<Log>> getAllLogsAsPerType(@RequestBody AdminEmailWrapper adminEmailWrapper, @PathVariable LogType type)
	{
		return new ResponseEntity<List<Log>>(service.getAllLogsAsPerType(adminEmailWrapper, type), null, 200);
	}
	
	@PostMapping("/add-log")
	public ResponseEntity<Boolean> addLog(@RequestBody NewLog newLog)
	{
		return new ResponseEntity<Boolean>(service.addLog(newLog), null, 200);
	}
	
}
