import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-admin-add-designation',
  standalone: false,
  templateUrl: './admin-add-designation.component.html',
  styleUrl: './admin-add-designation.component.css'
})
export class AdminAddDesignationComponent {
  name: string = '';
  description: string = '';
  totalEmp = 0
  constructor(
    public dialogRef: MatDialogRef<AdminAddDesignationComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    // If a date is passed when opening the dialog, set it as default
  }

  // Handle form submission
  submitDesign(): void {
    if (!this.name.trim() || !this.description.trim()) {
      alert('Please fill all fields before submitting.');
      return;
    }

    const design = {
      name: this.name,
      totalEmployees: this.totalEmp,
      description: this.description,   
    }
    // Send data back to the parent component
    this.dialogRef.close(design);
  }

  // Handle cancel button
  closeDialog(): void {
    this.dialogRef.close(null);
  }
}
