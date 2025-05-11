package com.prostaff.service.employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ServiceEmployeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceEmployeeApplication.class, args);
	}
	
	@Bean 
	@LoadBalanced
	RestTemplate restTemplate(){
	    return new RestTemplate();
	}
	
	@Bean
    BCryptPasswordEncoder passEncoder() {
		return new BCryptPasswordEncoder(12);
	}

}
