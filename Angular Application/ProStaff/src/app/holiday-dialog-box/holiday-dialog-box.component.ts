import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-holiday-dialog-box',
  standalone: false,
  templateUrl: './holiday-dialog-box.component.html',
  styleUrl: './holiday-dialog-box.component.css'
})
export class HolidayDialogBoxComponent {
  holidayDate: string = '';
  isHoliday: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<HolidayDialogBoxComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { selectedDate: Date, isHoliday: boolean }
  ) {
    if (data && data.selectedDate) {
      this.holidayDate = this.formatDate(data.selectedDate);
    }
    this.isHoliday = data.isHoliday;
  }

  formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  // Called when ADD or DELETE is clicked
  handleAction() {
    const actionType = this.isHoliday ? 'delete' : 'add';
    this.dialogRef.close({
      holidayDate: this.holidayDate,
      action: actionType
    });
  }

  closeDialog(shouldSubmit: boolean) {
    this.dialogRef.close({
      shouldSubmit,
      holidayDate: this.holidayDate
    });
  }
}
