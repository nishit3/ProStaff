package com.prostaff.service.employee.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(String to, String recepiantName, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to ProStaff – Your Login Credentials");
        String body = "Dear "+recepiantName+",\r\n"
        		+ "\r\n"
        		+ "Welcome to ProStaff! We’re excited to have you on board and look forward to working together. Below are your login credentials to access our Employee Management System:\r\n"
        		+ "\r\n"
        		+ "🔹 Email: "+to+"\r\n"
        		+ "🔹 Temporary Password: "+password+"\r\n"
        		+ "\r\n"
        		+ "Next Steps:\r\n"
        		+ "1️⃣ Use the above credentials to log in for the first time.\r\n"
        		+ "2️⃣ You can change your password post login.\r\n"
        		+ "3️⃣ Explore your dashboard, attendance details, and much more.\r\n"
        		+ "\r\n"
        		+ "If you have any questions or need assistance, feel free to contact prostaffems@gmail.com.\r\n"
        		+ "\r\n"
        		+ "Once again, welcome to ProStaff! Wishing you a successful and fulfilling journey with us.\r\n"
        		+ "\r\n"
        		+ "Best Regards,\r\n"
        		+ "ProStaff – Employee Management System\r\n"
        		+ "\r\n"
        		+ "";
        
        message.setText(body);
        message.setFrom("prostaffems@gmail.com");

        mailSender.send(message);
    }
    
    public void sendResetPasswordEmail(String to, String recepiantName, String token)
    {
    	SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("ProStaff Password Reset Link");
        String body = "Dear "+recepiantName+",\r\n"
        		+ "\r\n"
        		+ "Please click on the below link to reset your ProStaff account password.\r\n"
        		+ "\n\n"
        		+ "http://localhost:4200/Zp4Lq6dYtXv0RAfMnJw82EoKCyHgBb9TuVNsx3QZLiPmWkUDG7rFahceoMTlXq1SvnbJy?token="+token+"\r\n"
        		+ "\r\n"
        		+ "Best Regards,\r\n"
        		+ "ProStaff – Employee Management System\r\n"
        		+ "\r\n"
        		+ "";
        
        message.setText(body);
        message.setFrom("prostaffems@gmail.com");

        mailSender.send(message);
    }
    
    public void sendAdminWelcomeEmail(String to, String recepiantName, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to ProStaff Administrator – Your Login Credentials");
        String body = "Dear "+recepiantName+",\r\n"
        		+ "\r\n"
        		+ "Welcome to ProStaff! We’re excited to have you on board as an admin, and look forward to working together. Below are your login credentials to access our Employee Management System:\r\n"
        		+ "\r\n"
        		+ "🔹 Email: "+to+"\r\n"
        		+ "🔹 Temporary Password: "+password+"\r\n"
        		+ "\r\n"
        		+ "Next Steps:\r\n"
        		+ "1️⃣ Use the above credentials to log in for the first time.\r\n"
        		+ "2️⃣ You can change your password post login.\r\n"
        		+ "3️⃣ Explore your organization dashboard, employee details, teams details, targeted notification service, admin logs, leave request management, and much more.\r\n"
        		+ "\r\n"
        		+ "If you have any questions or need assistance, feel free to contact prostaffems@gmail.com.\r\n"
        		+ "\r\n"
        		+ "Once again, welcome to ProStaff! Wishing you a successful and fulfilling journey with us.\r\n"
        		+ "\r\n"
        		+ "Best Regards,\r\n"
        		+ "ProStaff – Employee Management System\r\n"
        		+ "\r\n"
        		+ "";
        
        message.setText(body);
        message.setFrom("prostaffems@gmail.com");

        mailSender.send(message);
    }
}

