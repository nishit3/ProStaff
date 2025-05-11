import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AuthServiceService } from '../services/auth-service.service';
import { Department, DepartmentServiceService } from '../services/department-service.service';
import { AdminRolloutComponent } from '../admin-rollout/admin-rollout.component';
import { AdminAddFundsComponent } from '../admin-add-funds/admin-add-funds.component';
import { SalaryLog, SalaryService } from '../services/salary.service';

@Component({
  selector: 'app-admin-funds',
  standalone: false,
  templateUrl: './admin-salary.component.html',
  styleUrl: './admin-salary.component.css'
})
export default class AdminSalaryComponent implements OnInit {
  constructor(private router: Router, public dialog: MatDialog, private authService: AuthServiceService,
    private departmentService: DepartmentServiceService, private salaryService: SalaryService
  ) { }
  orderId: number = 0
  salaryLogs: SalaryLog[] = [];
  filteredSalaryLogs: SalaryLog[] = [];
  searchTerm: string = '';

  funds: number = 0
  adminEmail: string | null = ''


  ngOnInit(): void {
    this.adminEmail = this.authService.getLoggedInEmail();
    if (!this.adminEmail) return;
    this.fetchOrgFund();
    this.getSalaryLogs();
  }


  openRolloutDialog() {
    const dialogRef = this.dialog.open(AdminRolloutComponent, {
      width: '1200px',
      maxWidth: 'unset'
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.getSalaryLogs(); // 👈 this will now be triggered properly
      }
    });
    
  }
  

  openAddFundsDialog() {
    const dialogRef = this.dialog.open(AdminAddFundsComponent, {
      width: '500px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result.success) {
        this.fetchOrgFund();
        console.log("Verified fund added successfully.");
      }
    });
  }

  getSalaryLogs() {
    this.salaryService.getSalaryLogs(this.adminEmail!).subscribe({
      next: (logs) => {
        this.salaryLogs = logs;
        console.log(this.salaryLogs)
        this.filteredSalaryLogs = logs;
      },
      error: (err) => {
        console.error("Failed to fetch salary logs:", err);
      }
    });
  }

  filterLogs() {
    const term = this.searchTerm.toLowerCase();
    this.filteredSalaryLogs = this.salaryLogs.filter(log =>
      log.employeeEmail.toLowerCase().includes(term) ||
      log.message.toLowerCase().includes(term)
    );
  }



  fetchOrgFund() { // Replace with actual admin email
    this.salaryService.getCurrentOrganizationFund(this.adminEmail!).subscribe({
      next: (fund) => {
        this.funds = fund
        console.log('Current Organization Fund:', fund);
      },
      error: (err) => {
        console.error('Error fetching fund:', err);
      }
    });
  }
}
