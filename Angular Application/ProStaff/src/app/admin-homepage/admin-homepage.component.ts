import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DepartmentServiceService } from '../services/department-service.service';
import { AuthServiceService } from '../services/auth-service.service';
import { TeamsService } from '../services/teams.service';
import { OrganizationService, UpcomingEvent } from '../services/organization.service';
import { AdminLog, AdminLogService } from '../services/admin-log.service';
import { EmployeeService, OrganizationEmployee } from '../services/employee.service';
import { LeaveRequestService } from '../services/leave-request.service';

@Component({
  selector: 'app-admin-homepage',
  standalone: false,
  templateUrl: './admin-homepage.component.html',
  styleUrl: './admin-homepage.component.css'
})
export class AdminHomepageComponent implements OnInit{
  constructor(private departmentService:DepartmentServiceService,private router:Router,private authService : AuthServiceService
    ,private teamService : TeamsService,private organizationService:OrganizationService,private logService: AdminLogService,
    private employeeService:EmployeeService,private leaveService : LeaveRequestService
  ){}
  totalLeavesCount : number = 0
  totalEmployees : number = 0
totalMaleEmp : number = 0
totalFemaleEmp : number = 0;
  leavesCount = 8; // example/
  adminEmail: string | null = null;
  ngOnInit() {
    this.adminEmail = this.authService.getLoggedInEmail();
    this.getDepartmentNames();
    this.getTeamNames();
    this.getUpcomingEvents();
    this.fetchLogs();
    this.getEmployeesList(); 
  }
  departments:string[] = [];
  teams:string[] = [];
  upcomingEvents: UpcomingEvent[] = [];
  logs: AdminLog[] = [];
  employees : OrganizationEmployee[] = [] 

  getDepartmentNames() {
    if (!this.adminEmail) return;
    this.departmentService.getAllDepartmentNames(this.adminEmail).subscribe(
      (departments: string[]) => {
        this.departments = departments;
        console.log('Departments:', departments);
        // Do something with the department list
      },
      (error) => {
        console.error('Failed to fetch department names', error);
        // Handle error appropriately
      }
    );
  }

  getEmployeesList() {
    if (!this.adminEmail) return;
    this.employeeService.getAllOrganizationEmployees(this.adminEmail).subscribe(
      (employees) => {
        console.log('Employees:', employees);
        this.employees = employees;
  
        // Count total, male, and female
        this.totalEmployees = employees.length;
        localStorage.setItem('totalEmp',this.totalEmployees.toString());
        this.authService.updateTotalEmp(this.totalEmployees);
        this.totalMaleEmp = employees.filter(emp => emp.gender?.toLowerCase() === 'male').length;
        this.totalFemaleEmp = employees.filter(emp => emp.gender?.toLowerCase() === 'female').length;
  
        console.log(`Total: ${this.totalEmployees}, Male: ${this.totalMaleEmp}, Female: ${this.totalFemaleEmp}`);
      },
      (error) => {
        console.error('Failed to fetch employees:', error);
      }
    );
  }
  
  

  getTeamNames() {
    if (!this.adminEmail) return;
    this.teamService.getAllTeamNames(this.adminEmail).subscribe(
      (teams: string[]) => {
        this.teams = teams;
        console.log('Teams:', teams);
        // Do something with the department list
      },
      (error) => {
        console.error('Failed to fetch teams names', error);
        // Handle error appropriately
      }
    );
  }

  addOneDay(date: string): string {
    const parsedDate = new Date(date);
  
    // Check if the parsed date is valid
    if (isNaN(parsedDate.getTime())) {
      console.error('Invalid date:', date);
      return '';  // Return an empty string or fallback value
    }
  
    // Add one day in UTC
    parsedDate.setUTCDate(parsedDate.getUTCDate() + 1);
  
    // Return the date in YYYY-MM-DD format (without the time)
    return parsedDate.toISOString().split('T')[0];  // Extract only the date part
  }
  
  

  getLeaves() {
    if (!this.adminEmail) return;
  
    this.leaveService.getAllLeaveRequests(this.adminEmail).subscribe((leaves) => {
      const totalLeaves = leaves.length;
      console.log("Total leaves:", totalLeaves);
      // You can store it in a variable if needed:
      this.totalLeavesCount = totalLeaves;
    });
  }
  

  getUpcomingEvents() {
    if (!this.adminEmail) return;
    this.organizationService.getUpcomingEvents(this.adminEmail).subscribe(
      (events: UpcomingEvent[]) => {
        console.log('Upcoming Events:', events);
        this.upcomingEvents = events;
      },
      (error) => {
        console.error('Failed to fetch upcoming events:', error);
      }
    );
  }

  fetchLogs() {
    if (!this.adminEmail) return;
    this.logService.getAllAdminLogs(this.adminEmail).subscribe(
      (logs: AdminLog[]) => {
        this.logs = logs;
        console.log('Logs fetched:', logs);
      },
      (error) => {
        console.error('Error fetching logs:', error);
      }
    );
  }
  
  
  
  
  formatDate(dateStr: string): string {
    const [dayStr, monthStr, yearStr] = dateStr.split('-');
    const day = parseInt(dayStr, 10) + 1;
    const newDay = day.toString().padStart(2, '0');
    return `${newDay}-${monthStr}-${yearStr}`;
  }
  
  
  

  
  getColor(type: string): string {
    if (type.includes('DELETED') || type.includes('REMOVED') || type.includes('DENIED')) {
      return 'red';
    } else if (type.includes('ADDED') || type.includes('APPROVED')) {
      return 'green';
    } else if (type.includes('UPDATED')) {
      return 'orange';
    }
    return 'black';
  }
}
