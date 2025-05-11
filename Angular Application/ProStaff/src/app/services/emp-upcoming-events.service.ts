import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface UpcomingEvent {
  id: number;
  name: string;
  date: string;         // ISO date string, e.g. "2025-03-13"
  description: string;
}

interface EmployeeEmailWrapper {
  employeeEmail: string;
}

@Injectable({
  providedIn: 'root'
})
export class EmpUpcomingEventsService {

  private baseUrl = 'http://localhost:5555/organization';

  constructor(private http: HttpClient) {}

  /** POST /organization/get-upcoming-events */
    getUpcomingEventsByEmail(employeeEmail: string): Observable<UpcomingEvent[]> {
      const body: EmployeeEmailWrapper = { employeeEmail };
      return this.http.post<UpcomingEvent[]>(`${this.baseUrl}/get-upcoming-events`, body);
    }
}
