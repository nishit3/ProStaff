import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { AuthServiceService } from '../services/auth-service.service';

@Component({
  selector: 'app-rest-password-quick-dialog',
  standalone: false,
  templateUrl: './rest-password-quick-dialog.component.html',
  styleUrl: './rest-password-quick-dialog.component.css'
})
export class RestPasswordQuickDialogComponent {
  constructor(
  public dialogRef: MatDialogRef<RestPasswordQuickDialogComponent>,
  @Inject(MAT_DIALOG_DATA) public data: any,
  public dialog: MatDialog,private authService: AuthServiceService
) {}
// @Output() showAlert = new EventEmitter<boolean>();
showAlert: boolean = false;
  alertMessage: string = '';
  isError: boolean = false;
submitEvent() {
  const empMail = this.authService.getLoggedInEmail();
  this.dialogRef.close('success'); // Close the dialog
  this.authService.sendResetPasswordMail(empMail!).subscribe({

    next: (response: string) => {
      console.log("inside reset password");
      if (response === "Password Reset Link Sent To The Registered Email Address") {
        this.alertMessage = response;
        this.isError = false;
        this.showResetSuccessAlert();
      } else if (response === "Error Occured! Please Try Again") {
        this.alertMessage = response;
        this.isError = true;
        this.showResetSuccessAlert();
      } else {
        this.alertMessage = response
        this.isError = true
        this.showResetSuccessAlert();
      }
    }
  });
}
showResetSuccessAlert() {
  this.showAlert = true;
  console.log("alert method")
  // Scroll to alert container
  document.querySelector('.welcome-container')?.scrollIntoView({ behavior: 'smooth' });

  // Add "show" class
  setTimeout(() => {
    const alertBox = document.querySelector('.global-alert');
    if (alertBox) {
      alertBox.classList.add('show');
    }
  }, 500);

  // Add exit animation
  setTimeout(() => {
    const alertBox = document.querySelector('.global-alert');
    if (alertBox) {
      alertBox.classList.add('exit-animation');
    }
  }, 2000);

  // Hide alert
  setTimeout(() => {
    this.showAlert = false;
  }, 2500);
}

  closeDialog(): void {
    this.dialogRef.close(null);
  }
}
