package com.prostaff.service_gateway.dto;


public class AuthRequest {
	
	String jwtToken;
	String path;
	
	public AuthRequest(String jwtToken, String path) {
		super();
		this.jwtToken = jwtToken;
		this.path = path;
	}
	
	public String getJwtToken() {
		return jwtToken;
	}
	
	public String getPath() {
		return path;
	} 
	
}