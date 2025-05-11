import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AuthServiceService } from '../services/auth-service.service';
import { DepartmentServiceService } from '../services/department-service.service';

@Component({
  selector: 'app-department-dialog-box',
  standalone: false,
  templateUrl: './department-dialog-box.component.html',
  styleUrl: './department-dialog-box.component.css'
})
export class DepartmentDialogBoxComponent implements OnInit {

  departmentsList: { id: number; name: string; selected: boolean }[] = [];

  constructor(
    public dialogRef: MatDialogRef<DepartmentDialogBoxComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private authService: AuthServiceService,
    private departmentService: DepartmentServiceService
  ) {}

  ngOnInit() {
    const adminEmail = this.authService.getLoggedInEmail();
    if (!adminEmail) return;

    this.departmentService.getAllDepartments(adminEmail).subscribe((depts) => {
      this.departmentsList = depts.map(dept => ({
        id: dept.id,
        name: dept.name,
        selected: this.data?.selectedDepts?.some((sel: any) => sel.id === dept.id) || false
      }));
      console.log("department list:",this.departmentsList)
    });
  }

  isAllSelected(): boolean {
    return this.departmentsList.every(dept => dept.selected);
  }

  toggleSelectAll(): void {
    const allSelected = this.isAllSelected();
    this.departmentsList.forEach(dept => dept.selected = !allSelected);
  }

  submitSelection(): void {
    const selectedDepts = this.departmentsList
      .filter(dept => dept.selected)
      .map(dept => ({ id: dept.id, name: dept.name }));

    this.dialogRef.close(selectedDepts);
  }

  closeDialog(): void {
    this.dialogRef.close(null);
  }
}
