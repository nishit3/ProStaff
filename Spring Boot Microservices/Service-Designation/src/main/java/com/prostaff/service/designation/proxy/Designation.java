package com.prostaff.service.designation.proxy;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Designation {

	Long id;
	String name;
	List<String> employees;
	String description;
}
