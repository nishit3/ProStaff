package com.prostaff.service.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prostaff.service.attendance.entity.Attendance;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance, String>{

}
