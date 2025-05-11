import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface AttendanceRecord {
  id: number;
  date: string;    // ISO date string
  status: string;  // e.g., "Present", "Absent", "Holiday"
  checkIn: string; // e.g., ISO time string
  checkOut: string; // e.g., ISO time string
}

export interface EmployeeEmailWrapper {
  employeeEmail: string;
}

@Injectable({
  providedIn: 'root'
})
export class EmpAttendanceService {

  private baseUrl = 'http://localhost:5555/attendance';

  constructor(private http: HttpClient) {}

  /**
   * Calls the check-in endpoint.
   */
  checkIn(employeeEmail: string): Observable<boolean> {
    const body: EmployeeEmailWrapper = { employeeEmail };
    return this.http.put<boolean>(`${this.baseUrl}/check-in`, body);
  }

  /**
   * Calls the check-out endpoint.
   */
  checkOut(employeeEmail: string): Observable<boolean> {
    const body: EmployeeEmailWrapper = { employeeEmail };
    return this.http.put<boolean>(`${this.baseUrl}/check-out`, body);
  }

  /**
   * Retrieves the attendance records for the employee.
   */
  getEmployeeAttendance(employeeEmail: string): Observable<AttendanceRecord[]> {
    const body: EmployeeEmailWrapper = { employeeEmail };
    return this.http.post<AttendanceRecord[]>(`${this.baseUrl}/get-employee-attendance`, body);
  }
}
