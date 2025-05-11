import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-admin-add-department',
  standalone: false,
  templateUrl: './admin-add-department.component.html',
  styleUrl: './admin-add-department.component.css'
})
export class AdminAddDepartmentComponent {
  name: string = '';
  description: string = '';
  totalEmp = 0
  constructor(
    public dialogRef: MatDialogRef<AdminAddDepartmentComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    // If a date is passed when opening the dialog, set it as default
  }

  // Handle form submission
  submitDept(): void {
    if (!this.name.trim() || !this.description.trim()) {
      alert('Please fill all fields before submitting.');
      return;
    }

    const dept = {
      name: this.name,
      description: this.description,   
    }
    // Send data back to the parent component
    this.dialogRef.close(dept);
  }

  // Handle cancel button
  closeDialog(): void {
    this.dialogRef.close(null);
  }

}
