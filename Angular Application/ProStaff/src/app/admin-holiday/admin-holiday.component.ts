import { Component, Inject, OnInit, ChangeDetectorRef, ViewChild } from '@angular/core';
import { MatCalendarCellClassFunction, MatCalendar } from '@angular/material/datepicker';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { HolidayDialogBoxComponent } from '../holiday-dialog-box/holiday-dialog-box.component';
import { AttendanceRecord, AttendanceService } from '../services/attendance.service';
import { AuthServiceService } from '../services/auth-service.service';

@Component({
  selector: 'app-admin-holiday',
  standalone: false,
  templateUrl: './admin-holiday.component.html',
  styleUrl: './admin-holiday.component.css'
})
export class AdminHolidayComponent implements OnInit {
  holidaysLoaded: boolean = false;
  holidayDates: AttendanceRecord[] = [];
  holidayDescription: string = '';
  selected = null;
  today = new Date();
  adminEmail: string | null = '';

  @ViewChild(MatCalendar) calendar!: MatCalendar<Date>; // ✅ Calendar reference

  constructor(
    public dialogRef: MatDialogRef<AdminHolidayComponent>,
    private dialog: MatDialog,
    private attendanceService: AttendanceService,
    private authService: AuthServiceService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.adminEmail = this.authService.getLoggedInEmail();
    if (!this.adminEmail) return;
    this.getHolidayList();
    console.log("Holiday:",this.holidayDates)
  }

  getHolidayList() {

    this.attendanceService.getAllHolidays(this.adminEmail!).subscribe({
      next: (holidays) => {
        this.holidayDates = holidays;
        this.holidaysLoaded = true;
        this.cdr.detectChanges();
        if (this.calendar) this.calendar.updateTodaysDate(); // ✅ Force update after data change
      },
      error: err => {
        console.error('Error fetching holidays:', err);
      }
    });
  }

  formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  dateClass: MatCalendarCellClassFunction<Date> = (date: Date, view: 'month' | 'year' | 'multi-year'): string => {
    if (view !== 'month') return '';
    const adjustedDate = new Date(date);
    adjustedDate.setDate(adjustedDate.getDate() + 1);
    const formattedDate = this.formatDate(adjustedDate);

    const isHoliday = this.holidayDates.some(
      record => record.date === formattedDate && record.status === 'HOLIDAY'
    );

    return isHoliday ? 'holiday-date' : '';
  };

  openAddHolidayDialog(date: Date | null) {
    if (date) {
      const nextDay = new Date(date);
      nextDay.setDate(nextDay.getDate() + 1);
      const formattedDate = this.formatDate(nextDay);
      const todayStr = this.formatDate(this.today);
  
      const isAlreadyHoliday = this.holidayDates.some(
        record => record.date === formattedDate && record.status === 'HOLIDAY'
      );
  
      const isBeforeToday = formattedDate < todayStr;
  
      // ❌ Prevent deletion for past dates (but allow adding)
      if (isAlreadyHoliday && isBeforeToday) {
        // alert("Cannot delete holidays from past dates.");
        return;
      }
  
      const dialogRef = this.dialog.open(HolidayDialogBoxComponent, {
        width: '400px',
        data: {
          selectedDate: nextDay,
          isHoliday: isAlreadyHoliday
        }
      });
  
      dialogRef.afterClosed().subscribe(result => {
        if (result?.action === 'add') {
          this.attendanceService.addHoliday(result.holidayDate, this.adminEmail!)
            .subscribe({
              next: (res) => {
                if (res === true) {
                  alert('Holiday added.');
                  this.getHolidayList();
                }
              },
              error: err => {
                console.error('Error adding holiday:', err);
              }
            });
  
        } else if (result?.action === 'delete') {
          this.attendanceService.deleteHoliday(result.holidayDate, this.adminEmail!)
            .subscribe({
              next: (res) => {
                if (res === true) {
                  alert('Holiday deleted.');
                  this.getHolidayList();
                }
              },
              error: err => {
                console.error('Error deleting holiday:', err);
              }
            });
        }
  
        this.selected = null;
      });
    }
  }
  

  closeDialog() {
    this.dialogRef.close();
  }
}
