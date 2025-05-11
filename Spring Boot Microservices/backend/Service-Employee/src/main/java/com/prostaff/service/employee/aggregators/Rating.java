package com.prostaff.service.employee.aggregators;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Rating {

	Integer punctuality;
	Integer performance;
	Integer softSkills;
	Integer creativity;
}
