package com.prostaff.service_salary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prostaff.service_salary.entity.OrganizationFund;

@Repository
public interface OrganizationFundRepo extends JpaRepository<OrganizationFund, String>{

}
