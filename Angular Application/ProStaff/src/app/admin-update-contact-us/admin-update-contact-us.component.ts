import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
@Component({
  selector: 'app-admin-update-contact-us',
  standalone: false,
  templateUrl: './admin-update-contact-us.component.html',
  styleUrl: './admin-update-contact-us.component.css'
})
export class AdminUpdateContactUsComponent {
  contactNo: string = ''
  contactEmail: string = '';
  contactDescription: string = '';

  constructor(
    public dialogRef: MatDialogRef<AdminUpdateContactUsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    // If a date is passed when opening the dialog, set it as default
  }

  allowOnlyNumbers(event: KeyboardEvent) {
    const charCode = event.which ? event.which : event.keyCode;
    if (charCode < 48 || charCode > 57) {
      event.preventDefault(); // Prevent entering non-numeric characters
    }
  }

  // Handle form submission
  submitEvent(): void {
    if (!this.contactNo || !this.contactEmail.trim() || !this.contactDescription.trim()) {
      alert('Please fill all fields before submitting.');
      return;
    }

    const contactNumberAsNumber = Number(this.contactNo); // Convert to number

  if (isNaN(contactNumberAsNumber)) {
    alert('Please enter a valid contact number.');
    return;
  }

    // Send data back to the parent component
    this.dialogRef.close({
      date: this.contactNo,
      title: this.contactEmail,
      description: this.contactDescription
    });
  }

  // Handle cancel button
  closeDialog(): void {
    this.dialogRef.close(null);
  }

}
