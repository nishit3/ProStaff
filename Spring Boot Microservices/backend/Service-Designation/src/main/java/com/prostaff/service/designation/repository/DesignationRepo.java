package com.prostaff.service.designation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prostaff.service.designation.entity.Designation;

public interface DesignationRepo extends JpaRepository<Designation, Long>{

}
