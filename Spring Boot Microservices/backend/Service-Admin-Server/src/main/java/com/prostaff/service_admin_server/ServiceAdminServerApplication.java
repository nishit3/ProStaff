package com.prostaff.service_admin_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

@SpringBootApplication
@EnableAdminServer
public class ServiceAdminServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceAdminServerApplication.class, args);
	}

}
