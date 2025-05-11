package com.prostaff.service.department.proxy;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class DepartmentProxy {

	Long id;
	String name;
	List<String> employees;
	String description;
}
