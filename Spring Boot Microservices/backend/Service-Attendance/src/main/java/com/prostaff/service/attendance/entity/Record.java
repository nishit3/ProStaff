package com.prostaff.service.attendance.entity;

import java.sql.Date;
import java.sql.Time;

import com.prostaff.service.attendance.inter_service_communication.enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Record {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	Date date;
	@Enumerated(EnumType.STRING)
	Status status;
	Time checkIn;
	Time checkOut;
}
