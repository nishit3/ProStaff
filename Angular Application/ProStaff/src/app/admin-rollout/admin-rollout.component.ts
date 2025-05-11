import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { AuthServiceService } from '../services/auth-service.service';
import { SalaryService } from '../services/salary.service';
import { EmployeeService, OrganizationEmployee } from '../services/employee.service';
import { AdminSalaryRemunerationComponent } from '../admin-salary-remuneration/admin-salary-remuneration.component';

@Component({
  selector: 'app-admin-rollout',
  standalone: false,
  templateUrl: './admin-rollout.component.html',
  styleUrls: ['./admin-rollout.component.css']
})
export class AdminRolloutComponent implements OnInit {
  employees: OrganizationEmployee[] = [];
  filteredEmployees: OrganizationEmployee[] = [];

  selectedEmployees: Map<string, boolean> = new Map(); // key: email, value: selected
  isAllSelected: boolean = false;
  searchTerm: string = '';
  adminEmail: string | null = null;

  constructor(
    public dialogRef: MatDialogRef<AdminRolloutComponent>,
    private authService: AuthServiceService,
    private salaryService: SalaryService,
    private employeeService: EmployeeService,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.adminEmail = this.authService.getLoggedInEmail();
    this.getEmployeesList();
  }

  getEmployeesList() {
    if (!this.adminEmail) return;

    this.employeeService.getAllOrganizationEmployees(this.adminEmail).subscribe({
      next: (employees) => {
        this.employees = employees;
        this.filteredEmployees = [...employees];

        // Initialize all to false
        this.employees.forEach(emp => {
          this.selectedEmployees.set(emp.email, false);
        });
      },
      error: (error) => {
        console.error('Failed to fetch employees:', error);
      }
    });
  }

  toggleSelectAll(event: any) {
    const checked = event.target.checked;
    this.isAllSelected = checked;

    this.filteredEmployees.forEach(emp => {
      this.selectedEmployees.set(emp.email, checked);
    });
  }

  updateSelectAllState() {
    const allSelected = this.filteredEmployees.every(emp => this.selectedEmployees.get(emp.email));
    this.isAllSelected = allSelected;
  }

  toggleSelection(emp: OrganizationEmployee) {
    const current = this.selectedEmployees.get(emp.email) || false;
    this.selectedEmployees.set(emp.email, !current);
    this.updateSelectAllState();
  }

  filterEmployees() {
    const term = this.searchTerm.toLowerCase();
    this.filteredEmployees = this.employees.filter(emp =>
      emp.fullName.toLowerCase().includes(term) || emp.email.toLowerCase().includes(term)
    );
    this.updateSelectAllState();
  }



  openSalaryRemDialog() {
    const selectedEmails = Array.from(this.selectedEmployees.entries())
      .filter(([_, selected]) => selected)
      .map(([email]) => email);

    if (selectedEmails.length === 0) {
      alert("Please select at least one employee.");
      return;
    }

    const dialogRef = this.dialog.open(AdminSalaryRemunerationComponent, {
      width: '500px',
      data: { selectedEmails: selectedEmails }
    });

    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result === true) {
        this.rolloutSalary()
        // Proceed with action
      } else {
        console.log('User cancelled');
      }
    });
  }



  rolloutSalary() {
    const selectedEmails = Array.from(this.selectedEmployees.entries())
      .filter(([_, selected]) => selected)
      .map(([email]) => email);

    if (!this.adminEmail || selectedEmails.length === 0) {
      alert("Please select at least one employee.");
      return;
    }

    this.salaryService.rolloutSalary(this.adminEmail, selectedEmails).subscribe({
      next: (response) => {
        if (response === true) {
          alert('Salary rollout successful!');
          this.dialogRef.close(true);  // 👈 close with true to notify parent
        } else {
          alert('Salary rollout failed.');
        }
      },
      error: (err) => {
        console.error("Error during salary rollout:", err);
        alert("An error occurred while rolling out salary.");
      }
    });
  }


  closeDialog() {
    this.dialogRef.close();
  }

  isSelected(email: string): boolean {
    return this.selectedEmployees.get(email) || false;
  }
}
