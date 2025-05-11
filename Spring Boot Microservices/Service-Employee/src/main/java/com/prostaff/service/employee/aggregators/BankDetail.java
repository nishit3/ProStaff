package com.prostaff.service.employee.aggregators;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class BankDetail {

	String bankName;
	String ifscCode;
	String accountNumber;
	String branch;
}
