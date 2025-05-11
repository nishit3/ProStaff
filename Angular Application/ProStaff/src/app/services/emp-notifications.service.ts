import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface NotificationDetails {
  // Define properties based on your backend NotificationDetails model
  id: number;
  message: string;
  date: string; // e.g. ISO date string
}

export interface EmployeeEmailWrapper {
  employeeEmail: string;
}


@Injectable({
  providedIn: 'root'
})
export class EmpNotificationsService {

  // Note: Use the Notification service's base URL.
  private baseUrl = 'http://localhost:5555/notification';

  constructor(private http: HttpClient) {}

  getEmployeeNotifications(employeeEmail: string): Observable<NotificationDetails[]> {
    const body: EmployeeEmailWrapper = { employeeEmail };
    return this.http.post<NotificationDetails[]>(`${this.baseUrl}/get-emplyoyee-notifications`, body);
  }

}
