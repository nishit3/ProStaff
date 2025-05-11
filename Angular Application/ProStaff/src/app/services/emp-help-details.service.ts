import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface HelpDetails {
  // Define properties as per your backend HelpDetails object.
  email: string;
  phoneNumber: string;
}

export interface EmployeeEmailWrapper {
  employeeEmail: string;
}

@Injectable({
  providedIn: 'root'
})
export class EmpHelpDetailsService {

  private baseUrl = 'http://localhost:5555/organization';

  constructor(private http: HttpClient) {}

  /**
   * Calls the backend to get help details for the employee.
   */
  getHelpDetails(employeeEmail: string): Observable<HelpDetails> {
    const body: EmployeeEmailWrapper = { employeeEmail };
    return this.http.post<HelpDetails>(`${this.baseUrl}/get-help-details`, body);
  }
}
