package com.prostaff.service_auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ServiceAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceAuthApplication.class, args);
	}

    @Bean
    BCryptPasswordEncoder passEncoder() {
		return new BCryptPasswordEncoder(12);
	}
    
    @Bean
    @LoadBalanced
    RestTemplate restTemplate()
    {
    	return new RestTemplate();
    }

}
