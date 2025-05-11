package com.prostaff.service.leave.request.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prostaff.service.leave.request.entity.LeaveRequest;

@Repository
public interface LeaveRequestRepo extends JpaRepository<LeaveRequest, Long>{
	
	@Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee_email = :email")
	List<LeaveRequest> findByEmployeeEmail(@Param("email") String email);
	
}
