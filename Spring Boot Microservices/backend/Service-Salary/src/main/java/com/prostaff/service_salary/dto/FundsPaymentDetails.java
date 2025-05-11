package com.prostaff.service_salary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FundsPaymentDetails {
	
	String razorpay_order_id;
	String razorpay_payment_id;
	String razorpay_signature;
	String adminEmail;
	Long amount;
	
}
