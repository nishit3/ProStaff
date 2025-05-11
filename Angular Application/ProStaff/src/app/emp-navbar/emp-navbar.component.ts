import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { EmpHelpDetailsComponent } from '../emp-help-details/emp-help-details.component';
import { MatDialog } from '@angular/material/dialog';
import { EmpOrganizationService, OrganizationData } from '../services/emp-organization.service';
import { AuthServiceService } from '../services/auth-service.service';
import { EmpInformationService, EmployeeInformation } from '../services/emp-information.service';

@Component({
  selector: 'app-emp-navbar',
  standalone: false,
  templateUrl: './emp-navbar.component.html',
  styleUrl: './emp-navbar.component.css'
})
export class EmpNavbarComponent implements OnInit {

  organizationData?: OrganizationData;
  isDropdownOpen = false;
  imageSrc?: string;
  empImageSrc?: string; // Stores the employee's image source

  employeeFullName: string = ''; // Stores the employee's full name

  constructor(public router:Router,
    private authService: AuthServiceService,
    private dialog: MatDialog,
    private orgService: EmpOrganizationService,
    private empInfoService: EmpInformationService
  ){}

  ngOnInit(): void {
    // Retrieve the authenticated employee's email.
    const employeeEmail = this.authService.getLoggedInEmail();
    if (!employeeEmail) {
      console.error('Employee email not found. Please ensure the user is logged in.');
      return;
    }

    // Fetch organization data.
    this.orgService.getOrganizationData(employeeEmail).subscribe({
      next: (data: OrganizationData) => {
        this.organizationData = data;
        if (data.image) {
          // Convert the byte array into a Base64 string.
          // Note: Depending on your backend/HTTP configuration you may need additional conversion.
          // this.imageSrc = this.convertByteArrayToBase64(data.image);
          this.imageSrc = 'data:image/png;base64,' + data.image;
        }
      },
      error: (err) => console.error('Error fetching organization data:', err)
    });

    // Fetch employee full name
    this.empInfoService.getEmployeeInformation(employeeEmail).subscribe({
      next: (empInfo: EmployeeInformation) => {
        if (empInfo.profileImage) {
          // Convert the byte array into a Base64 string.
          this.empImageSrc = 'data:image/png;base64,' + empInfo.profileImage;
        }
        this.employeeFullName = empInfo.fullName;
      },
      error: (err) => {
        console.error('Error fetching employee information:', err);
      }
    });

  }

  toggleDropdown() {
      this.isDropdownOpen = !this.isDropdownOpen;
  }

  // This example expects the input to be a Uint8Array.
  private convertByteArrayToBase64(byteArray: Uint8Array): string {
    let binary = '';
    const len = byteArray.byteLength;
    for (let i = 0; i < len; i++) {
      binary += String.fromCharCode(byteArray[i]);
    }
    console.log('data:image/png;base64,' + window.btoa(binary));
    
    return 'data:image/png;base64,' + window.btoa(binary);
  }

  logOutClicked(){
    console.log("logout clicked")
    this.router.navigate(['']);
  }

  // Opens the Help Details dialog.
  openHelpDetailsDialog(): void {
    this.dialog.open(EmpHelpDetailsComponent, {
      width: '400px',
      // You can pass additional data if needed:
      data: { selectedDate: new Date() }
    });
  }
}
