package com.prostaff.service_salary.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrganizationFund {

	@Id
	String organization;
	Long availableFund;
	
	@OneToMany(cascade = CascadeType.ALL)
	List<SalaryLog> logs;
}
