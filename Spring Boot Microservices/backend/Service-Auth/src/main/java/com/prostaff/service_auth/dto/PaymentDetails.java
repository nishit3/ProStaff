package com.prostaff.service_auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetails {
	
	String razorpay_order_id;
	String razorpay_payment_id;
	String razorpay_signature;
	String organizationName;
	String adminFullName;
	String adminEmail;
	
}
