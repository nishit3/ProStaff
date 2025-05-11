import { Component, OnInit } from '@angular/core';
import { MatCalendarCellClassFunction} from '@angular/material/datepicker';
import { MatDialog } from '@angular/material/dialog';
import { LeaveRequestFormComponent } from '../leave-request-form/leave-request-form.component';
import { AdminAddEventComponent } from '../admin-add-event/admin-add-event.component';
import { AdminUpdateContactUsComponent } from '../admin-update-contact-us/admin-update-contact-us.component';
import { AdminAddNotificationsComponent } from '../admin-add-notifications/admin-add-notifications.component';
import { TeamsDialogBoxComponent } from '../teams-dialog-box/teams-dialog-box.component';
import { EmpInformationService } from '../services/emp-information.service';
import { LeaveRequest, LeaveRequestService } from '../services/leave-request.service';
import { AttendanceRecord, EmpAttendanceService } from '../services/emp-attendance.service';
import { ChangeDetectorRef } from '@angular/core';
// import { MatCardModule } from '@angular/material/card';
// import { FormsModule } from '@angular/forms';
// import { MatNativeDateModule } from '@angular/material/core';
@Component({
  selector: 'app-emp-profile',
  standalone: false,
  templateUrl: './emp-profile.component.html',
  styleUrl: './emp-profile.component.css'
})
export class EmpProfileComponent implements OnInit {

  employeeDetails: any[] = [];
  bankDetails: any[] = [];
  selectedDate: Date | null = new Date();
  fullName: string='';
  designation: string='';
  empImageSrc?: string;

  leaveRequests: LeaveRequest[] = [];
  attendanceRecords: AttendanceRecord[] = [];

  // Flag to control calendar rendering after leaveRequests are loaded.
  leaveRequestsLoaded: boolean = false;

  constructor(
    public dialog: MatDialog, 
    private employeeService: EmpInformationService,
    private leaveRequestService: LeaveRequestService,
    private empAttendanceService: EmpAttendanceService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const email = localStorage.getItem('email');
    if (!email) {
      console.error('Employee email not found. Please ensure the user is logged in.');
      return;
    }
   
    this.employeeService.getEmployeeInformation(email!).subscribe({
      next: (res: any) => { // 🔁 used to be: res: any[]
        const data = res;   // 🔁 no more `res[0]` needed!
       
        if (data.profileImage) {
          // Convert the byte array into a Base64 string.
          this.empImageSrc = 'data:image/png;base64,' + data.profileImage;
        }
 
        this.employeeDetails = [
          { title: 'Department', value: data.department.name, icon: 'fa fa-building' },
          { title: 'Reporting Manager', value: data.reportingManager, icon: 'fa fa-user-tie' },
          { title: 'Email', value: data.email, icon: 'fa fa-envelope' },
          { title: 'Mobile Phone', value: '+91 ' + data.phoneNumber, icon: 'fa fa-phone' },
          { title: 'Emergency Contact', value: '+91 ' + data.emergencyContact, icon: 'fa fa-exclamation-triangle' },
          { title: 'Age', value: data.age, icon: 'fa fa-birthday-cake' },
          { title: 'DOB', value: data.dob, icon: 'fa fa-calendar-alt' },
          { title: 'Joining Date', value: data.joiningDate, icon: 'fa fa-user-clock' },
          { title: 'Teams', value: [data.teams.map((t: any) => t.name)], icon: 'fa fa-users' },
          { title: 'Skills', value: [data.skills], icon: 'fa fa-code' },
          // { title: 'FullName', value: data.fullName, icon: 'fa fa-user' },
          // { title: 'Designation', value: data.designation.name, icon: 'fa fa-user-tag' }
        ];        
 
        this.fullName=data.fullName;
        this.designation=data.designation.name;
 
        this.bankDetails = [
          { title: 'Bank Name', value: data.bankDetail.bankName },
          { title: 'IFSC Code', value: data.bankDetail.ifscCode },
          { title: 'Account Number', value: data.bankDetail.accountNumber },
          { title: 'Branch', value: data.bankDetail.branch }
        ];
      },
      error: (err) => {
        console.error('Error loading employee data:', err);
      }
    });

    // Fetch leave requests.
    this.leaveRequestService.getEmployeeLeaveRequest(email).subscribe({
      next: (data: LeaveRequest[]) => {
        this.leaveRequests = data;
        this.leaveRequestsLoaded = true;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error fetching leave requests:', err)
    });

    // Fetch attendance records.
    this.empAttendanceService.getEmployeeAttendance(email).subscribe({
      next: (data: AttendanceRecord[]) => {
        this.attendanceRecords = data;
      },
      error: (err) => console.error('Error fetching attendance records:', err)
    });
  }
 
  flattenArray(value: any): string[] {
    return value.flat(Infinity);
  }

  formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

 
  isArray(value: any): boolean {
    return Array.isArray(value);
  }
 
  /**
   * dateClass is used to decorate the calendar cells:
   * - Future dates: no decoration.
   * - If a leave request exists for that date:
   *    Pending → 'pending-leave-date' (yellow ring)
   *    Approved → 'approved-leave-date' (green ring)
   *    Rejected → 'rejected-leave-date' (red ring)
   */
  dateClass = (date: Date): string => {
    const today = new Date();
    const d = new Date(date.getFullYear(), date.getMonth(), date.getDate());
    const current = new Date(today.getFullYear(), today.getMonth(), today.getDate());
    
    // // Do not decorate future dates.
    // if (d > current) {
    //   return '';
    // }
    
    // Look for a leave request with leaveDate equal to this date.
    const request = this.leaveRequests.find(req => {
      const reqDate = new Date(req.leaveDate);

      return reqDate.getFullYear() === date.getFullYear() &&
             reqDate.getMonth() === date.getMonth() &&
             reqDate.getDate() === date.getDate();
    });
    if (request) {
      const status = request.status.toUpperCase();
      
      if (status === 'PENDING') {      
        return 'pending-date'; // Yellow ring.
      } else if (status === 'ACCEPTED') {
        return 'approved-date'; // Green ring.
      } else if (status === 'REJECTED') {
        return 'declined-date'; // Red ring.
      }
    }
    return '';
  };

  /**
   * Called when a date is selected on the calendar.
   * If a leave request exists for that date, open the form with that leave data.
   * Otherwise, open an empty leave request form with the selected date.
   */
  onDateSelected(selectedDate: Date): void {
    const adjustedDate = new Date(selectedDate);
    adjustedDate.setDate(adjustedDate.getDate() + 1); // fix timezone offset
  
    const existingRequest = this.leaveRequests.find(req => {
      const reqDate = new Date(req.leaveDate);
      return reqDate.getFullYear() === adjustedDate.getFullYear() &&
             reqDate.getMonth() === adjustedDate.getMonth() &&
             reqDate.getDate() === adjustedDate.getDate();
    });
  
    if (existingRequest) {
      this.dialog.open(LeaveRequestFormComponent, {
        width: '400px',
        data: {
          leaveData: existingRequest.leaveDate,
          reason: existingRequest.reason,
          type: existingRequest.type,
          status: existingRequest.status,
          selectedDate: adjustedDate
        }
      });
    } else {
      this.dialog.open(LeaveRequestFormComponent, {
        width: '400px',
        data: { selectedDate: adjustedDate }
      });
    }
  }
  
  
}

//   constructor(public dialog: MatDialog) {}
//   employeeDetails = [
//     { title: 'Department', value: 'IT', icon: 'fa fa-building' },
//     { title: 'Reporting Manager', value: 'Jane Smith', icon: 'fa fa-user-tie' },
//     { title: 'Email', value: 'john.doe@example.com', icon: 'fa fa-envelope' },
//     { title: 'Location', value: 'New York, USA', icon: 'fa fa-map-marker-alt' },
//     { title: 'Mobile Phone', value: '+1 234 567 890', icon: 'fa fa-phone' },
//     { title: 'Emergency Contact', value: '+1 987 654 321', icon: 'fa fa-exclamation-triangle' },
//     { title: 'Age', value: 33, icon: 'fa fa-birthday-cake' },
//     { title: 'DOB', value: '01 Jan 1990', icon: 'fa fa-calendar-alt' },
//     { title: 'Joining Date', value: '10 March 2015', icon: 'fa fa-user-clock' },

//     // Nested array example
//     { title: 'Teams', value: [['Python', 'Java', 'Angular', 'React']], icon: 'fa fa-users' },
//     { title: 'Skills', value: [['Node.js', 'MongoDB', 'Express', 'GraphQL']], icon: 'fa fa-code' },
//     // { title: 'Languages', value: [['English', 'Spanish', 'French']], icon: 'fa fa-language' }
//   ];
  
//   flattenArray(value: any): string[] {
//     return value.flat(Infinity); // Flattens deeply nested arrays into a single-level array
//   }
//   isArray(value: any): boolean {
//     return Array.isArray(value);
//   }

//   listValues: { [key: string]: string[] } = {
//     teams: ['Python', 'Java', 'Angular']
//   };

//   getValue(detail: any): any {
//     return this.listValues[detail.value] || detail.value;
//   }

//   // selected: Date | null = new Date();
//   selectedDate: Date | null = new Date();
//   // minimumDate: Date = new Date(); // Set minimum selectable date
//   // restrictedBookingDates: Date = new Date(2026, 11, 31); // Example max date

//   leaveRequests = [
//     { date: new Date(2025, 2, 19), status: 'pending' },
//     { date: new Date(2025, 2, 20), status: 'pending' },   // Yellow
//     { date: new Date(2025, 2, 30), status: 'declined' },
//     { date: new Date(2025, 2, 22), status: 'approved' },  // Green
//     { date: new Date(2025, 2, 25), status: 'declined' }   // Red
//   ];

//   // ✅ Function to return class based on the date
//   dateClass = (date: Date): string => {
//     const foundLeave = this.leaveRequests.find(leave =>
//       leave.date.getFullYear() === date.getFullYear() &&
//       leave.date.getMonth() === date.getMonth() &&
//       leave.date.getDate() === date.getDate()
//     );

//     return foundLeave ? foundLeave.status + '-date' : ''; // Assigning class dynamically
//   };

//   bankDetails = [
//     { title: 'Bank Name', value: 'ABC Bank' },
//     { title: 'IFSC Code', value: 'ABCD0123456' },
//     { title: 'Account Number', value: '123456789012' },
//     { title: 'Branch', value: 'Downtown Branch' }
//   ];

//   openLeaveRequestDialog(date: Date | null) {
//     if (date) {  
//       const nextDay = new Date(date);
//       nextDay.setDate(nextDay.getDate() + 1); // Increment date by 1 day
  
//       this.dialog.open(LeaveRequestFormComponent, {
//         width: '400px',
//         data: { selectedDate: nextDay } // Pass the incremented date
//       });
      
//     }
//   }
  
  
// }
