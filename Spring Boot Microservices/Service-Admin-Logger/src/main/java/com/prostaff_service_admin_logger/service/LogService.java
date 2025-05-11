package com.prostaff_service_admin_logger.service;


import java.util.List;
import com.prostaff_service_admin_logger.entity.Log;
import com.prostaff_service_admin_logger.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff_service_admin_logger.inter_service_communication.dto.NewLog;
import com.prostaff_service_admin_logger.inter_service_communication.enums.LogType;


public interface LogService {

	public List<Log> getAllLogs(AdminEmailWrapper adminEmailWrapper);
	public List<Log> getAllLogsAsPerType(AdminEmailWrapper adminEmailWrapper, LogType type);
	public Boolean addLog(NewLog newLog);
}
