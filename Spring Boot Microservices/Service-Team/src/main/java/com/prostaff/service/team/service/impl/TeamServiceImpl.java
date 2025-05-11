package com.prostaff.service.team.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.prostaff.service.team.entity.Team;
import com.prostaff.service.team.exception.TeamAlreadyExistsException;
import com.prostaff.service.team.exception.TeamNotFoundException;
import com.prostaff.service.team.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.team.inter_service_communication.dto.EmployeeBasicInformation;
import com.prostaff.service.team.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.team.inter_service_communication.dto.EmployeeTeamsUpdate;
import com.prostaff.service.team.inter_service_communication.dto.NewLog;
import com.prostaff.service.team.inter_service_communication.dto.NewTeam;
import com.prostaff.service.team.inter_service_communication.dto.TeamBasicInformation;
import com.prostaff.service.team.inter_service_communication.enums.LogType;
import com.prostaff.service.team.repository.TeamRepo;
import com.prostaff.service.team.service.TeamService;

@Service
public class TeamServiceImpl implements TeamService{

	
	@Autowired
	private TeamRepo teamRepo;
	
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public List<String> getAllTeamNames(AdminEmailWrapper adminEmailWrapper) {
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmailWrapper.getAdminEmail()), String.class);
		String organizationName = resp.getBody();
		ResponseEntity<List<Long>> teamIdsRE = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-teams/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});
		
		ArrayList<String> toBeReturned = new ArrayList<>();
		
		for(Team team : teamRepo.findAllById(teamIdsRE.getBody()))
		{
			toBeReturned.add(team.getName());
		}
		
		return toBeReturned;
	}

	@Override
	public List<TeamBasicInformation> getEmployeeteams(EmployeeEmailWrapper employeeEmailWrapper) {
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", employeeEmailWrapper, String.class);
		
		String organizationName = resp.getBody();
		String employeeEmail = employeeEmailWrapper.getEmployeeEmail();
		
		ResponseEntity<List<Long>> teamIdsRE = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-teams/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});
		
		ArrayList<TeamBasicInformation> toBeReturned = new ArrayList<>();
		
		for(Team team : teamRepo.findAllById(teamIdsRE.getBody()))
		{
			if(team.getEmployees().contains(employeeEmail))
			{
				TeamBasicInformation tbi = new TeamBasicInformation();
				
				tbi.setDescription(team.getDescription());
				tbi.setEmployeeCount(Integer.toUnsignedLong(team.getEmployees().size()));
				tbi.setId(team.getId());
				tbi.setName(team.getName());
				toBeReturned.add(tbi);
			}
		}
		
		return toBeReturned;
	}

	@Override
	public List<EmployeeBasicInformation> getAllEmployee(Long id) {
		
		if(!teamRepo.existsById(id)) throw new TeamNotFoundException();
		
		List<String> employees = teamRepo.findById(id).get().getEmployees();
		
		ResponseEntity<List<EmployeeBasicInformation>> resp = restTemplate.exchange("http://SERVICE-EMPLOYEE/employee/get-employees-basic-information", HttpMethod.POST, new HttpEntity<List<String>>(employees), new ParameterizedTypeReference<List<EmployeeBasicInformation>>(){});
		List<EmployeeBasicInformation> toBeReturned = resp.getBody();
		
		return toBeReturned;
	}

	@Override
	public List<TeamBasicInformation> getAllteams(AdminEmailWrapper adminEmailWrapper) {
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmailWrapper.getAdminEmail()), String.class);
		String organizationName = resp.getBody();
		ResponseEntity<List<Long>> teamIdsRE = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-teams/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});
		
		ArrayList<TeamBasicInformation> toBeReturned = new ArrayList<>();
		
		for(Team team : teamRepo.findAllById(teamIdsRE.getBody()))
		{
			TeamBasicInformation tbi = new TeamBasicInformation();
			tbi.setDescription(team.getDescription());
			tbi.setEmployeeCount(Integer.toUnsignedLong(team.getEmployees().size()));
			tbi.setId(team.getId()); 
			tbi.setName(team.getName());
			toBeReturned.add(tbi);
		}
		
		return toBeReturned;
	}

	@Override
	public Boolean updateEmployees(Long id, List<String> employeeList) {
		
		if(!teamRepo.existsById(id)) throw new TeamNotFoundException();
		
		Team entity = teamRepo.findById(id).get();
		entity.setEmployees(employeeList);
		teamRepo.save(entity);
		
		return true;
	}

	@Override
	public Boolean updateCurrentEmployeeTeams(EmployeeTeamsUpdate employeeTeamsUpdate) {
		
		String employeeEmail = employeeTeamsUpdate.getEmployeeEmail();
		List<Long> updatedTeams = employeeTeamsUpdate.getTeams();
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(employeeEmail), String.class);
		String organizationName = resp.getBody();
		ResponseEntity<List<Long>> teamIdsRE = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-teams/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});
		
		for(Team team : teamRepo.findAllById(teamIdsRE.getBody()))
		{
			if(updatedTeams.contains(team.getId()))
			{
				if(!team.getEmployees().contains(employeeEmail))
				{
					team.getEmployees().add(employeeEmail);
					teamRepo.save(team);
				}
			}
			else
			{
				if(team.getEmployees().contains(employeeEmail))
				{
					team.getEmployees().remove(employeeEmail);
					teamRepo.save(team);
				}
			}
		}
		
		return true;
	}

	@Override
	public Boolean addTeam(NewTeam newTeam) {
		
		String adminEmail =  newTeam.getAdminEmail();
		List<String> orgteams = getAllTeamNames(new AdminEmailWrapper(adminEmail));
		
		if(orgteams.contains(newTeam.getName())) throw new TeamAlreadyExistsException();
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		
		Team entity = new Team();
		
		entity.setDescription(newTeam.getDescription());
		entity.setEmployees(new ArrayList<String>());
		entity.setName(newTeam.getName());
		
		entity = teamRepo.save(entity);
		
		// register with organization
		restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/add-team/"+organizationName+"/"+entity.getId(), HttpMethod.POST, null, Boolean.class);
		
		// add log
		NewLog teamAddedLog = new NewLog();
		teamAddedLog.setAdminEmail(adminEmail);
		teamAddedLog.setType(LogType.TEAM_ADDED);
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(adminEmail), String.class);
		String adminName = adminNameRE.getBody();
		teamAddedLog.setMessage("Team "+ entity.getName()+ " Added by Admin "+adminName+" "+"("+adminEmail+")");
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", teamAddedLog, Boolean.class);
		
		return true;
	}

	@Override
	public Boolean deleteTeam(Long id, AdminEmailWrapper adminEmailWrapper) {
		
		if(!teamRepo.existsById(id)) throw new TeamNotFoundException();
		
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmailWrapper.getAdminEmail()), String.class);
		String organizationName = resp.getBody();
		String adminEmail = adminEmailWrapper.getAdminEmail();
		String teamName = teamRepo.findById(id).get().getName();
		teamRepo.deleteById(id);
		
		// remove from organization
		restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/remove-team/"+organizationName+"/"+id, HttpMethod.DELETE, null, Boolean.class);
		
		
		// add AdminLog
		NewLog teamDeletedLog = new NewLog();
		teamDeletedLog.setAdminEmail(adminEmail);
		teamDeletedLog.setType(LogType.TEAM_REMOVED);
		
		ResponseEntity<String> adminNameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(adminEmail), String.class);
		String adminName = adminNameRE.getBody();
		teamDeletedLog.setMessage("Team "+ teamName+ " Deleted by Admin "+adminName+" "+"("+adminEmail+")");
		
		restTemplate.postForEntity("http://SERVICE-ADMIN-LOGGER/admin-logs/add-log", teamDeletedLog, Boolean.class);
		
		return true;
	}


	@Override
	public Boolean deleteEmployeeFromAllTeams(EmployeeEmailWrapper employeeEmailWrapper) {
		
		String employeeEmail = employeeEmailWrapper.getEmployeeEmail();
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", employeeEmailWrapper, String.class);
		String organizationName = resp.getBody();
		ResponseEntity<List<Long>> teamIdsRE = restTemplate.exchange("http://SERVICE-ORGANIZATION/organization/get-teams/"+organizationName, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});
		
		for(Team team : teamRepo.findAllById(teamIdsRE.getBody()))
		{
			if(team.getEmployees().contains(employeeEmail))
			{
				team.getEmployees().remove(employeeEmail);
				teamRepo.save(team);
			}
		}
		
		return true;
	}

	@Override
	public Boolean addEmployeeToTeam(Long id, EmployeeEmailWrapper employeeEmailWrapper) {
		
		if(!teamRepo.existsById(id)) throw new TeamNotFoundException();
		
		Team entity = teamRepo.findById(id).get();
		entity.getEmployees().add(employeeEmailWrapper.getEmployeeEmail());
		teamRepo.save(entity);
		
		return true;
	}

	
}
