import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AdminAddNotificationsComponent } from '../admin-add-notifications/admin-add-notifications.component';
import { AdminNotificationFilterComponent } from '../admin-notification-filter/admin-notification-filter.component';
import { AuthServiceService } from '../services/auth-service.service';
import { NotificationInterface, NotificationsService } from '../services/notifications.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-admin-notification',
  standalone: false,
  templateUrl: './admin-notification.component.html',
  styleUrl: './admin-notification.component.css'
})
export class AdminNotificationComponent implements OnInit {
  constructor(
    public dialog: MatDialog,
    private authService: AuthServiceService,
    private notificationService: NotificationsService
  ) { }

  searchTerm: string = '';
  selectedDepartments: number[] = [];   // Store department IDs
  selectedDesignations: number[] = []; // Store designation IDs
  selectedTeams: number[] = [];        // Store team IDs
  notifications: NotificationInterface[] = [];
  adminEmail: string | null = null;
  selectedDepartmentsFull: { id: number, name: string }[] = [];
  selectedTeamsFull: { id: number, name: string }[] = [];
  ngOnInit() {
    this.adminEmail = this.authService.getLoggedInEmail();
    this.getNotifications();
  }

  getNotifications() {
    if (!this.adminEmail) return;
    this.notificationService.getAllNotifications(this.adminEmail).subscribe(
      (notifications: NotificationInterface[]) => {
        console.log('Notifications:', notifications);
        this.notifications = notifications;
      },
      (error: HttpErrorResponse) => {
        console.error('Failed to fetch notifications:', error);
      }
    );
  }

  openFilterDialog() {
    const dialogRef = this.dialog.open(AdminNotificationFilterComponent, {
      width: '500px',
      data: {
        selectedDepartments: this.selectedDepartmentsFull,
        selectedDesignations: this.selectedDesignations,
        selectedTeams: this.selectedTeamsFull
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Store full objects for dialog use later
        this.selectedDepartmentsFull = result.departments || [];
        this.selectedTeamsFull = result.teams || [];

        // Extract only IDs for backend filtering
        this.selectedDepartments = this.selectedDepartmentsFull.map((dept: { id: number }) => dept.id);
        this.selectedTeams = this.selectedTeamsFull.map((team: { id: number }) => team.id);
        this.selectedDesignations = result.designations || [];

        console.log("Updated dept IDs after dialog closed:", this.selectedDepartments);

        const isAnyFilterApplied =
          this.selectedDepartments.length > 0 ||
          this.selectedDesignations.length > 0 ||
          this.selectedTeams.length > 0;

        if (isAnyFilterApplied) {
          this.getFilterNotifications();
        } else {
          this.getNotifications();
        }
      }
    });
  }

  get filteredNotifications(): NotificationInterface[] {
    if (!this.searchTerm) return this.notifications;
    const term = this.searchTerm.toLowerCase();
    return this.notifications.filter(n => n.message.toLowerCase().includes(term));
  }

  getFilterNotifications() {
    if (!this.adminEmail) return;

    this.notificationService.getFilteredNotifications({
      adminEmail: this.adminEmail,
      teams: this.selectedTeams.map(Number),
      departments: this.selectedDepartments.map(Number),
      designations: this.selectedDesignations.map(Number)
    }).subscribe({
      next: (notifications: NotificationInterface[]) => {
        console.log('Filtered Notifications:', notifications);
        this.notifications = notifications;
      },
      error: (err: HttpErrorResponse) => {
        console.error('Error fetching filtered notifications', err);
      }
    });
  }

  openLeaveRequestDialog() {
    const dialogRef = this.dialog.open(AdminAddNotificationsComponent, {
      width: '500px'
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Dialog returned:', result);
        this.notificationService.addNotification({
          message: result.message,
          adminEmail: this.adminEmail!,
          teams: result.teams,
          departments: result.departments,
          designations: result.designations
        }).subscribe({
          next: (success) => {
            if (success) {
              console.log('Notification added successfully!');
              this.getNotifications();
              // this.notifications.push(result); // Add only after successful save
            }
          },
          error: (err) => {
            console.error('Failed to add notification', err);
          }
        });
      }
    });
  }
  

  deleteNotification(index: number) {
    const notificationId = this.notifications[index].id; // assuming each notification has a unique ID
    if (!this.adminEmail) return;
  
    this.notificationService.deleteNotification(notificationId, this.adminEmail).subscribe({
      next: (response: boolean) => {
        if (response) {
          // this.notifications.splice(index, 1);
          this.getNotifications();
          console.log('Notification deleted successfully');
        } else {
          console.warn('Delete failed on backend');
        }
      },
      error: (err) => {
        console.error('Failed to delete notification:', err);
      }
    });
  }
  
}
