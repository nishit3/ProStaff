package com.prostaff.service_organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prostaff.service_organization.domain.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, String>{
	
}
