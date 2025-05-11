import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface HelpDetails {
  id: number;
  email: string;
  phoneNumber: string;
  countryCode: '+91'; // Add other country codes as needed
}

export interface EmployeeEmailWrapper {
  employeeEmail: string;
}

export interface AdminEmailWrapper {
  adminEmail: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminContactUsService {

  private baseUrl = 'http://localhost:5555/organization'; // Update to your backend URL

  constructor(private http: HttpClient) {}

  getHelpDetails(wrapper: EmployeeEmailWrapper): Observable<HelpDetails> {
    return this.http.post<HelpDetails>(`${this.baseUrl}/get-help-details`, wrapper);
  }

  updateHelpDetails(wrapper: AdminEmailWrapper, updatedHelp: HelpDetails): Observable<boolean> {
    const formData: FormData = new FormData();
    formData.append('wrapper', new Blob([JSON.stringify(wrapper)], { type: 'application/json' }));
    formData.append('updatedHelpDetails', new Blob([JSON.stringify(updatedHelp)], { type: 'application/json' }));

    return this.http.put<boolean>(`${this.baseUrl}/update-help-details`, formData);
  }
}
