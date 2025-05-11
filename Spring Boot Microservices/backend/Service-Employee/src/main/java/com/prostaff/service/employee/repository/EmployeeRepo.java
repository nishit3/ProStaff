package com.prostaff.service.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prostaff.service.employee.entity.Employee;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, String> {

}
