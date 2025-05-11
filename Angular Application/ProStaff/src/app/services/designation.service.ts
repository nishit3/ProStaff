import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { OrganizationEmployee } from './employee.service';
export interface Designation {
  id: number;
  name: string;
  description: string;
  employeeCount: number;
}
export interface AddDesignationRequest {
  name: string;
  description: string;
  adminEmail: string;
}
@Injectable({
  providedIn: 'root'
})
export class DesignationService {

  private baseUrl = 'http://localhost:5555';

  constructor(private http: HttpClient, private router: Router) { }

  getAllDesignations(adminEmail: string): Observable<Designation[]> {
    const url = `${this.baseUrl}/designation/get-all-designations`;
    return this.http.post<Designation[]>(url, { adminEmail });
  }

  addDesignation(data: AddDesignationRequest): Observable<boolean> {
    const url = `${this.baseUrl}/designation/add-designation`;
    return this.http.post<boolean>(url, data);
  }

  getAllEmployeesByDesginationId(departmentId: number, adminEmail: string): Observable<OrganizationEmployee[]> {
    const url = `${this.baseUrl}/designation/get-all-employees/${departmentId}`;
    const body = { adminEmail };

    return this.http.post<OrganizationEmployee[]>(url, body);
  }


  deleteDesignation(desgId: number, adminEmail: string): Observable<boolean> {
    const url = `${this.baseUrl}/designation/delete-designation/${desgId}`;
    const options = {
      body: { adminEmail }
    };
    return this.http.delete<boolean>(url, options);
  }
  
  updateCurrentDesignation(id: number, employeeEmail: string): Observable<boolean> {
    const payload = { employeeEmail };
    return this.http.put<boolean>(`${this.baseUrl}/designation/update-current-designation/${id}`, payload);
  }
}
