import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Rating {
  punctuality: number;
  performance: number;
  softSkills: number;
  creativity: number;
}

export interface DepartmentBasicInformation {
  id: number;
  name: string;
}

export interface DesignationBasicInformation {
  id: number;
  name: string;
}

export interface TeamBasicInformation {
  id: number;
  name: string;
}

export interface EmployeeInformation {
  email: string;
  fullName: string;
  gender: string;      // e.g. 'Male', 'Female', etc.
  skills: string[];
  reportingManager: string;
  phoneNumber: string;
  address: string;
  dob: string;         // ISO date string (e.g. "1980-05-15")
  profileImage: string; // e.g. Base64-encoded string
  joiningDate: string; // ISO date string
  emergencyContact: string;
  bankDetail: any;     // Change the type if you have a proper interface
  rating: Rating;
  age: number;
  department: DepartmentBasicInformation;
  designation: DesignationBasicInformation;
  teams: TeamBasicInformation[];
}

export interface EmployeeEmailWrapper {
  employeeEmail: string;
}

@Injectable({
  providedIn: 'root'
})
export class EmpInformationService {

  private baseUrl = 'http://localhost:5555/employee';

  constructor(private http: HttpClient) {}

  getEmployeeInformation(employeeEmail: string): Observable<EmployeeInformation> {
    const body: EmployeeEmailWrapper = { employeeEmail };
    return this.http.post<EmployeeInformation>(`${this.baseUrl}/get-employee-information`, body);
  }
}
