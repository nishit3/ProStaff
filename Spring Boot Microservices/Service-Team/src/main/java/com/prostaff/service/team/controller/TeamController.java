package com.prostaff.service.team.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prostaff.service.team.inter_service_communication.dto.AdminEmailWrapper;
import com.prostaff.service.team.inter_service_communication.dto.EmployeeBasicInformation;
import com.prostaff.service.team.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service.team.inter_service_communication.dto.EmployeeTeamsUpdate;
import com.prostaff.service.team.inter_service_communication.dto.NewTeam;
import com.prostaff.service.team.inter_service_communication.dto.TeamBasicInformation;
import com.prostaff.service.team.service.TeamService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/team")
@Tag(name = "TeamController")
public class TeamController {

	@Autowired
	private TeamService teamService;
	
	@PostMapping("/get-all-team-names")
	public ResponseEntity<List<String>> getAllTeamNames(@RequestBody AdminEmailWrapper adminEmailWrapper){
		return new ResponseEntity<List<String>>(teamService.getAllTeamNames(adminEmailWrapper),HttpStatus.OK);
	}
	
	@PostMapping("/get-employee-teams")
	public ResponseEntity<List<TeamBasicInformation>> getEmployeeTeams(@RequestBody EmployeeEmailWrapper employeeEmailWrapper){
		return new ResponseEntity<List<TeamBasicInformation>>(teamService.getEmployeeteams(employeeEmailWrapper),HttpStatus.OK);
	}
	
	@GetMapping("/get-all-employees/{id}")
	public ResponseEntity<List<EmployeeBasicInformation>> getAllEmployee(@PathVariable Long id){
		return new ResponseEntity<List<EmployeeBasicInformation>>(teamService.getAllEmployee(id),HttpStatus.OK);
	}
	
	@PostMapping("/get-all-teams")
	public ResponseEntity<List<TeamBasicInformation>> getAllTeams(@RequestBody AdminEmailWrapper adminEmailWrapper){
		return new ResponseEntity<List<TeamBasicInformation>>(teamService.getAllteams(adminEmailWrapper),HttpStatus.OK);
	}
	
	@PutMapping("/update-employees/{id}")
	public ResponseEntity<Boolean> updateEmployees(@PathVariable Long id, @RequestBody List<String> employeList){
		return new ResponseEntity<Boolean>(teamService.updateEmployees(id, employeList),HttpStatus.OK);
	}
	
	@PutMapping("/update-current-employee-teams")
	public ResponseEntity<Boolean> updateCurentEmployeeTeams(@RequestBody EmployeeTeamsUpdate employeeTeamsUpdate){
		return new ResponseEntity<Boolean>(teamService.updateCurrentEmployeeTeams(employeeTeamsUpdate),HttpStatus.OK);
	}
	
	@PostMapping("/add-team")
	public ResponseEntity<Boolean> addTeam(@RequestBody NewTeam newTeam){
		return new ResponseEntity<Boolean>(teamService.addTeam(newTeam),HttpStatus.OK);
	}
	
	@DeleteMapping("/delete-team/{id}")
	public ResponseEntity<Boolean> deleteTeam(@PathVariable Long id,@RequestBody AdminEmailWrapper adminEmailWrapper){
		return new ResponseEntity<Boolean>(teamService.deleteTeam(id, adminEmailWrapper),HttpStatus.OK);
	}
	
	@DeleteMapping("/delete-employee-from-all-teams")
	public ResponseEntity<Boolean> deleteEmployeeFromAllTeams(@RequestBody EmployeeEmailWrapper employeeEmailWrapper)
	{
		return new ResponseEntity<Boolean>(teamService.deleteEmployeeFromAllTeams(employeeEmailWrapper),HttpStatus.OK);
	}
	
	@PostMapping("/add-employee-to-team/{id}")
	public ResponseEntity<Boolean> addEmployeeToTeam(@PathVariable Long id, @RequestBody EmployeeEmailWrapper employeeEmailWrapper)
	{
		return new ResponseEntity<Boolean>(teamService.addEmployeeToTeam(id, employeeEmailWrapper), HttpStatus.OK);
	}
	
}
