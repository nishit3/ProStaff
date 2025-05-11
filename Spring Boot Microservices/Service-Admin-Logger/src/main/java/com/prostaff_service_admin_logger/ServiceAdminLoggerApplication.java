package com.prostaff_service_admin_logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ServiceAdminLoggerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceAdminLoggerApplication.class, args);
	}
	
	@Bean 
	@LoadBalanced
	RestTemplate restTemplate(){
	    return new RestTemplate();
	}

}
