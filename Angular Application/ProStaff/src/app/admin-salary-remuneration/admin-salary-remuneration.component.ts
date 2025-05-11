import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { SalaryService } from '../services/salary.service';
import { AuthServiceService } from '../services/auth-service.service';

@Component({
  selector: 'app-admin-salary-remuneration',
  standalone: false,
  templateUrl: './admin-salary-remuneration.component.html',
  styleUrl: './admin-salary-remuneration.component.css'
})
export class AdminSalaryRemunerationComponent implements OnInit {
  seelctedEmails: string[] = []
  adminEmail: string | null = ''
  message : string = ''
  constructor(
    public dialogRef: MatDialogRef<AdminSalaryRemunerationComponent>,
    private salaryService: SalaryService,
    private authService : AuthServiceService,
    @Inject(MAT_DIALOG_DATA) public data: { selectedEmails: string[] }
  ) {
    this.seelctedEmails = data.selectedEmails;
    console.log("Selected Emails:",this.seelctedEmails)
  }

  ngOnInit(): void {
    this.adminEmail = this.authService.getLoggedInEmail();
    if (!this.adminEmail) return;
    this.getTotalRemuneration();
  }

  getTotalRemuneration() {
    this.salaryService.getTotalRemuneration(this.adminEmail!, this.seelctedEmails).subscribe({
      next: (res) => {
        console.log("Total Remuneration:", res);
        this.message = `Total Remuneration: ₹${res}`;
      },
      error: (err) => {
        console.error("Error fetching remuneration:", err);
        alert("Failed to fetch total remuneration.");
      }
    });
  }

  closeDialog(result: boolean) {
    this.dialogRef.close(result);
  }

  
}
