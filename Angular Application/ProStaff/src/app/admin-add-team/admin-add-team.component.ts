import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-admin-add-team',
  standalone: false,
  templateUrl: './admin-add-team.component.html',
  styleUrl: './admin-add-team.component.css'
})
export class AdminAddTeamComponent {
  name: string = '';
  description: string = '';
  totalEmp = 0
  constructor(
    public dialogRef: MatDialogRef<AdminAddTeamComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    // If a date is passed when opening the dialog, set it as default
  }

  // Handle form submission
  submitTeam(): void {
    if (!this.name.trim() || !this.description.trim()) {
      alert('Please fill all fields before submitting.');
      return;
    }

    const team = {
      name: this.name,
      description: this.description,
    }
    // Send data back to the parent component
    this.dialogRef.close(team);
  }

  // Handle cancel button
  closeDialog(): void {
    this.dialogRef.close(null);
  }
}
