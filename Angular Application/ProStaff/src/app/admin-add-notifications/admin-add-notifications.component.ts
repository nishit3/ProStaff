import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { TeamsDialogBoxComponent } from '../teams-dialog-box/teams-dialog-box.component';
import { DepartmentDialogBoxComponent } from '../department-dialog-box/department-dialog-box.component';
import { DesignationDialogBoxComponent } from '../designation-dialog-box/designation-dialog-box.component';

@Component({
  selector: 'app-admin-add-notifications',
  standalone: false,
  templateUrl: './admin-add-notifications.component.html',
  styleUrl: './admin-add-notifications.component.css'
})
export class AdminAddNotificationsComponent {
  department: string = '';
  designation: string = '';
  message: string = '';
  teams: string = '';
  selectedTeams: { id: number, name: string }[] = [];
  selectedDepts: { id: number, name: string }[] = [];
  selectedDesg: { id: number, name: string }[] = [];


  constructor(
    public dialogRef: MatDialogRef<AdminAddNotificationsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialog: MatDialog
  ) {}

  allowOnlyNumbers(event: KeyboardEvent) {
    const charCode = event.which ? event.which : event.keyCode;
    if (charCode < 48 || charCode > 57) {
      event.preventDefault(); // Prevent entering non-numeric characters
    }
  }

  // Open Team Selection Dialog
  openTeamSelectionDialog(): void {
    const dialogRef = this.dialog.open(TeamsDialogBoxComponent, {
      width: '300px',
      data: { selectedTeams: [...this.selectedTeams] }
    });

    dialogRef.afterClosed().subscribe((result: { id: number, name: string }[]) => {
      if (result) {
        this.selectedTeams = result;
        this.teams = result.map(team => team.name).join(', ');
      }
    });
  }

  openDesgSelectionDialog(): void {
    const dialogRef = this.dialog.open(DesignationDialogBoxComponent, {
      width: '400px',
      data: { selectedDesg: [...this.selectedDesg] }
    });

    dialogRef.afterClosed().subscribe((result: { id: number, name: string }[]) => {
      if (result) {
        this.selectedDesg = result;
        this.designation = result.map(desg => desg.name).join(', ');
      }
    });
  }

  openDeptSelectionDialog(): void {
    const dialogRef = this.dialog.open(DepartmentDialogBoxComponent, {
      width: '350px',
      data: { selectedDepts: [...this.selectedDepts] }
    });

    dialogRef.afterClosed().subscribe((result: { id: number, name: string }[]) => {
      if (result) {
        this.selectedDepts = result;
        this.department = result.map(dept => dept.name).join(', ');
      }
    });
  }

  // Handle form submission
  submitEvent() {
    // if (!this.department || !this.designation || !this.message.trim() || !this.teams) {
    //   alert('Please fill all fields before submitting.');
    //   return;
    // }
  
    const newNotification = {
      message: this.message,
      teams: this.selectedTeams.map(team => team.id),
      departments: this.selectedDepts.map(dept => dept.id),
      designations: this.selectedDesg.map(desg => desg.id)
    };
  
    this.dialogRef.close(newNotification); // Send data & close dialog
  }
  

  // Handle cancel button
  closeDialog(): void {
    this.dialogRef.close(null);
  }
}
