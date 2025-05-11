package com.prostaff.service_salary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prostaff.service_salary.entity.Salary;

@Repository
public interface SalaryRepo extends JpaRepository<Salary, String>{

}
