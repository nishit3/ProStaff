import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
export interface LeaveRequest {
  id: number;
  employee_email: string;
  requestDate: string;
  leaveDate: string;
  reason: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  type: 'PAID' | 'UNPAID' | 'SICK';
}
export interface LeaveRequestEmp {
  // id: number;
  employee_email: string;
  requestDate: string; // ISO date string
  leaveDate: string;   // ISO date string
  reason: string;
  status: string; // e.g., "PENDING", "APPROVED", "DECLINED"
  type: string;   // e.g., "Paid Leave", "Sick Leave", etc.
}
export interface EmployeeEmailWrapperWithOnlyPending {
  employeeEmail: string;
  onlyPending: boolean;
}
export interface LeaveCountResponse {
  sickLeaves: number;
  paidLeaves: number;
  unPaidLeaves: number;
}
@Injectable({
  providedIn: 'root'
})
export class LeaveRequestService {

  private baseUrl = 'http://localhost:5555';

  constructor(private http: HttpClient, private router: Router) { }

  createRequest(leaveRequest: LeaveRequestEmp): Observable<boolean> {
    return this.http.post<boolean>(`${this.baseUrl}/leave-request/create-request`, leaveRequest);
  }
  getAllLeaveRequests(adminEmail: string): Observable<LeaveRequest[]> {
    const url = `${this.baseUrl}/leave-request/get-all-leave-request`;
    return this.http.post<LeaveRequest[]>(url, { adminEmail });
  }

  grantLeaveRequest(id: number, adminEmail: string): Observable<boolean> {
    const url = `${this.baseUrl}/leave-request/grant/${id}`;
    const body = { adminEmail };
    return this.http.put<boolean>(url, body);
  }
  getLeaveRequestsCount(employeeEmail: string): Observable<LeaveCountResponse> {
    const url = `${this.baseUrl}/leave-request/get-leave-requests-count`;
    const body = {
      employeeEmail: employeeEmail
    };

    return this.http.post<LeaveCountResponse>(url, body);
  }

  denyLeaveRequest(id: number, adminEmail: string): Observable<boolean> {
    const url = `${this.baseUrl}/leave-request/deny/${id}`;
    const body = { adminEmail };
    return this.http.put<boolean>(url, body);
  }

  getEmployeeLeaveRequest(employeeEmail: string): Observable<LeaveRequest[]> {
    const body: EmployeeEmailWrapperWithOnlyPending = { employeeEmail, onlyPending: false };
    return this.http.post<LeaveRequest[]>(`${this.baseUrl}/leave-request/get-employee-leave-request`, body);
  }

}
