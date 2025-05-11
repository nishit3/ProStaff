package com.prostaff.service.employee.dto;

import lombok.Data;

@Data
public class BankDetail
{
	String bankName; 
	String ifscCode; 
	String accountNumber; 
	String branch;
}