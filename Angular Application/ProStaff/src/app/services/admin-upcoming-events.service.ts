import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';


export interface UpcomingEvent {
  id: number;
  name: string;
  date: string;         // ISO date string, e.g. "2025-03-13"
  description: string;
}

interface AdminEmailWrapper {
  adminEmail: string;
}

interface EmployeeEmailWrapper {
  employeeEmail: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminUpcomingEventsService {

  private baseUrl = 'http://localhost:5555/organization';

  constructor(private http: HttpClient) {}

  /** POST /organization/get-upcoming-events */
  getUpcomingEventsByEmail(employeeEmail: string): Observable<UpcomingEvent[]> {
    const body: EmployeeEmailWrapper = { employeeEmail };
    return this.http.post<UpcomingEvent[]>(`${this.baseUrl}/get-upcoming-events`, body);
  }

  /** POST /organization/add-upcoming-event (multipart/form-data) */
  addUpcomingEvent(
    adminEmail: string,
    event: Omit<UpcomingEvent, 'id'>
  ): Observable<boolean> {
    const wrapper: AdminEmailWrapper = { adminEmail };
    const newEvent = {
      name: event.name,
      date: event.date,
      description: event.description
    };

    const formData = new FormData();
    formData.append(
      'wrapper',
      new Blob([JSON.stringify(wrapper)], { type: 'application/json' })
    );
    formData.append(
      'newUpcomingEvent',
      new Blob([JSON.stringify(newEvent)], { type: 'application/json' })
    );

    return this.http.post<boolean>(`${this.baseUrl}/add-upcoming-event`, formData);
  }

  /** PUT /organization/update-upcoming-event (multipart/form-data) */
  updateUpcomingEvent(
    adminEmail: string,
    event: UpcomingEvent
  ): Observable<boolean> {
    const wrapper: AdminEmailWrapper = { adminEmail };

    const formData = new FormData();
    formData.append(
      'wrapper',
      new Blob([JSON.stringify(wrapper)], { type: 'application/json' })
    );
    formData.append(
      'updatedUpcomingEvent',
      new Blob([JSON.stringify(event)], { type: 'application/json' })
    );

    return this.http.put<boolean>(`${this.baseUrl}/update-upcoming-event`, formData);
  }

  /** DELETE /organization/remove-upcoming-event/{id} with body */
  deleteUpcomingEvent(adminEmail: string, id: number): Observable<boolean> {
    const wrapper: AdminEmailWrapper = { adminEmail };
    return this.http.delete<boolean>(
      `${this.baseUrl}/remove-upcoming-event/${id}`,
      { body: wrapper }
    );
  }
}
