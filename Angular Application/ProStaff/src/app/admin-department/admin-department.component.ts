import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AdminAddDepartmentComponent } from '../admin-add-department/admin-add-department.component';
import { Department, DepartmentServiceService } from '../services/department-service.service';
import { AuthServiceService } from '../services/auth-service.service';

@Component({
  selector: 'app-admin-department',
  standalone: false,
  templateUrl: './admin-department.component.html',
  styleUrl: './admin-department.component.css'
})
export class AdminDepartmentComponent implements OnInit {
  constructor(private router: Router, public dialog: MatDialog, private authService: AuthServiceService,
    private departmentService: DepartmentServiceService
  ) { }
  searchTerm: string = ''
  adminEmail: string | null = ''
  departments: Department[] = []

  get filteredDepts() {
    return this.departments.filter(dept =>
      dept.name.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  ngOnInit(): void {
    this.adminEmail = this.authService.getLoggedInEmail();
    if (!this.adminEmail) return;
    this.getDepartments();
  }

  getDepartments() {
    if (!this.adminEmail) return;

    this.departmentService.getAllDepartments(this.adminEmail).subscribe((depts) => {
      this.departments = depts;
      console.log("department list:", this.departments);
    });
  }
  viewEmployees(departmentId: number) {
    // Store department ID in local storage
    localStorage.setItem('selectedDepartmentId', departmentId.toString());
  
    // Navigate to employee management page
    this.router.navigate(['/adminEmpManagement']);
  }
  

  deleteDepartment(departmentId: number, index: number) {
    if (!this.adminEmail) return;

    this.departmentService.deleteDepartment(departmentId, this.adminEmail).subscribe(
      (response) => {
        if (response) {
          this.getDepartments() // Update UI immediately
        } else {
          alert('Failed to delete department.');
        }
      },
      (error) => {
        console.error('Delete department error:', error);
        alert('An error occurred while deleting the department.');
      }
    );
  }
  // Open Dialog Box to Add Notification
  openDialog() {
    const dialogRef = this.dialog.open(AdminAddDepartmentComponent, {
      width: '500px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Inject adminEmail here before calling API
        const newDept = {
          ...result,
          adminEmail: this.adminEmail
        };

        this.departmentService.addDepartment(newDept).subscribe(
          (response: boolean) => {
            if (response) {
              this.getDepartments();
            } else {
              console.log('Failed to add department');
            }
          },
          (error) => {
            console.error('API error:', error);
          }
        );
      }
    });
  }

}
