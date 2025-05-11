package com.prostaff.service.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prostaff.service.team.entity.Team;

public interface TeamRepo extends JpaRepository<Team, Long>{
	
}
