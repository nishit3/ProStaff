import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EmployeeService, OrganizationEmployee } from '../services/employee.service';
import { AuthServiceService } from '../services/auth-service.service';
import { DepartmentServiceService } from '../services/department-service.service';
import { TeamsService } from '../services/teams.service';
import { DesignationService } from '../services/designation.service';

@Component({
  selector: 'app-admin-employee-management',
  standalone: false,
  templateUrl: './admin-employee-management.component.html',
  styleUrl: './admin-employee-management.component.css'
})
export class AdminEmployeeManagementComponent implements OnInit {
  constructor(private router: Router, private route: ActivatedRoute, private employeeService: EmployeeService, private authService: AuthServiceService,
    private departmentService: DepartmentServiceService, private teamService: TeamsService, private designationService: DesignationService
  ) { }
  searchTerm: string = '';
  teamId: number | null = null; // Retained teamId
  adminEmail: string | null = null;
  designationId: string | null = null;
  employees: OrganizationEmployee[] = []
  totalEmployees : number = 0
  ngOnInit() {
    this.adminEmail = this.authService.getLoggedInEmail();
    const storedDeptId = localStorage.getItem('selectedDepartmentId');
    const storedTeamId = localStorage.getItem('selectedTeamId');
    const storedDesgId = localStorage.getItem('selectedDesignationId');
    if (storedDeptId) {
      const deptId = parseInt(storedDeptId, 10);
      this.fetchEmployeesByDepartment(deptId);
      localStorage.removeItem('selectedDepartmentId');  // Optional: clear once used
    } else if (storedTeamId) {
      const teamId = parseInt(storedTeamId, 10);
      this.fetchEmployeesByTeam(teamId);
      localStorage.removeItem('selectedTeamId');
    } else if (storedDesgId) {
      const desgId = parseInt(storedDesgId, 10);
      this.fetchEmployeesByDesg(desgId);
      localStorage.removeItem('selectedDesignationId');
    }
    else {
      this.getEmployeesList(); // Call this ONLY if no deptId in localStorage
    }
  }


  fetchEmployeesByDepartment(deptId: number): void {
    this.departmentService.getAllEmployeesByDepartmentId(deptId).subscribe(
      (employees) => {
        this.employees = employees;
        console.log('Filtered Employees:', this.employees);
      },
      (error) => {
        console.error('Failed to fetch employees:', error);
      }
    );
  }

  fetchEmployeesByTeam(teamId: number): void {
    this.teamService.getAllEmployeesByTeamId(teamId).subscribe(
      (employees) => {
        this.employees = employees;
        console.log('Filtered Employees:', this.employees);
      },
      (error) => {
        console.error('Failed to fetch employees:', error);
      }
    );
  }

  fetchEmployeesByDesg(desgId: number): void {
    this.designationService.getAllEmployeesByDesginationId(desgId, this.adminEmail!).subscribe(
      (employees) => {
        this.employees = employees;
        console.log('Employees by desg:', employees);
      },
      (error) => {
        console.error('Error fetching desg employees:', error);
      }
    );
  }

  get filteredEmployees() {
    let filteredList = this.employees;

    // Apply search filter
    if (this.searchTerm) {
      filteredList = filteredList.filter(emp =>
        emp.fullName.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }

    // Apply team filter only if teamId is provided
    // if (this.teamId !== null) {
    //   filteredList = filteredList.filter(emp => emp.teamId === this.teamId);
    // }

    // if (this.designationId !== null) {
    //   filteredList = filteredList.filter(emp => emp.designationId === this.designationId);
    // }

    return filteredList;
  }

  viewProfile(empEmail: string) {
    localStorage.setItem('employeeEmail', empEmail);
    this.router.navigate(['/adminViewProfile']);
  }

  getEmployeesList() {
    if (!this.adminEmail) return;
    this.employeeService.getAllOrganizationEmployees(this.adminEmail).subscribe(
      (employees) => {
        console.log('Employees:', employees);
        this.employees = employees;
        this.totalEmployees = employees.length;
        this.authService.updateTotalEmp(this.totalEmployees);
      },
      (error) => {
        console.error('Failed to fetch employees:', error);
      }
    );

  }

  deleteEmployee(empEmail: string) {
    this.employeeService.deleteEmployee(empEmail, this.adminEmail!)
      .subscribe({
        next: (res) => {
          if (res === true) {
            alert('Employee deleted successfully!');
            this.getEmployeesList();
          }
        },
        error: (err) => {
          console.error('Delete failed:', err);
        }
      });

  }

}
