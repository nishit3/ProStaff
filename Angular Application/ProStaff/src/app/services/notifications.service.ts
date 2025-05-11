import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
// models/notification.model.ts
export interface FilteredNotificationsRequest {
  adminEmail: string;
  teams: number[];
  departments: number[];
  designations: number[];
}
export interface NotificationInterface {
  id: number;
  date: string; // or Date if you prefer parsing it
  message: string;
}
@Injectable({
  providedIn: 'root'
})
export class NotificationsService {

  private baseUrl = 'http://localhost:5555';

  constructor(private http: HttpClient,private router:Router) {}

  getAllNotifications(adminEmail: string): Observable<NotificationInterface[]> {
    const url = `${this.baseUrl}/notification/get-all-notifications`;
    const body = { adminEmail };
    return this.http.post<NotificationInterface[]>(url, body);
  }

  getFilteredNotifications(payload: FilteredNotificationsRequest): Observable<NotificationInterface[]> {
    const url = `${this.baseUrl}/notification/get-filtered-notifications`;
    return this.http.post<NotificationInterface[]>(url, payload);
  }

  addNotification(data: {
    message: string;
    adminEmail: string;
    teams: number[];
    departments: number[];
    designations: number[];
  }) {
    console.log("in api notifs   ",data)
    return this.http.post<boolean>(`${this.baseUrl}/notification/add-notification`, data);
  }
  
  deleteNotification(notificationId: number, adminEmail: string): Observable<boolean> {
    const url = `${this.baseUrl}/notification/deleteNotification/${notificationId}`;
    const body = { adminEmail };
  
    return this.http.delete<boolean>(url, { body });
  }
  
}
