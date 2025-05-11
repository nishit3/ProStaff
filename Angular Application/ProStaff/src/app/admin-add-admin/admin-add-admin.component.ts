import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-admin-add-admin',
  standalone: false,
  templateUrl: './admin-add-admin.component.html',
  styleUrl: './admin-add-admin.component.css'
})
export class AdminAddAdminComponent {
  name: string = '';
  email: string = '';
  totalEmp = 0
  
  constructor(
    public dialogRef: MatDialogRef<AdminAddAdminComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    // If a date is passed when opening the dialog, set it as default
  }

  // Handle form submission
  submitDesign(): void {
    if (!this.name.trim() || !this.email.trim()) {
      alert('Please fill all fields before submitting.');
      return;
    }

    const design = {
      name: this.name,
      email: this.email,   
    }
    // Send data back to the parent component
    this.dialogRef.close(design);
  }

  // Handle cancel button
  closeDialog(): void {
    this.dialogRef.close(null);
  }
}
