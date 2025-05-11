import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
export interface SalaryLog {
  id: number;
  employeeEmail: string;
  date: string;  // ISO string
  time: string;  // ISO string
  message: string;
}
@Injectable({
  providedIn: 'root'
})
export class SalaryService {

  private baseUrl = 'http://localhost:5555';

  constructor(private http: HttpClient, private router: Router) { }

  getEmployeeSalary(employeeEmail: string): Observable<number> {
    const body = { employeeEmail };
    return this.http.post<number>(`${this.baseUrl}/salary/get-employee-salary`, body);
  }

  addFunds(amount: number): Observable<{ orderId: string }> {
    const url = `${this.baseUrl}/salary/add-funds/${amount}`;
    return this.http.post<{ orderId: string }>(url, {});
  }

  getTotalRemuneration(adminEmail: string, employeeEmails: string[]): Observable<number> {
    const url = `${this.baseUrl}/salary/get-total-remuneration`;  // Update with full URL if needed (e.g., `${environment.apiBaseUrl}/salary/get-total-remuneration`)

    const formData = new FormData();
    const adminEmailWrapper = { "adminEmail": adminEmail }
    formData.append('adminEmailWrapper', new Blob([JSON.stringify(adminEmailWrapper)], { type: 'application/json' }));
    formData.append('employeeEmails', new Blob([JSON.stringify(employeeEmails)], { type: 'application/json' }));


    return this.http.post<number>(url, formData);
  }

  getSalaryLogs(adminEmail: string) {
    const body = { adminEmail };
    return this.http.post<SalaryLog[]>(`${this.baseUrl}/salary/get-salary-logs`, body);
  }

  rolloutSalary(adminEmail: string, employeeEmails: string[]): Observable<boolean> {
    const formData = new FormData();
    const adminEmailWrapper = { "adminEmail": adminEmail }
    formData.append('adminEmailWrapper', new Blob([JSON.stringify(adminEmailWrapper)], { type: 'application/json' }));
    formData.append('employeeEmails', new Blob([JSON.stringify(employeeEmails)], { type: 'application/json' }));
    return this.http.post<boolean>(`${this.baseUrl}/salary/rollout-salary`, formData);
  }


  verifyFundPayment(payload: {
    razorpay_order_id: string;
    razorpay_payment_id: string;
    razorpay_signature: string;
    adminEmail: string;
    amount: number;
  }): Observable<boolean> {
    const url = `${this.baseUrl}/salary/verify-fund-payment`;
    return this.http.post<boolean>(url, payload);
  }


  updateEmployeeSalary(newSalary: number, employeeEmail: string): Observable<boolean> {
    const url = `${this.baseUrl}/salary/update-employee-salary/${newSalary}`;
    const body = {
      employeeEmail: employeeEmail
    };

    return this.http.put<boolean>(url, body);
  }

  getCurrentOrganizationFund(adminEmail: string): Observable<number> {
    const url = `${this.baseUrl}/salary/get-current-organization-fund`;
    const body = {
      adminEmail: adminEmail
    };

    return this.http.post<number>(url, body);
  }
}
