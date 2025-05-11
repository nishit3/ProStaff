package com.prostaff.service.designation.entity;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Designation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;	
	String name;
	
	@Lob
    @JdbcTypeCode(SqlTypes.JSON)
	List<String> employees;
	
	String description;
}
