package com.prostaff.service.team.proxy;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TeamProxy {

	String name;
	List<String> employees;
	String description;
}
