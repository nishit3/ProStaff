import { Component, OnInit } from '@angular/core';
import { AdminAddDesignationComponent } from '../admin-add-designation/admin-add-designation.component';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { Designation, DesignationService } from '../services/designation.service';
import { AuthServiceService } from '../services/auth-service.service';

@Component({
  selector: 'app-admin-designation',
  standalone: false,
  templateUrl: './admin-designation.component.html',
  styleUrl: './admin-designation.component.css'
})
export class AdminDesignationComponent implements OnInit {
  constructor(private router: Router, public dialog: MatDialog, private authService: AuthServiceService, private designationService: DesignationService) { }

  searchTerm: string = '';
  adminEmail: string | null = ''
  designations: Designation[] = []

  ngOnInit(): void {
    this.adminEmail = this.authService.getLoggedInEmail();
    if (!this.adminEmail) return;
    this.getDesignations();
  }

  getDesignations() {
    if (!this.adminEmail) return;

    this.designationService.getAllDesignations(this.adminEmail).subscribe((desgs) => {
      this.designations = desgs;
      console.log("designations list:", this.designations);
    });
  }

  get filteredDesignations() {
    return this.designations.filter(designation =>
      designation.name.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  viewEmployees(desgId: number) {
    // Store department ID in local storage
    localStorage.setItem('selectedDesignationId', desgId.toString());

    // Navigate to employee management page
    this.router.navigate(['/adminEmpManagement']);
  }


  deleteDesignation(desgId: number, index: number) {
    if (!this.adminEmail) return;

    this.designationService.deleteDesignation(desgId, this.adminEmail).subscribe(
      (response) => {
        if (response) {
          this.getDesignations() // Update UI immediately
        } else {
          alert('Failed to delete designation.');
        }
      },
      (error) => {
        console.error('Delete designation error:', error);
        alert('An error occurred while deleting the designation.');
      }
    );
  }

  // Open Dialog Box to Add Designation
  openDialog() {
    const dialogRef = this.dialog.open(AdminAddDesignationComponent, {
      width: '500px'
    });

    // Receive Data from Dialog when the Create button is clicked
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        // Inject adminEmail here before calling API
        const newDesg = {
          ...result,
          adminEmail: this.adminEmail
        };

        this.designationService.addDesignation(newDesg).subscribe(
          (response: boolean) => {
            if (response) {
              this.getDesignations();
            } else {
              console.log('Failed to add designation');
            }
          },
          (error) => {
            console.error('API error:', error);
          }
        );
      }
    });
  }
}
