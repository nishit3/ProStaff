package com.prostaff.service.leave.request.scheduler;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.prostaff.service.leave.request.entity.LeaveRequest;
import com.prostaff.service.leave.request.inter_service_communication.enums.LeaveRequestStatus;
import com.prostaff.service.leave.request.repository.LeaveRequestRepo;

@Component
public class GarbageLeaveRequestCleaner {
	
	@Autowired
	private LeaveRequestRepo db;
	
	@EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        clean();
    }

    @Scheduled(cron = "0 15 0 * * ?") 
    public void onSchedule() {
        clean();
    }
    
    
    public void clean()
    {
    	for(LeaveRequest req : db.findAll())
    	{
    		if(req.getStatus() != LeaveRequestStatus.PENDING && isGarbage(req.getRequestDate()))
    		{
    			db.delete(req);
    		}
    	}
    }
    
    public static boolean isGarbage(Date date) {
        long millisInWeek = 7 * 24 * 60 * 60 * 1000L;
        long diff = Math.abs(date.getTime() - Date.valueOf(LocalDate.now()).getTime());
        return diff == millisInWeek;
    }
	
}
