import { AttendanceRecord } from './../services/emp-attendance.service';
import { Component, Input, model, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { LeaveRequestFormComponent } from '../leave-request-form/leave-request-form.component';
import { RestPasswordQuickDialogComponent } from '../rest-password-quick-dialog/rest-password-quick-dialog.component';
import { Router } from '@angular/router';
import { EmpUpcomingEventsService, UpcomingEvent } from '../services/emp-upcoming-events.service';
import { AuthServiceService } from '../services/auth-service.service';
import { EmpOrganizationService, OrganizationData } from '../services/emp-organization.service';
import { EmpHelpDetailsService, HelpDetails } from '../services/emp-help-details.service';
import { EmpNotificationsService, NotificationDetails } from '../services/emp-notifications.service';
import { EmpHelpDetailsComponent } from '../emp-help-details/emp-help-details.component';
import { EmpInformationService, EmployeeInformation } from '../services/emp-information.service';
import { LeaveRequest, LeaveRequestService } from '../services/leave-request.service';
import { EmpAttendanceService } from '../services/emp-attendance.service';
import { ChangeDetectorRef } from '@angular/core';
import { MatCalendarCellClassFunction } from '@angular/material/datepicker';

interface PerformanceItem {
  title: string;
  rating: number;
}

@Component({
  selector: 'app-emp-homepage',
  standalone: false,
  templateUrl: './emp-homepage.component.html',
  styleUrl: './emp-homepage.component.css'
})
export class EmpHomepageComponent implements OnInit {

  // Data models for API responses.
  upcomingEvents: UpcomingEvent[] = [];
  organizationData?: OrganizationData;
  helpDetails?: HelpDetails;
  notifications: NotificationDetails[] = [];
  employeeInformation?: EmployeeInformation;

  // Build performance list from employee rating.
  performanceList: PerformanceItem[] = [];

  // Leave requests and attendance records fetched from backend.
  leaveRequests: LeaveRequest[] = [];
  attendanceRecords: AttendanceRecord[] = [];
  
  selected: Date | null = null; // Declare the 'selected' property

  showAlert: boolean = false;

  // Store the employee email once retrieved.
  private email: string = '';

  // Flag to control calendar rendering after leaveRequests are loaded.
  attendanceRecordLoaded: boolean = false;

  constructor(
    public dialog: MatDialog,
    public router: Router,
    private authService: AuthServiceService,
    private upcomingEventsService: EmpUpcomingEventsService,
    private orgService: EmpOrganizationService,
    private helpDetailsService: EmpHelpDetailsService,
    private notificationsService: EmpNotificationsService,
    private infoService: EmpInformationService,
    private leaveRequestService: LeaveRequestService,
    private empAttendanceService: EmpAttendanceService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Retrieve the authenticated employee's email.
    const employeeEmail = this.authService.getLoggedInEmail();
    if (!employeeEmail) {
      console.error('Employee email not found. Please ensure the user is logged in.');
      return;
    }
    this.email=employeeEmail;

    // Fetch upcoming events.
    this.upcomingEventsService.getUpcomingEventsByEmail(employeeEmail).subscribe({
      next: (data: UpcomingEvent[]) => {
        console.log(data);
        
        this.upcomingEvents = data;
      },
      error: (err) => console.error('Error fetching upcoming events:', err)
    });

    // Fetch organization data.
    this.orgService.getOrganizationData(employeeEmail).subscribe({
      next: (data: OrganizationData) => {
        this.organizationData = data;
      },
      error: (err) => console.error('Error fetching organization data:', err)
    });

    // Fetch help details.
    this.helpDetailsService.getHelpDetails(employeeEmail).subscribe({
      next: (data: HelpDetails) => {
        this.helpDetails = data;
      },
      error: (err) => console.error('Error fetching help details:', err)
    });

    // Fetch employee notifications.
    this.notificationsService.getEmployeeNotifications(employeeEmail).subscribe({
      next: (data: NotificationDetails[]) => {
        this.notifications = data;
      },
      error: (err) => console.error('Error fetching notifications:', err)
    });

    // Fetch employee information which includes the rating.
    this.infoService.getEmployeeInformation(employeeEmail).subscribe({
      next: (data: EmployeeInformation) => {
        this.employeeInformation = data;
        // Build performance list from the rating object.
        if (data.rating) {
          this.performanceList = [
            { title: 'Punctuality', rating: data.rating.punctuality },
            { title: 'Performance', rating: data.rating.performance },
            { title: 'Soft Skills', rating: data.rating.softSkills },
            { title: 'Creativity', rating: data.rating.creativity }
          ];
        }
      },
      error: (err) => console.error('Error fetching employee information:', err)
    });

    // Fetch leave requests.
    this.leaveRequestService.getEmployeeLeaveRequest(employeeEmail).subscribe({
      next: (data: LeaveRequest[]) => {
        this.leaveRequests = data;
      },
      error: (err) => console.error('Error fetching leave requests:', err)
    });

    // Fetch attendance records.
    this.empAttendanceService.getEmployeeAttendance(employeeEmail).subscribe({
      next: (data: AttendanceRecord[]) => {
        this.attendanceRecords = data;
        this.attendanceRecordLoaded = true;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error fetching attendance records:', err)
    });
  }

  get averageRating(): number {
    if (this.performanceList.length > 0) {
      const total = this.performanceList.reduce((sum, item) => sum + item.rating, 0);
      return total / this.performanceList.length;
    }
    return 0;
  }

  // Generates an array of full stars based on the rating.
  getStars(rating: number): number[] {
    return Array(Math.floor(rating)).fill(1);
  }

  // Checks if the rating includes a half star.
  hasHalfStar(rating: number): boolean {
    return rating % 1 !== 0;
  }

  // The dateClass function returns a CSS class based on the attendance record and special rules.
  dateClass: MatCalendarCellClassFunction<Date> = (date: Date, view: 'month' | 'year' | 'multi-year'): string => {
    if (view !== 'month') return '';
    const today = new Date();
    const d = new Date(date.getFullYear(), date.getMonth(), date.getDate());
    const current = new Date(today.getFullYear(), today.getMonth(), today.getDate());
    
    // // No ring for future dates.
    // if (d > current) {
    //   return '';
    // }
    // If the day is Saturday (6) or Sunday (0), mark as holiday.
    // if (date.getDay() === 0) {
    //   return 'holiday-date'; // Assume yellow ring.
    // }
    // Check if there's an attendance record for this date.
    const record = this.attendanceRecords.find(att => {
      const attDate = new Date(att.date);
      return attDate.getFullYear() === date.getFullYear() &&
             attDate.getMonth() === date.getMonth() &&
             attDate.getDate() === date.getDate();
    });
    if (record) {
      if (record.status.toLowerCase() === 'present') {
        return 'present-date'; // Assume green ring.
      } else if (record.status.toLowerCase() === 'absent') {
        return 'absent-date'; // Assume red ring.
      } else if (record.status.toLowerCase() === 'holiday') {
        return 'holiday-date';
      }
    }
    return '';
  };

  // Opens a dialog for submitting a leave request.
  openLeaveRequestDialog(): void {
    const nextDay = new Date();
    nextDay.setDate(nextDay.getDate() + 1);
    this.dialog.open(LeaveRequestFormComponent, {
      width: '400px',
      data: { selectedDate: nextDay }
    });
  }

  // Calls the check-in API and optionally handles response feedback.
  onCheckIn(): void {
    this.empAttendanceService.checkIn(this.email).subscribe({
      next: (success) => {
        if (success) {
          console.log('Check-in successful.');
          // Optionally, update the attendance records after check-in.
          this.refreshAttendance();
        }
      },
      error: (err) => console.error('Error during check-in:', err)
    });
  }

  // Calls the check-out API and optionally handles response feedback.
  onCheckOut(): void {
    this.empAttendanceService.checkOut(this.email).subscribe({
      next: (success) => {
        if (success) {
          console.log('Check-out successful.');
          // Optionally, update the attendance records after check-out.
          this.refreshAttendance();
        }
      },
      error: (err) => console.error('Error during check-out:', err)
    });
  }

  // Refresh attendance records.
  private refreshAttendance(): void {
    this.empAttendanceService.getEmployeeAttendance(this.email).subscribe({
      next: (data: AttendanceRecord[]) => {
        this.attendanceRecords = data;
      },
      error: (err) => console.error('Error refreshing attendance records:', err)
    });
  }

  // Opens the reset password dialog.
  openResetPassDialog(): void {
    const dialogRef = this.dialog.open(RestPasswordQuickDialogComponent, {
      width: '500px'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result === 'success') {
        this.showResetSuccessAlert();
      }
    });
  }

  // Navigates to the FAQ page.
  openFAQ(): void {
    this.router.navigate(['/empFAQ']);
  }

  // Opens the help details dialog.
  openHelpDetailsDialog(): void {
    this.dialog.open(EmpHelpDetailsComponent, {
      width: '400px',
      data: { selectedDate: new Date() }
    });
  }

  // Displays an alert indicating that the reset password email was sent.
  showResetSuccessAlert(): void {
    this.showAlert = true;
    document.querySelector('.welcome-container')?.scrollIntoView({ behavior: 'smooth' });
    setTimeout(() => {
      const alertBox = document.querySelector('.global-alert');
      if (alertBox) { alertBox.classList.add('show'); }
    }, 500);
    setTimeout(() => {
      const alertBox = document.querySelector('.global-alert');
      if (alertBox) { alertBox.classList.add('exit-animation'); }
    }, 2000);
    setTimeout(() => { this.showAlert = false; }, 2500);
  }

  // Returns a border color based on the leave request status.
  getStatusColor(status: string): string {
    switch (status) {
      case 'PENDING': return '#D9D945';
      case 'REJECTED': return 'red';
      case 'ACCEPTED': return 'green';
      default: return 'black';
    }
  }
}
  

  // constructor(public dialog: MatDialog,public route : Router, private authService: AuthServiceService) { }
  // performanceList = [
  //   { title: 'Punctuality', rating: 4.5 },
  //   { title: 'Soft Skills', rating: 3.8 },
  //   { title: 'Performance', rating: 5 },
  //   { title: 'Creativity', rating: 4.2 },
  // ];
  // upcomingEvents = [
  //   { title: 'Tech Conference', date: 'April 5, 2025', description: 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Eligendi ratione nam soluta quibusdam harum fugiat fugit adipisci? Iusto laborum, dolores qui, rerum nostrum cupiditate maiores autem nemo est suscipit reprehenderit?' },
  //   { title: 'Marketing Workshop', date: 'April 10, 2025', description: 'A deep dive into digital marketing trends.' },
  //   { title: 'Networking Event', date: 'April 15, 2025', description: 'Expand your professional connections.' },
  //   { title: 'Product Launch', date: 'April 20, 2025', description: 'Introducing our newest product line.' }
  // ];
  // selectedDate: Date | null = new Date();

  // get averageRating(): number {
  //   const total = this.performanceList.reduce((sum, item) => sum + item.rating, 0);
  //   return this.performanceList.length ? total / this.performanceList.length : 0;
  // }

  // // Generate array of full stars based on rating
  // getStars(rating: number): number[] {
  //   return Array(Math.floor(rating)).fill(1);
  // }

  // // Check if there's a half star
  // hasHalfStar(rating: number): boolean {
  //   return rating % 1 !== 0;
  // }

  // selected = model<Date | null>(null);

  // leaveRequests = [
  //   {
  //     status: 'Pending',
  //     description: 'Awaiting approval from manager',
  //     type: 'Paid Leave',
  //     date: new Date('2025-03-28')
  //   },
  //   {
  //     status: 'Approved',
  //     description: 'Leave approved for personal reasons',
  //     type: 'Unpaid Leave',
  //     date: new Date('2025-04-02')
  //   },
  //   {
  //     status: 'Declined',
  //     description: 'Leave request declined due to project deadlines',
  //     type: 'Sick Leave',
  //     date: new Date('2025-04-05')
  //   },
  //   {
  //     status: 'Pending',
  //     description: 'Pending HR review',
  //     type: 'Casual Leave',
  //     date: new Date('2025-04-10')
  //   },
  //   {
  //     status: 'Approved',
  //     description: 'Leave granted for family function',
  //     type: 'Paid Leave',
  //     date: new Date('2025-04-15')
  //   }
  // ];

  // attendanceRecords = [
  //   { date: new Date(2025, 3, 1), status: 'Present' },  // Example attendance data
  //   { date: new Date(2025, 3, 2), status: 'Absent' },
  //   { date: new Date(2025, 3, 5), status: 'Present' },
  //   { date: new Date(2025, 3, 6), status: 'Holiday' }, // Holiday on a specific Saturday
  //   { date: new Date(2025, 3, 10), status: 'Absent' },
  //   { date: new Date(2025, 3, 15), status: 'Holiday' } // Holiday on a weekday
  // ];

  // dateClass = (date: Date): string => {
  //   // Check if the date is a Sunday (Default Holiday)
  //   if (date.getDay() === 0) {
  //     return 'holiday-date';
  //   }

  //   // Check if the date exists in the attendanceRecords list
  //   const foundRecord = this.attendanceRecords.find(att =>
  //     att.date.getFullYear() === date.getFullYear() &&
  //     att.date.getMonth() === date.getMonth() &&
  //     att.date.getDate() === date.getDate()
  //   );

  //   // If the record exists, return its corresponding status class
  //   if (foundRecord) {
  //     return foundRecord.status.toLowerCase() + '-date';
  //   }

  //   return ''; // Default (no class)
  // };

  // openLeaveRequestDialog() {

  //   const nextDay = new Date();
  //   nextDay.setDate(nextDay.getDate() + 1); // Increment date by 1 day

  //   this.dialog.open(LeaveRequestFormComponent, {
  //     width: '400px',
  //     data: { selectedDate: nextDay } // Pass the incremented date
  //   });

  // }
  // showAlert: boolean = false;

  // openResetPassDialog() {
  //   const dialogRef = this.dialog.open(RestPasswordQuickDialogComponent, {
  //     width: '500px',
  //   });

  //   // After dialog closes, check if the reset was successful
  //   dialogRef.afterClosed().subscribe(result => {
  //     if (result === 'success') { // Dialog will return 'success' on successful submission
  //       this.showResetSuccessAlert();
  //     }
  //   });
  // }

  // openFAQ() {
  //   this.route.navigate(['/empFAQ']);
  // }

  // showResetSuccessAlert() {
  //   this.showAlert = true;
  
  //   // First, scroll smoothly to the Welcome section
  //   document.querySelector('.welcome-container')?.scrollIntoView({ behavior: 'smooth' });
  
  //   // Add "show" class to trigger animation
  //   setTimeout(() => {
  //     const alertBox = document.querySelector('.global-alert');
  //     if (alertBox) {
  //       alertBox.classList.add('show');
  //     }
  //   }, 500); // Small delay to sync with scroll
  
  //   // Hide the alert after 2 seconds with exit animation
  //   setTimeout(() => {
  //     const alertBox = document.querySelector('.global-alert');
  //     if (alertBox) {
  //       alertBox.classList.add('exit-animation'); // Start exit animation
  //     }
  //   }, 2000);
  
  //   // Completely remove alert after animation completes
  //   setTimeout(() => {
  //     this.showAlert = false;
  //   }, 2500);
  // }
  
  



  // getStatusColor(status: string) {
  //   switch (status) {
  //     case 'Pending': return '#D9D945';
  //     case 'Declined': return 'red';
  //     case 'Approved': return 'green';
  //     default: return 'black';
  //   }
  // }

// }
