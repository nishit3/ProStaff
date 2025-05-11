import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
export interface AdminLog {
  id: number;
  date: string;         // ISO Date string
  time: string;         // ISO Time string
  type: string;         // e.g., "EMPLOYEE_ADDED"
  message: string;
  adminEmail: string;
  adminFullName: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminLogService {

  private baseUrl = 'http://localhost:5555';

  constructor(private http: HttpClient,private router:Router) {}

  getAllAdminLogs(adminEmail: string): Observable<AdminLog[]> {
    const url = `${this.baseUrl}/admin-logs/get-all-logs`;
    const body = { adminEmail };
    return this.http.post<AdminLog[]>(url, body);
  }
}
