import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { LeaveRequestService } from '../services/leave-request.service';
import { AuthServiceService } from '../services/auth-service.service';

@Component({
  selector: 'app-leave-request-form',
  standalone: false,
  templateUrl: './leave-request-form.component.html',
  styleUrl: './leave-request-form.component.css'
})
export class LeaveRequestFormComponent {
  // The leave date is stored as a YYYY-MM-DD string.
  leaveDate: string = new Date().toISOString().split('T')[0];
  reason: string = ''; // Stores selected leave type
  type: string = ''; // Stores leave description

  constructor(
    public dialogRef: MatDialogRef<LeaveRequestFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { 
      selectedDate: Date,
      leaveData?: string,    // Existing leave's leaveDate (YYYY-MM-DD)
      reason?: string,       // Existing leave reason
      type?: string,         // Existing leave type
      status?: string        // Existing leave status (optional)
    },
    private leaveRequestService: LeaveRequestService,
    private authService: AuthServiceService
  ) 
  {
    if (data && data.selectedDate) {
      this.leaveDate = this.formatDate(data.selectedDate); // Convert Date to string format
    }

    // If an existing leave request is passed in, override the fields.
    if (data && data.leaveData) {
      this.leaveDate = data.leaveData; 
      this.reason = data.reason || '';
      this.type = data.type || '';
    }
  }

  formatDate(date: Date): string {
    return date.toISOString().split('T')[0]; // Convert Date to YYYY-MM-DD format
  }

  // Called when the user clicks on the Submit button.
  submitRequest(): void {
    const employeeEmail = this.authService.getLoggedInEmail();
    if (!employeeEmail) {
      console.error('Employee email not found.');
      return;
    }

    // Build the leave request payload.
    // Note: requestDate is set to today's date.
    const payload = {
      employee_email: employeeEmail,
      requestDate: new Date().toISOString().split('T')[0],
      leaveDate: this.leaveDate,
      reason: this.reason,
      status: "PENDING", // New requests are initially pending.
      type: this.type  // e.g.,"PAID", "UNPAID", "SICK"
    };

    // Call the create request API.
    this.leaveRequestService.createRequest(payload).subscribe({
      next: (success: boolean) => {
        if (success) {
          console.log('Leave request created successfully.');
          // Close the dialog and optionally pass a result.
          this.dialogRef.close({ success: true });
        } else {
          console.error('Failed to create leave request.');
        }
      },
      error: (err) => {
        console.error('Error creating leave request:', err);
      }
    });
  }
}
