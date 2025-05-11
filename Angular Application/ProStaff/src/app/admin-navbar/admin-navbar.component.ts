import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { OrganizationService } from '../services/organization.service';
import { AuthServiceService } from '../services/auth-service.service';
import { MatDialog } from '@angular/material/dialog';
import { AdminAddAdminComponent } from '../admin-add-admin/admin-add-admin.component';

@Component({
  selector: 'app-admin-navbar',
  standalone: false,
  templateUrl: './admin-navbar.component.html',
  styleUrl: './admin-navbar.component.css'
})
export class AdminNavbarComponent implements OnInit {

  constructor(private authService: AuthServiceService, private organizationService: OrganizationService, public dialog: MatDialog) { }
  organizationName: string = '';
  organizationLogo: string = '';
  adminEmail: string | null = null;

  ngOnInit(): void {
    this.adminEmail = this.authService.getLoggedInEmail();
    if (!this.adminEmail) return
    this.organizationService.getOrganizationData(this.adminEmail).subscribe(data => {

      this.organizationName = data.organizationName
      this.organizationLogo = 'data:image/png;base64,' + data.image;
    });
  }

  openAddAdminDialog() {
    const dialogRef = this.dialog.open(AdminAddAdminComponent, {
      width: '500px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log("adminemail:",result.email," fullname",result.name)
        this.authService.addAdmin({
          adminEmail: this.adminEmail!,
          newAdminEmail: result.email,
          newAdminFullName: result.name

        }).subscribe({
          next: (response) => {
            if (response) {
              alert('New admin added successfully!');
            } else {
              alert('Failed to add new admin.');
            }
          },
          error: (err) => {
            console.error('Error adding new admin:', err);
            alert('Something went wrong.');
          }
        });
      }
    })
  }


}
