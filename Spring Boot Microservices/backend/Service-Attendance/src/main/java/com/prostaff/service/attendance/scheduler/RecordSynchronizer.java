package com.prostaff.service.attendance.scheduler;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.prostaff.service.attendance.entity.Attendance;
import com.prostaff.service.attendance.entity.Record;
import com.prostaff.service.attendance.inter_service_communication.enums.Status;
import com.prostaff.service.attendance.repository.AttendanceRepo;
import com.prostaff.service.attendance.repository.RecordRepo;


@Component
public class RecordSynchronizer {

	@Autowired
	private AttendanceRepo db;
	
	@Autowired
	private RecordRepo records_db;
	
	
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        synchronizeRecords();
    }

    @Scheduled(cron = "0 5 0 * * ?") 
    public void onSchedule() {
        synchronizeRecords();
    }
 
    public void synchronizeRecords() {
        
    	
        // Add Sundays as Record with status HOLIDAY for entire month IF NOT ALREDY PRESENT.
		for(Attendance attendance : db.findAll())
		{	
			for(Date sunday : getAllSundaysOfCurrentMonth())
			{
				if(!doAttendanceRecordsHasThisDate(attendance.getAttendance(), sunday))
				{
					Record sundayRecord = new Record();
					sundayRecord.setDate(sunday);
					sundayRecord.setStatus(Status.HOLIDAY);
					sundayRecord = records_db.save(sundayRecord);
					attendance.getAttendance().add(sundayRecord);
					db.save(attendance);
				}
			}
		}
		
		
		// Mark ABSENT to previous dates with status PENDING or CHECKEDIN (except todays date)
		for(Attendance attendance : db.findAll())
		{	
			for(Record recrd : attendance.getAttendance())
			{
				if((recrd.getStatus() == Status.PENDING || recrd.getStatus() == Status.CHECKEDIN) && !recrd.getDate().toLocalDate().equals(LocalDate.now()))
				{
					recrd.setStatus(Status.ABSENT);
					records_db.save(recrd);
				}
			}
		}
		
		
		// Add current days Record with status PENDING.
		for(Attendance attendance : db.findAll())
		{	
			if(!doAttendanceRecordsHasThisDate(attendance.getAttendance(), Date.valueOf(LocalDate.now())))
			{
				Record rcrd = new Record();
				rcrd.setDate(Date.valueOf(LocalDate.now()));
				rcrd.setStatus(Status.PENDING);
				rcrd = records_db.save(rcrd);
				attendance.getAttendance().add(rcrd);
				db.save(attendance);
			}
		}	

    }
    
    private boolean doAttendanceRecordsHasThisDate(List<Record> records, Date date)
    {
    	for(Record record : records)
    	{
    		if(record.getDate().equals(date)) return true;
    	}
    	return false;
    }
    
    private List<Date> getAllSundaysOfCurrentMonth() {
        List<Date> sundays = new ArrayList<>();
        
        LocalDate today = LocalDate.now();
        YearMonth yearMonth = YearMonth.of(today.getYear(), today.getMonth());
        
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                sundays.add(Date.valueOf(date));
            }
        }
        
        return sundays;
    }
}
