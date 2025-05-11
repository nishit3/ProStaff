package com.prostaff.service.team.service;

import java.util.List;

import com.prostaff.service.team.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.team.inter_service_communication.dto.EmployeeBasicInformation;
import com.prostaff.service.team.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.team.inter_service_communication.dto.EmployeeTeamsUpdate;
import com.prostaff.service.team.inter_service_communication.dto.NewTeam;
import com.prostaff.service.team.inter_service_communication.dto.TeamBasicInformation;

public interface TeamService {
    
	
	public List<String> getAllTeamNames(AdminEmailWrapper adminEmailWrapper);

	public List<TeamBasicInformation> getEmployeeteams(EmployeeEmailWrapper employeeEmailWrapper);

	public List<EmployeeBasicInformation> getAllEmployee(Long id);

	public List<TeamBasicInformation> getAllteams(AdminEmailWrapper adminEmailWrapper);

	public Boolean updateEmployees(Long id, List<String> employeeList);

	public Boolean updateCurrentEmployeeTeams(EmployeeTeamsUpdate employeeTeamsUpdate);

	public Boolean addTeam(NewTeam newTeam);

	public Boolean deleteTeam(Long id, AdminEmailWrapper adminEmailWrapper);
	
	// will be called from employee service
	public Boolean deleteEmployeeFromAllTeams(EmployeeEmailWrapper employeeEmailWrapper);
	
	public Boolean addEmployeeToTeam(Long id, EmployeeEmailWrapper employeeEmailWrapper);
}
