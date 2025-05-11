import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { EmpNotificationsService, NotificationDetails } from '../services/emp-notifications.service';
import { AuthServiceService } from '../services/auth-service.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-notifications',
  standalone: false,
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.css'
})
export class NotificationsComponent implements OnInit {

  notifications: NotificationDetails[] = [];

  constructor(
    public router: Router,
    private authService: AuthServiceService,
    private notificationsService: EmpNotificationsService,
   ) {}

   ngOnInit(): void {
    // Retrieve the authenticated employee's email.
    const employeeEmail = this.authService.getLoggedInEmail();
    if (!employeeEmail) {
      console.error('Employee email not found. Please ensure the user is logged in.');
      return;
    }

    // Fetch employee notifications.
    this.notificationsService.getEmployeeNotifications(employeeEmail).subscribe({
      next: (data: NotificationDetails[]) => {
        this.notifications = data;
      },
      error: (err) => console.error('Error fetching notifications:', err)
    });
  }

// notifications = [
//   {
//     date: "April 2, 2025",
//     message: "Scheduled maintenance will occur from 12 AM to 2 AM."
//   },
//   {
//     date: "April 1, 2025",
//     message: "We have introduced a new dark mode feature. Check it out!"
//   },
//   {
//     date: "March 30, 2025",
//     message: "Your password will expire in 3 days. Please update it soon."
//   },
//   {
//     date: "March 28, 2025",
//     message: "Team meeting scheduled for March 30th at 10 AM in Conference Room B."
//   },
  
//   {
//     date: "March 30, 2025",
//     message: "Your password will expire in 3 days. Please update it soon."
//   },
//   {
//     date: "March 28, 2025",
//     message: "Team meeting scheduled for March 30th at 10 AM in Conference Room B."
//   }
// ];

}
