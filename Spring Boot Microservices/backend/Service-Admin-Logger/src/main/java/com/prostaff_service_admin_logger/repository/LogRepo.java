package com.prostaff_service_admin_logger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prostaff_service_admin_logger.entity.Log;

@Repository
public interface LogRepo extends JpaRepository<Log, Long>{

}
