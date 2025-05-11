import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AuthServiceService } from '../services/auth-service.service';
import { DesignationService } from '../services/designation.service';

@Component({
  selector: 'app-designation-dialog-box',
  standalone: false,
  templateUrl: './designation-dialog-box.component.html',
  styleUrl: './designation-dialog-box.component.css'
})
export class DesignationDialogBoxComponent implements OnInit{
  designationsList: { id: number; name: string; selected: boolean }[] = [];

  constructor(
    public dialogRef: MatDialogRef<DesignationDialogBoxComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private authService: AuthServiceService,
    private designationService: DesignationService
  ) { }

  isAllSelected(): boolean {
    return this.designationsList.every(desg => desg.selected);
  }

  // Toggle select all functionality
  ngOnInit() {
    const adminEmail = this.authService.getLoggedInEmail();
    if (!adminEmail) return;

    this.designationService.getAllDesignations(adminEmail).subscribe((desgs) => {
      this.designationsList = desgs.map(desg => ({
        id: desg.id,
        name: desg.name,
        selected: this.data?.selectedDesg?.some((sel: any) => sel.id === desg.id) || false
      }));
      console.log("designation list:",this.designationsList)
    });
  }


  toggleSelectAll(): void {
    const allSelected = this.isAllSelected();
    this.designationsList.forEach(desg => desg.selected = !allSelected);
  }

  submitSelection(): void {
    const selectedDesg = this.designationsList
      .filter(desg => desg.selected)
      .map(desg => ({ id: desg.id, name: desg.name }));

    this.dialogRef.close(selectedDesg);
  }

  closeDialog(): void {
    this.dialogRef.close(null);
  }
}
