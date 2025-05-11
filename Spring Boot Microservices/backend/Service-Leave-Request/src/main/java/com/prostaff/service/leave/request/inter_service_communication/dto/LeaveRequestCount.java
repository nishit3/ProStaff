package com.prostaff.service.leave.request.inter_service_communication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequestCount {
	
	Integer sickLeaves;
	Integer paidLeaves;
	Integer unPaidLeaves;
}
