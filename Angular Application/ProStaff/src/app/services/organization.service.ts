import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
export interface UpcomingEvent {
  id: number;
  name: string;
  description: string;
  date: string; // ISO format
}
// models/organization-data.model.ts
export interface OrganizationData {
  organizationName: string;
  image: string;
}

@Injectable({
  providedIn: 'root'
})
export class OrganizationService {

  private baseUrl = 'http://localhost:5555';

  constructor(private http: HttpClient,private router:Router) {}

  getUpcomingEvents(employeeEmail: string): Observable<UpcomingEvent[]> {
    const url = `${this.baseUrl}/organization/get-upcoming-events`;
    return this.http.post<UpcomingEvent[]>(url, { employeeEmail });
  }

  getOrganizationData(employeeEmail: string): Observable<OrganizationData> {
    const body = { employeeEmail };
    return this.http.post<OrganizationData>(`${this.baseUrl}/organization/get-organization-data`, body);
  }
}
