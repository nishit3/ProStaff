import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface OrganizationData {
  // Define the properties that match your backend OrganizationData object.
  organizationName: string;
  image: Uint8Array; // Assuming image is a byte array, adjust as needed.
}

export interface EmployeeEmailWrapper {
  employeeEmail: string;
}

@Injectable({
  providedIn: 'root'
})
export class EmpOrganizationService {

  private baseUrl = 'http://localhost:5555/organization';

  constructor(private http: HttpClient) {}

  /**
   * Retrieves organization data for the logged-in employee.
   */
  getOrganizationData(employeeEmail: string): Observable<OrganizationData> {
    const body: EmployeeEmailWrapper = { employeeEmail };
    return this.http.post<OrganizationData>(`${this.baseUrl}/get-organization-data`, body);
  }
}
