package com.prostaff.service_salary.service.impl;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.prostaff.service_salary.dto.AdminEmailWrapper;
import com.prostaff.service_salary.dto.EmployeeEmailWrapper;
import com.prostaff.service_salary.dto.EmployeeEmailWrapperWithOnlyPending;
import com.prostaff.service_salary.dto.FundOrderDetails;
import com.prostaff.service_salary.dto.FundsPaymentDetails;
import com.prostaff.service_salary.dto.LeaveRequest;
import com.prostaff.service_salary.dto.Record;
import com.prostaff.service_salary.dto.enums.LeaveRequestStatus;
import com.prostaff.service_salary.dto.enums.LeaveRequestType;
import com.prostaff.service_salary.dto.enums.Status;
import com.prostaff.service_salary.entity.OrganizationFund;
import com.prostaff.service_salary.entity.Salary;
import com.prostaff.service_salary.entity.SalaryLog;
import com.prostaff.service_salary.exception.EmployeeAlreadyExistsException;
import com.prostaff.service_salary.exception.EmployeeNotFoundException;
import com.prostaff.service_salary.exception.InsufficientOrganizationFundException;
import com.prostaff.service_salary.exception.OrganizationNotFoundException;
import com.prostaff.service_salary.repository.OrganizationFundRepo;
import com.prostaff.service_salary.repository.SalaryLogsRepo;
import com.prostaff.service_salary.repository.SalaryRepo;
import com.prostaff.service_salary.service.SalaryService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

@Service
public class SalaryServiceImpl implements SalaryService{

	@Autowired
	private SalaryRepo salaryRepo;
	
	@Autowired
	private SalaryLogsRepo logsRepo;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private OrganizationFundRepo orgRepo;
	
	@Override
	public Long getEmployeeSalary(EmployeeEmailWrapper employeeEmailWrapper) {
		String employeeEmail = employeeEmailWrapper.getEmployeeEmail();
		if(!salaryRepo.existsById(employeeEmail)) throw new EmployeeNotFoundException();
		return salaryRepo.findById(employeeEmail).get().getEmployeeSalary();
	}

	@Override
	public Boolean createOrganizationFund(String organizationName) {
		if(orgRepo.existsById(organizationName)) return false;
		
		OrganizationFund entity = new OrganizationFund();
		
		entity.setAvailableFund(0L);
		entity.setLogs(new ArrayList<SalaryLog>());
		entity.setOrganization(organizationName);
		
		orgRepo.save(entity);
		
		return true;
	}

	@Override
	public Boolean addEmployeeSalary(EmployeeEmailWrapper employeeEmailWrapper, Long salary) {
		String employeeEmail = employeeEmailWrapper.getEmployeeEmail();
		if(salaryRepo.existsById(employeeEmail)) throw new EmployeeAlreadyExistsException();
		
		Salary entity = new Salary();
		entity.setEmployeeEmail(employeeEmail);
		entity.setEmployeeSalary(salary);
		
		salaryRepo.save(entity);
		
		return true;
	}

	@Override
	public Boolean deleteEmployeeSalary(EmployeeEmailWrapper employeeEmailWrapper) {
		String employeeEmail = employeeEmailWrapper.getEmployeeEmail();
		if(!salaryRepo.existsById(employeeEmail)) throw new EmployeeNotFoundException();
		salaryRepo.deleteById(employeeEmail);
		return true;
	}

	@Override
	public Boolean updateEmployeeSalary(Long newSalary, EmployeeEmailWrapper employeeEmailWrapper) {
		if(newSalary < 0) return false; 
		String employeeEmail = employeeEmailWrapper.getEmployeeEmail();
		if(!salaryRepo.existsById(employeeEmail)) throw new EmployeeNotFoundException();
		Salary entity = salaryRepo.findById(employeeEmail).get();
		entity.setEmployeeSalary(newSalary);
		salaryRepo.save(entity);
		return true;
	}

	@Override
	public Long getCurrentOrganizationFund(AdminEmailWrapper adminEmailWrapper) {
		String adminEmail = adminEmailWrapper.getAdminEmail();
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		if(!orgRepo.existsById(organizationName)) throw new OrganizationNotFoundException();
		
		return orgRepo.findById(organizationName).get().getAvailableFund();
	}

	@Override
	public Boolean rollOutSalary(AdminEmailWrapper adminEmailWrapper, List<String> employeeEmails) {
		String adminEmail = adminEmailWrapper.getAdminEmail();
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		
		if(!orgRepo.existsById(organizationName)) throw new OrganizationNotFoundException();
		
		Long availableFund = orgRepo.findById(organizationName).get().getAvailableFund();
		Long totalRemuneration = getTotalRemuneration(adminEmailWrapper, employeeEmails);
		
		if(availableFund < totalRemuneration) throw new InsufficientOrganizationFundException();
		
		OrganizationFund entity =  orgRepo.findById(organizationName).get();
		
		for(String employeeEmail : employeeEmails)
		{
			if(!salaryRepo.existsById(employeeEmail)) throw new EmployeeNotFoundException();
			
			Long employeeRemuneration = getEmployeeRemuneration(employeeEmail);
			
			if(employeeRemuneration <= entity.getAvailableFund())
			{
				entity.setAvailableFund(entity.getAvailableFund() - employeeRemuneration);
				
				// API CALL TO 3RD PARTY PAYMENT VENDOR WILL COME HERE IN FUTURE FOR DEBIT TRANSACTION
				
				SalaryLog newSalaryLog = new SalaryLog();
				
				newSalaryLog.setDate(Date.valueOf(LocalDate.now()));
				newSalaryLog.setTime(Time.valueOf(LocalTime.now()));
				newSalaryLog.setEmployeeEmail(employeeEmail);
				
				ResponseEntity<String> nameRE = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-fullname", new EmployeeEmailWrapper(employeeEmail), String.class);
				String fullName = nameRE.getBody();
				
				newSalaryLog.setMessage("INR "+employeeRemuneration+" debited to "+fullName+" ("+employeeEmail+")");
				
				newSalaryLog = logsRepo.save(newSalaryLog);
				entity.getLogs().add(newSalaryLog);
				orgRepo.save(entity);
			}
			else
			{
				throw new InsufficientOrganizationFundException();
			}
			
		}
		
		return true;
	}

	@Override
	public Long getTotalRemuneration(AdminEmailWrapper adminEmailWrapper, List<String> employeeEmails) {
		String adminEmail = adminEmailWrapper.getAdminEmail();
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		if(!orgRepo.existsById(organizationName)) throw new OrganizationNotFoundException();
		
		Long totalRemuneration = 0L;
		
		for(String employeeEmail : employeeEmails)
		{
			totalRemuneration += getEmployeeRemuneration(employeeEmail);
		}
		
		return totalRemuneration;
	}
	
	private Long getEmployeeRemuneration(String employeeEmail)
	{
		if(!salaryRepo.existsById(employeeEmail)) throw new EmployeeNotFoundException();
		
		Long employeeRenumeration = salaryRepo.findById(employeeEmail).get().getEmployeeSalary();
		
		// fetch all previous month attendance records
		List<Record> attendanceRecords = getPreviousMonthRecords(employeeEmail);
		List<LeaveRequest> acceptedLeaveRequests = getAcceptedLeaveRequestsFromPreviousMonth(employeeEmail);
		
		
		int totalDays = attendanceRecords.size() - totalHolidays(attendanceRecords);
		if(totalDays == 0) return 0L;
		
		double ratio =  ((double)(totalDays - totalAbsents(attendanceRecords) - totalUnpaidAndSickLeaves(acceptedLeaveRequests))) / totalDays;
		
		return (long)((double)employeeRenumeration * ratio);
	}
	
	private int totalAbsents(List<Record> attendanceRecords)
	{
		int count = 0;
		for(Record rcrd : attendanceRecords)
		{
			if(rcrd.getStatus() == Status.ABSENT) count++;
		}
		return count;
	}
	
	private int totalHolidays(List<Record> attendanceRecords)
	{
		int count = 0;
		for(Record rcrd : attendanceRecords)
		{
			if(rcrd.getStatus() == Status.HOLIDAY) count++;
		}
		return count;
	}
	
	private int totalUnpaidAndSickLeaves(List<LeaveRequest> acceptedLeaveRequests)
	{
		int count = 0;
		
		for(LeaveRequest req : acceptedLeaveRequests)
		{
			if(req.getType() == LeaveRequestType.UNPAID || req.getType() == LeaveRequestType.SICK) count++;
		}
		
		return count;
	}
	
	private List<Record> getPreviousMonthRecords(String employeeEmail) {
		
		List<Record> attendanceRecords = restTemplate.exchange(
				"http://SERVICE-ATTENDANCE/attendance/get-employee-attendance", 
				HttpMethod.POST,
				new HttpEntity<EmployeeEmailWrapper>(new EmployeeEmailWrapper(employeeEmail)),
				new ParameterizedTypeReference<List<Record>> (){}
			).getBody();
		 
        LocalDate currentDate = LocalDate.now();
        LocalDate firstDayOfCurrentMonth = currentDate.withDayOfMonth(1);
        LocalDate firstDayOfPreviousMonth = firstDayOfCurrentMonth.minusMonths(1);
        LocalDate lastDayOfPreviousMonth = firstDayOfCurrentMonth.minusDays(1);

        return attendanceRecords.stream()
                .filter(record -> {
                    LocalDate recordDate = record.getDate().toLocalDate();
                    return (recordDate.isEqual(firstDayOfPreviousMonth) || recordDate.isAfter(firstDayOfPreviousMonth))
                            && (recordDate.isEqual(lastDayOfPreviousMonth) || recordDate.isBefore(firstDayOfCurrentMonth));
                })
                .collect(Collectors.toList());
	 }
	
	private List<LeaveRequest> getAcceptedLeaveRequestsFromPreviousMonth(String employeeEmail) {
		
		List<LeaveRequest> leaveRequests = restTemplate.exchange(
				"http://SERVICE-LEAVE-REQUEST/leave-request/get-employee-leave-request", 
				HttpMethod.POST,
				new HttpEntity<EmployeeEmailWrapperWithOnlyPending>(new EmployeeEmailWrapperWithOnlyPending(employeeEmail, false)),
				new ParameterizedTypeReference<List<LeaveRequest>> (){}
			).getBody();
		
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfCurrentMonth = now.withDayOfMonth(1);
        LocalDate firstDayOfPreviousMonth = firstDayOfCurrentMonth.minusMonths(1);
        LocalDate lastDayOfPreviousMonth = firstDayOfCurrentMonth.minusDays(1);

        return leaveRequests.stream()
                .filter(request -> request.getStatus() == LeaveRequestStatus.ACCEPTED)
                .filter(request -> {
                    LocalDate requestDate = request.getRequestDate().toLocalDate();
                    return !requestDate.isBefore(firstDayOfPreviousMonth) && !requestDate.isAfter(lastDayOfPreviousMonth);
                })
                .collect(Collectors.toList());
    }

	@Override
	public List<SalaryLog> getSalaryLogs(AdminEmailWrapper adminEmailWrapper) {
		String adminEmail = adminEmailWrapper.getAdminEmail();
		ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(adminEmail), String.class);
		String organizationName = resp.getBody();
		if(!orgRepo.existsById(organizationName)) throw new OrganizationNotFoundException();
		
		return orgRepo.findById(organizationName).get().getLogs();
	}

	@Override
	public FundOrderDetails addFunds(Long amount) {
	
		try {	
			System.setProperty("https.protocols", "TLSv1.2");
			System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
			
            RazorpayClient razorpay = new RazorpayClient("rzp_test_shfEe2LrMp8nj7", "zd26d14FuCkuGSsn3S8DCe34");

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100); // In paise (INR * 100)
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
 
            Order order = razorpay.orders.create(orderRequest);
 
            FundOrderDetails details = new FundOrderDetails();
            details.setOrderId(order.get("id"));
            
            return details;
        } catch (Exception e) {
            throw new RuntimeException("Error while creating order", e);
        }
	}

	@Override
	public Boolean verifyPayment(FundsPaymentDetails details) {
		if(details.getAmount() < 0) return false;
		
		String orderId = details.getRazorpay_order_id();
        String paymentId = details.getRazorpay_payment_id();
        String signature = details.getRazorpay_signature();
        
        if(verifyPayment(orderId, paymentId, signature))
        {
        	ResponseEntity<String> resp = restTemplate.postForEntity("http://SERVICE-AUTH/auth/get-user-organization", new EmployeeEmailWrapper(details.getAdminEmail()), String.class);
    		String organizationName = resp.getBody();
    		if(!orgRepo.existsById(organizationName)) throw new OrganizationNotFoundException();
    		
    		OrganizationFund entity =  orgRepo.findById(organizationName).get();
    		entity.setAvailableFund(entity.getAvailableFund() + details.getAmount());
    		orgRepo.save(entity);
        	
        	return true;
        }
		return false;
	}

	private boolean verifyPayment(String orderId, String paymentId, String signature) {
      try {
      	
      	System.setProperty("https.protocols", "TLSv1.2");
      	System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
      	
          String data = orderId + "|" + paymentId;
          return Utils.verifySignature(data, signature, "zd26d14FuCkuGSsn3S8DCe34");
          
      } catch (Exception e) {
          e.printStackTrace();
          return false;
      }

   }

}
