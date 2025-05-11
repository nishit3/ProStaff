import { Component, Inject } from '@angular/core';
import { EmpHelpDetailsService, EmployeeEmailWrapper, HelpDetails } from '../services/emp-help-details.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
// import { Employee } from '../services/employee.service';
import { AuthServiceService } from '../services/auth-service.service';

@Component({
  selector: 'app-emp-help-details',
  standalone: false,
  templateUrl: './emp-help-details.component.html',
  styleUrl: './emp-help-details.component.css'
})
export class EmpHelpDetailsComponent {
  isEditing = false;
  countryCodes: string[] = ['+91'];
  selectedCountryCode = '+91';
  helpDetails: HelpDetails = { email: '',
    phoneNumber: ''
  };

  constructor(
    public dialogRef: MatDialogRef<EmpHelpDetailsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { selectedDate: Date },
    private empHelpDetailsService: EmpHelpDetailsService,
    private authService: AuthServiceService
  ) {}

  ngOnInit(): void {
    
    const employeeEmail = this.authService.getLoggedInEmail();
    if (employeeEmail) {
      this.empHelpDetailsService.getHelpDetails(employeeEmail).subscribe({
        next: (res: HelpDetails) => {
          this.helpDetails = res;
        },
        error: (err) => console.error('Error loading HelpDetails', err)
      });
    } else {
      console.error('No employee email available');
    }
  }
}
