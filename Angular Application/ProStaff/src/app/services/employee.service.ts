import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, Observable, throwError } from 'rxjs';
import { AddEmployeeRequest } from '../models/employee.model';
// models/organization-employee.model.ts
export interface OrganizationEmployee {
  fullName: string;
  email: string;
  gender: 'MALE' | 'FEMALE';
  age: number;
  profileImage: string;
  department: string;
  designation: string;
}
export interface EmployeeInfo {
  email: string;
  fullName: string;
  gender: 'MALE' | 'FEMALE';
  skills: string[];
  reportingManager: string;
  phoneNumber: string;
  dob: string; // ISO string format
  profileImage: string;
  joiningDate: string;
  emergencyContact: string;
  bankDetail: {
    bankName: string;
    ifscCode: string;
    accountNumber: string;
    branch: string;
  };
  rating: {
    punctuality: number;
    performance: number;
    softSkills: number;
    creativity: number;
  };
  age: number;
  department: {
    id: number;
    name: string;
    description: string;
    employeeCount: number;
  };
  designation: {
    id: number;
    name: string;
    description: string;
    employeeCount: number;
  };
  teams: {
    id: number;
    name: string;
    description: string;
    employeeCount: number;
  }[];
  address: string;
}
@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private baseUrl = 'http://localhost:5555';

  constructor(private http: HttpClient) {}

  getAllOrganizationEmployees(adminEmail: string): Observable<OrganizationEmployee[]> {
    const url = `${this.baseUrl}/employee/get-all-organization-employees`;
    const body = { adminEmail };
    return this.http.post<OrganizationEmployee[]>(url, body);
  }

  getEmployeeInfo(email: string): Observable<EmployeeInfo> {
    return this.http.post<EmployeeInfo>(`${this.baseUrl}/employee/get-employee-information`, {
      employeeEmail: email
    });
  }

  updateEmployee(employeeData: any): Observable<boolean> {
    return this.http.put<boolean>(`${this.baseUrl}/employee/update-employee`, employeeData);
  } 

  updateProfileImage(formData: FormData) {
    return this.http.put(`${this.baseUrl}/employee/update-employee-profile-image`, formData, {
      reportProgress: true,
      observe: 'events'
    });
  }

  addEmployee(payload: any): Observable<boolean> {
    return this.http.post<boolean>(`${this.baseUrl}/employee/add-employee`, payload);
  }

  deleteEmployee(employeeEmail: string, adminEmail: string): Observable<boolean> {
    const url = `${this.baseUrl}/employee/delete-employee`;
    const body = { employeeEmail, adminEmail };

    return this.http.delete<boolean>(url, { body });
  }

  private handleError(error: HttpErrorResponse) {
    console.error('API Error:', error);
    return throwError(() => new Error('Something went wrong; please try again later.'));
  }
}
