package com.prostaff.service_salary.dto;

import java.sql.Date;
import java.sql.Time;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.prostaff.service_salary.dto.enums.Status;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Record {
	
	Long id;
	Date date;
	Status status;
	Time checkIn;
	Time checkOut;
}
