package com.prostaff.service.employee.inter_service_communication.dto;

import lombok.Data;

@Data
public class BankDetail
{
	String bankName; 
	String ifscCode; 
	String accountNumber; 
	String branch;
}
