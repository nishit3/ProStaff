import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AuthServiceService } from '../services/auth-service.service';
import { LeaveRequestService } from '../services/leave-request.service';

@Component({
  selector: 'app-grant-leave',
  standalone: false,
  templateUrl: './grant-leave.component.html',
  styleUrl: './grant-leave.component.css'
})
export class GrantLeaveComponent implements OnInit {
  leaveDate: string = new Date().toISOString().split('T')[0];
  reason: string = ''; // Stores selected leave type
  type: string = ''; // Stores leave description
  empMail: string = ''
  constructor(private authService: AuthServiceService,
    private leaveService: LeaveRequestService,
    public dialogRef: MatDialogRef<GrantLeaveComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { leaveId: number, empMail: string },

  ) {
    this.leaveId = data.leaveId;
    this.empMail = data.empMail;
    console.log("Leave ID passed to dialog:", this.leaveId);
  }
  paidLeaveCount = 0
  unpaidLeaveCount = 0
  sickLeaveCount = 0
  leaveId: number = 0;


  adminEmail: string | null = ''

  ngOnInit(): void {
    this.adminEmail = this.authService.getLoggedInEmail();
    if (!this.adminEmail) return;
    this.leaveService.getLeaveRequestsCount(this.empMail).subscribe({
      next: (data) => {
        console.log('Sick Leaves:', data.sickLeaves);
        console.log('Paid Leaves:', data.paidLeaves);
        console.log('Unpaid Leaves:', data.unPaidLeaves);
        this.sickLeaveCount = data.sickLeaves
        this.paidLeaveCount = data.paidLeaves
        this.unpaidLeaveCount = data.unPaidLeaves
      },
      error: (err) => {
        console.error('Error fetching leave count:', err);
      }
    });

  }
  approveLeave(): void { // Replace this with your actual admin email logic

    this.leaveService.grantLeaveRequest(this.leaveId, this.adminEmail!).subscribe({
      next: (response) => {
        if (response === true) {
          console.log('Leave granted successfully!');
          this.closeDialog()
        }
      },
      error: (err) => {
        console.error('Error while granting leave:', err);
      }
    });
  }
  closeDialog(): void {
    this.dialogRef.close(null);
  }
}
