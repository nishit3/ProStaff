package com.prostaff.service_organization.aggregator;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor

public class HelpDetails {
	String email;
	String phoneNumber;
}
