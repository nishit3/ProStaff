import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { OrganizationEmployee } from './employee.service';
// department.model.ts (optional file)
export interface Department {
  id: number;
  name: string;
  description: string;
  employeeCount: number;
}
export interface AddDepartmentRequest {
  name: string;
  description: string;
  adminEmail: string;
}

@Injectable({
  providedIn: 'root'
})
export class DepartmentServiceService {

  private baseUrl = 'http://localhost:5555';

  constructor(private http: HttpClient,private router:Router) {}

  getAllDepartmentNames(adminEmail: string): Observable<string[]> {
    const url = `${this.baseUrl}/department/get-all-department-names`;
    const body = { adminEmail };
    return this.http.post<string[]>(url, body);
  }

  getAllDepartments(adminEmail: string): Observable<Department[]> {
    const url = `${this.baseUrl}/department/get-all-departments`;
    return this.http.post<Department[]>(url, { adminEmail });
  }

  deleteDepartment(departmentId: number, adminEmail: string): Observable<boolean> {
    const url = `${this.baseUrl}/department/delete-department/${departmentId}`;
    const options = {
      body: { adminEmail }
    };
    return this.http.delete<boolean>(url, options);
  }

  updateCurrentDepartment(departmentId: number, employeeEmail: string): Observable<boolean> {
    const payload = { employeeEmail };
    return this.http.put<boolean>(`${this.baseUrl}/department/update-current-department/${departmentId}`, payload);
  }

  getAllEmployeesByDepartmentId(departmentId: number): Observable<OrganizationEmployee[]> {
    const url = `${this.baseUrl}/department/get-all-employees/${departmentId}`;
    return this.http.get<OrganizationEmployee[]>(url);
  }
  
  addDepartment(data: AddDepartmentRequest): Observable<boolean> {
    const url = `${this.baseUrl}/department/add-department`;
    return this.http.post<boolean>(url, data);
  }

}
