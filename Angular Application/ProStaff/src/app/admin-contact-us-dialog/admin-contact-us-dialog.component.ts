import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AdminContactUsService, AdminEmailWrapper, EmployeeEmailWrapper, HelpDetails } from '../services/admin-contact-us.service';
import { AuthServiceService } from '../services/auth-service.service';

@Component({
  selector: 'app-admin-contact-us-dialog',
  standalone: false,
  templateUrl: './admin-contact-us-dialog.component.html',
  styleUrl: './admin-contact-us-dialog.component.css'
})
export class AdminContactUsDialogComponent implements OnInit {

  isEditing = false;
  selectedCountryCode = '+91';  
  countryCodes = ['+1', '+44', '+91', '+81', '+444'];
  helpDetails: HelpDetails = { id: 0, email: '', phoneNumber: '', countryCode: '+91' };

  constructor(
    public dialogRef: MatDialogRef<AdminContactUsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { selectedDate: Date },
    private contactUsService: AdminContactUsService,
    private authService: AuthServiceService
  ) {}

  ngOnInit(): void {
    
    const wrapper: EmployeeEmailWrapper = { employeeEmail: this.authService.getLoggedInEmail()! }; // Replace dynamically as needed
    // console.log("Sending email to backend:", wrapper.employeeEmail);

    this.contactUsService.getHelpDetails(wrapper).subscribe({
      next: (res: any) => {
        this.helpDetails = res;
        this.selectedCountryCode = '+91';
      },
      error: (err: any) => console.error('Error loading HelpDetails', err)
    });
  }

  toggleEdit() {
    this.isEditing = !this.isEditing;
  }

  saveChanges() {
    const wrapper: AdminEmailWrapper = { adminEmail: this.authService.getLoggedInEmail()! }; // Replace with actual admin email

    const updatedHelp: HelpDetails = {
      ...this.helpDetails,
      countryCode: '+91' // Set the default country code to +91
    };

    this.contactUsService.updateHelpDetails(wrapper, updatedHelp).subscribe({
      next: (res : any) => {
        alert('Details updated successfully!');
        this.isEditing = false;
      },
      error: (err : any) =>{
        console.error('Update failed', err);
        alert('Something went wrong. Please try again.');
      }  
        
    });
  }


  // constructor(
  //   public dialogRef: MatDialogRef<AdminContactUsDialogComponent>,
  //   @Inject(MAT_DIALOG_DATA) public data: { selectedDate: Date }
  // ) { }
  // isEditing: boolean = false;
  // selectedCountryCode: string = '+91';
  // countryCodes: string[] = ['+1', '+44', '+91', '+81','+444'];
  // email: string = 'abcd@gmail.com';
  // contactNo: string = '1234567890';

  // toggleEdit() {
  //   this.isEditing = !this.isEditing;
  // }
}
