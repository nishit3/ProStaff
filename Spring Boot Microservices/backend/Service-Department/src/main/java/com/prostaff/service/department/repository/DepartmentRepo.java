package com.prostaff.service.department.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prostaff.service.department.entity.Department;

@Repository
public interface DepartmentRepo extends JpaRepository<Department, Long>{

}
