package com.prostaff.service_auth.service.impl;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.prostaff.service_auth.domian.User;
import com.prostaff.service_auth.dto.LoginRequest;
import com.prostaff.service_auth.dto.LoginResponse;
import com.prostaff.service_auth.dto.NewAdmin;
import com.prostaff.service_auth.dto.NewAdminAndOrganizationDetails;
import com.prostaff.service_auth.dto.NewAdminDetails;
import com.prostaff.service_auth.dto.OrderDetails;
import com.prostaff.service_auth.dto.PaymentDetails;
import com.prostaff.service_auth.exception.AdminAlreadyExistsException;
import com.prostaff.service_auth.exception.OrganizationAlreadyExistsException;
import com.prostaff.service_auth.inter_service_communication.dto.AuthRequest;
import com.prostaff.service_auth.inter_service_communication.dto.EmployeeEmailWrapper;
import com.prostaff.service_auth.inter_service_communication.dto.NewUser;
import com.prostaff.service_auth.inter_service_communication.enums.Role;
import com.prostaff.service_auth.repository.UserRepository;
import com.prostaff.service_auth.service.UserService;
import com.prostaff.service_auth.utils.JwtUtils;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
    private RestTemplate restTemplate;

	@Autowired
	private UserRepository db;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Override
	public Boolean addUser(NewUser newUser) {
		User entity = new User();
		
		if(db.existsById(newUser.getEmail())) return false;
		
		entity.setEmail(newUser.getEmail());
		entity.setPassword(newUser.getPassword());
		entity.setFullName(newUser.getFullName());
		entity.setOrganizationName(newUser.getOrganizationName());
		entity.setRole(Role.EMPLOYEE);
		
		db.save(entity);
		return true;
	}

	@Override
	public Boolean deleteUser(EmployeeEmailWrapper employeeEmailWrapper) {
		if(!db.existsById(employeeEmailWrapper.getEmployeeEmail())) return false;
		
		db.deleteById(employeeEmailWrapper.getEmployeeEmail());
		
		return true;
	}

	@Override
	public LoginResponse login(LoginRequest loginRequest) {
		
		if(!db.existsById(loginRequest.getEmail()) || !passwordEncoder.matches(loginRequest.getPassword(), db.findById(loginRequest.getEmail()).get().getPassword()))
		{
			return new LoginResponse("FAILED", null);
		}
		
		LoginResponse loginResponse = new LoginResponse(jwtUtils.generateToken(loginRequest.getEmail()), db.findById(loginRequest.getEmail()).get().getRole());
		return loginResponse;
	}

	@Override
	public Integer validate(AuthRequest authRequest) {
		
		String path = authRequest.getPath();
		System.err.println(path);
		String token = authRequest.getJwtToken();
		token = token.substring(7);
		
		List<String> employeeAccessiblePaths = List.of(
		   "/employee/get-employee-information",
		   "/employee/update-employee-profile-image",
		   "/team/get-employee-teams",
		   "/department/get-employee-department",
		   "/designation/get-employee-designation",
		   "/attendance/get-employee-attendance",
		   "/attendance/check-in",
		   "/attendance/check-out",
		   "/attendance/get-employee-attendance",
		   "/leave-request/get-employee-leave-request",
		   "/leave-request/create-request",
		   "/leave-request/get-leave-requests-count",
		   "/notification/get-emplyoyee-notifications",
		   "/organization/get-organization-data",
		   "/organization/get-faqs",
		   "/organization/get-upcoming-events",
		   "/organization/get-help-details"
		);
		
		try {
			
			String email = jwtUtils.extractUserName(token);
			
			if(!db.existsById(email) || jwtUtils.isTokenExpired(token)) return 1;
			User user = db.findById(email).get();
			if(user.getRole() == Role.ADMIN) return 0;
			
			// user is employee
			if(isValidEmployeePath(path, employeeAccessiblePaths)) return 0;
			return 2;
			
		} catch (Exception e) {return 1;}
	}

	@Override
	public Boolean isUserExist(EmployeeEmailWrapper employeeEmailWrapper) {
		return db.existsById(employeeEmailWrapper.getEmployeeEmail());
	}

	@Override
	public String getUserOrganization(EmployeeEmailWrapper employeeEmailWrapper) {
		if(!db.existsById(employeeEmailWrapper.getEmployeeEmail())) return "";
		return db.findById(employeeEmailWrapper.getEmployeeEmail()).get().getOrganizationName();
	}

	@Override
	public String getFullName(EmployeeEmailWrapper employeeEmailWrapper) {
		if(!db.existsById(employeeEmailWrapper.getEmployeeEmail())) return "NO SUCH USER EXIST";
		return db.findById(employeeEmailWrapper.getEmployeeEmail()).get().getFullName();
	}
	
	private boolean isValidEmployeePath(String path, List<String> allowedPaths)
	{
		for(String allowedPath : allowedPaths)
		{
			if(path.contains(allowedPath)) return true;
		}
		return false;
	}

	@Override
	public OrderDetails getOrderInformation() {
		
		try {
			
			System.setProperty("https.protocols", "TLSv1.2");
			System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
			
            RazorpayClient razorpay = new RazorpayClient("ID", "SECRET");

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", 100); // In paise (INR * 100)
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

            Order order = razorpay.orders.create(orderRequest);
 
            OrderDetails details = new OrderDetails();
            details.setAmount(100l);
            details.setOrderId(order.get("id"));
            
            return details;
        } catch (Exception e) {
            throw new RuntimeException("Error while creating order", e);
        }
	}

	@Override
	public Boolean verifyPayment(PaymentDetails details, MultipartFile orgImage) {
		
		String orderId = details.getRazorpay_order_id();
        String paymentId = details.getRazorpay_payment_id();
        String signature = details.getRazorpay_signature();
        
        if(verifyPayment(orderId, paymentId, signature))
        {
        	User newAdmin = new User();
        	newAdmin.setEmail(details.getAdminEmail());
        	newAdmin.setFullName(details.getAdminFullName());
        	newAdmin.setOrganizationName(details.getOrganizationName());
        	newAdmin.setRole(Role.ADMIN);
        	String randomPassword = generateRandomPassword();
        	newAdmin.setPassword(passwordEncoder.encode(randomPassword));
        	
        	
        	// make new organization
        	String url = "http://SERVICE-ORGANIZATION/organization/register-organization/{organizationName}";
      
            Resource imageResource;
			try {
				imageResource = new InputStreamResource(orgImage.getInputStream()) {
				    @Override
				    public String getFilename() {
				        return orgImage.getOriginalFilename();
				    }
				    @Override
				    public long contentLength() throws IOException {
				        return -1;
				    }
				};
			} catch (IOException e) {return false;}
			
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("organizationImage", imageResource);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            restTemplate.exchange(url, HttpMethod.POST, requestEntity, Boolean.class, details.getOrganizationName());
        	
            
            // save's new admin only if org was successfully registered
            db.save(newAdmin);
            
            System.err.println(newAdmin.getEmail()+"  "+randomPassword);
            
            // register new admin with organization
            restTemplate.postForEntity("http://SERVICE-ORGANIZATION/organization/add-employee/"+details.getOrganizationName(), new EmployeeEmailWrapper(details.getAdminEmail()), Boolean.class);
            
            
            // send mail to admin email with help of Servie-Employee
            NewAdminDetails newAdminDetails = new NewAdminDetails(newAdmin.getEmail(), randomPassword, newAdmin.getFullName());
            restTemplate.postForEntity("http://SERVICE-EMPLOYEE/employee/send-admin-welcome-mail", newAdminDetails, Boolean.class);
            
            restTemplate.postForEntity("http://SERVICE-SALARY/salary/create-organization-fund/"+details.getOrganizationName(), null, Boolean.class);
            
        	return true;
        }
        return false;
	}
	
	private String generateRandomPassword()
	{
		final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
	    final int PASSWORD_LENGTH = 24;
		
		SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        
        return password.toString();
	}
	
	private boolean verifyPayment(String orderId, String paymentId, String signature) {
        try {
        	
        	System.setProperty("https.protocols", "TLSv1.2");
        	System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
        	
            String data = orderId + "|" + paymentId;
            return Utils.verifySignature(data, signature, "SECRET");
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

		
    }

	@Override
	public Boolean validateNewAdminAndOrganization(NewAdminAndOrganizationDetails details) {
		if(restTemplate.getForEntity("http://SERVICE-ORGANIZATION/organization/is-organization-exist/"+details.getOrganizationName(), Boolean.class).getBody())
		{
			throw new OrganizationAlreadyExistsException();
		}
		
		if(isUserExist(new EmployeeEmailWrapper(details.getAdminEmail()))) throw new AdminAlreadyExistsException();
		
		return true;
	}

	@Override
	public Boolean addAdmin(NewAdmin newAdmin) {
		System.out.println("INSIDE ADD ADMIN");
		if(isUserExist(new EmployeeEmailWrapper(newAdmin.getNewAdminEmail()))) throw new AdminAlreadyExistsException();
		
		String adminEmail =  newAdmin.getAdminEmail();
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		
		User user = new User();
    	user.setEmail(newAdmin.getNewAdminEmail());
    	user.setFullName(newAdmin.getNewAdminFullName());
    	user.setOrganizationName(organizationName);
    	user.setRole(Role.ADMIN);
    	String randomPassword = generateRandomPassword();
    	System.err.println("New admin Email:"+newAdmin.getNewAdminEmail()+" new admin password: "+randomPassword);
    	user.setPassword(passwordEncoder.encode(randomPassword));
    	
    	restTemplate.postForEntity("http://SERVICE-ORGANIZATION/organization/add-employee/"+organizationName, 
    			new EmployeeEmailWrapper(user.getEmail()), 
    			Boolean.class);
    	
    	db.save(user);
        
        System.err.println(user.getEmail()+"  "+randomPassword);
        
        NewAdminDetails newAdminDetails = new NewAdminDetails(user.getEmail(), randomPassword, user.getFullName());
        restTemplate.postForEntity("http://SERVICE-EMPLOYEE/employee/send-admin-welcome-mail", newAdminDetails,
        		Boolean.class);
		
		return true;
	}
	
}
