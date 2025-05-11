import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { OrganizationEmployee } from './employee.service';
// models/team.model.ts
export interface Team {
  id: number;
  name: string;
  description: string;
  employeeCount: number;
}
export interface AddTeamRequest {
  name: string;
  description: string;
  adminEmail: string;
}
@Injectable({
  providedIn: 'root'
})
export class TeamsService {

  private baseUrl = 'http://localhost:5555';

  constructor(private http: HttpClient, private router: Router) { }

  getAllTeamNames(adminEmail: string): Observable<string[]> {
    const url = `${this.baseUrl}/team/get-all-team-names`;
    const body = { adminEmail };
    return this.http.post<string[]>(url, body);
  }

  updateEmployeeTeams(employeeEmail: string, adminEmail: string, teams: number[]): Observable<boolean> {
    const payload = {
      employeeEmail,
      adminEmail,
      teams
    };

    return this.http.put<boolean>(`${this.baseUrl}/team/update-current-employee-teams`, payload);
  }

  getAllTeams(adminEmail: string): Observable<Team[]> {
    const url = `${this.baseUrl}/team/get-all-teams`;
    const body = { adminEmail };
    return this.http.post<Team[]>(url, body);
  }

  addTeam(data: AddTeamRequest): Observable<boolean> {
    const url = `${this.baseUrl}/team/add-team`;
    return this.http.post<boolean>(url, data);
  }

  getAllEmployeesByTeamId(teamId: number): Observable<OrganizationEmployee[]> {
    const url = `${this.baseUrl}/team/get-all-employees/${teamId}`;
    return this.http.get<OrganizationEmployee[]>(url);
  }

  deleteTeam(teamId: number, adminEmail: string): Observable<boolean> {
    const url = `${this.baseUrl}/team/delete-team/${teamId}`;
    const options = {
      body: { adminEmail }
    };
    return this.http.delete<boolean>(url, options);
  }
}
