package com.prostaff.service_organization.domain;

import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.prostaff.service_organization.aggregator.HelpDetails;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Organization {
	
	@Id
	String organizationName; 
	
	String imagePath; 
	
	@Lob
    @JdbcTypeCode(SqlTypes.JSON)
	List<String> employees;
	
	@Lob
    @JdbcTypeCode(SqlTypes.JSON)
	List<Long> departments;
	
	@Lob
    @JdbcTypeCode(SqlTypes.JSON)
	List<Long> designations;
	
	@Lob
    @JdbcTypeCode(SqlTypes.JSON)
	List<Long> teams;
	
	@Lob
    @JdbcTypeCode(SqlTypes.JSON)
	List<Long> notifications;
	
	@Lob
    @JdbcTypeCode(SqlTypes.JSON)
	List<Long> adminLogs;
	
	@OneToMany
	@Cascade(CascadeType.ALL)
	List<FAQ> faqs;
	
	@OneToMany
	@Cascade(CascadeType.ALL)
	List<UpcomingEvent> upcomingEvents;
	
	@Embedded
	HelpDetails helpDetails;
}
