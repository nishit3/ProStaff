import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
export interface AttendanceRecord {
  id: number;
  date: string;
  status: 'PRESENT' | 'ABSENT' | 'PENDING' | 'CHECKEDIN' | 'HOLIDAY'; // adjust based on possible statuses
  checkIn: string;
  checkOut: string;
}
@Injectable({
  providedIn: 'root'
})
export class AttendanceService {

  
  private baseUrl = 'http://localhost:5555';

  constructor(private http: HttpClient,private router:Router) {}

  getEmployeeAttendance(email: string): Observable<AttendanceRecord[]> {
    const url = `${this.baseUrl}/attendance/get-employee-attendance`;
    const body = { employeeEmail: email };
    return this.http.post<AttendanceRecord[]>(url, body);
  }

  addHoliday(date: string, adminEmail: string): Observable<boolean> {
    const url = `${this.baseUrl}/attendance/add-holiday/${date}`;
    const body = { adminEmail };
    return this.http.post<boolean>(url, body);
  }

  getAllHolidays(adminEmail: string): Observable<AttendanceRecord[]> {
    const body = { adminEmail };
    return this.http.post<AttendanceRecord[]>(`${this.baseUrl}/attendance/get-all-holidays`, body);
  }

  deleteHoliday(date: string, adminEmail: string): Observable<boolean> {
    const url = `${this.baseUrl}/attendance/delete-holiday/${date}`;
    const body = { adminEmail };
    
    return this.http.request<boolean>('DELETE', url, { body });
  }
}
