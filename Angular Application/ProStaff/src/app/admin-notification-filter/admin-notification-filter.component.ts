import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { TeamsDialogBoxComponent } from '../teams-dialog-box/teams-dialog-box.component';
import { DesignationDialogBoxComponent } from '../designation-dialog-box/designation-dialog-box.component';
import { DepartmentDialogBoxComponent } from '../department-dialog-box/department-dialog-box.component';

@Component({
  selector: 'app-admin-notification-filter',
  standalone: false,
  templateUrl: './admin-notification-filter.component.html',
  styleUrl: './admin-notification-filter.component.css'
})
export class AdminNotificationFilterComponent implements OnInit {
  department: string = '';
  designation: string = '';
  teams: string = '';
  selectedTeams: { id: number, name: string }[] = [];
  selectedDepts: { id: number, name: string }[] = [];
  selectedDesg: { id: number, name: string }[] = [];

  constructor(
    public dialogRef: MatDialogRef<AdminNotificationFilterComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialog: MatDialog
  ) {}

  ngOnInit() {
    // Initialize values safely
    this.selectedTeams = this.data?.selectedTeams || [];
    this.selectedDepts = this.data?.selectedDepartments || [];
    this.selectedDesg = this.data?.selectedDesignations || [];

    // Create string to display in input fields
    this.teams = this.selectedTeams.map(team => team.name).join(', ');
    this.department = this.selectedDepts.map(dept => dept.name).join(', ');
    this.designation = this.selectedDesg.map(desg => desg.name).join(', ');
  }

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

  submitEvent() {
    this.dialogRef.close({
      designations: this.selectedDesg,
      departments: this.selectedDepts,
      teams: this.selectedTeams // Includes both id and name
    });
  }

  closeDialog(): void {
    this.dialogRef.close(null);
  }
}
