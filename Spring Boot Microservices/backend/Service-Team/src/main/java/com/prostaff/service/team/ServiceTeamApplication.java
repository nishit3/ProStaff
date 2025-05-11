package com.prostaff.service.team;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceTeamApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceTeamApplication.class, args);
	}
	
	@Bean 
	@LoadBalanced
	RestTemplate restTemplate(){
	    return new RestTemplate();
	}

}
