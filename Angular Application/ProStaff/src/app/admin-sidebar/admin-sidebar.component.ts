import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AdminContactUsDialogComponent } from '../admin-contact-us-dialog/admin-contact-us-dialog.component';
import { AuthServiceService } from '../services/auth-service.service';
import { AdminHolidayComponent } from '../admin-holiday/admin-holiday.component';
import { AdminAddAdminComponent } from '../admin-add-admin/admin-add-admin.component';

@Component({
  selector: 'app-admin-sidebar',
  standalone: false,
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './admin-sidebar.component.html',
  styleUrl: './admin-sidebar.component.css'
})
export class AdminSidebarComponent {

  constructor(private router: Router, public dialog: MatDialog, private cdr: ChangeDetectorRef, private authService: AuthServiceService) { }
  sidebarLoaded = false; // Tracks if sidebar is already loaded
  counter: number = 0
  fullName: string = ''
  isHidden: boolean = false;
  ngOnInit() {
    const adminEmail = this.authService.getLoggedInEmail();
    this.authService.getTotalEmpObservable().subscribe(count => {
      this.isHidden = count === 0;
      this.cdr.markForCheck();
    });
    console.log("hidden", this.isHidden)

    if (!adminEmail) return// Replace with dynamic email if needed
    this.authService.getUserFullName(adminEmail).subscribe(
      (response: string) => {
        console.log(response);
        console.log("Fullname admin:", response)
        this.fullName = response;
        this.cdr.markForCheck();
      },
      error => {
        console.error('Error fetching full name', error);
      }
    );
  }
  logOutClicked() {
    console.log("logout clicked")
    this.authService.logout();
  }
  openContactUs() {
    const dialogRef = this.dialog.open(AdminContactUsDialogComponent, {
      width: '500px',
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        alert("opened")
      }
    });
  }

  openHoliday() {
    const dialogRef = this.dialog.open(AdminHolidayComponent, {
      width: '500px',
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        alert("opened")
      }
    });
  }



}  
