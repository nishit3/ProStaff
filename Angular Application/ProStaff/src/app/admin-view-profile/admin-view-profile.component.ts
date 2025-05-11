import { Component, model, OnInit, provideExperimentalCheckNoChangesForDebug } from '@angular/core';
import { EmployeeInfo, EmployeeService } from '../services/employee.service';
import { DesignationService } from '../services/designation.service';
import { TeamsService } from '../services/teams.service';
import { DepartmentServiceService } from '../services/department-service.service';
import { MatDialog } from '@angular/material/dialog';
import { TeamsDialogBoxComponent } from '../teams-dialog-box/teams-dialog-box.component';
import { AttendanceRecord, AttendanceService } from '../services/attendance.service';
import { AuthServiceService } from '../services/auth-service.service';
import { MatCalendarCellClassFunction } from '@angular/material/datepicker';
import { SalaryService } from '../services/salary.service';

@Component({
  selector: 'app-admin-view-profile',
  standalone: false,
  templateUrl: './admin-view-profile.component.html',
  styleUrl: './admin-view-profile.component.css'
})
export class AdminViewProfileComponent implements OnInit {
  constructor(
    private employeeService: EmployeeService,
    private designationService: DesignationService,
    private departmentService: DepartmentServiceService,
    private teamService: TeamsService,
    private attendanceService: AttendanceService,
    private authService : AuthServiceService,
    private salaryService : SalaryService,
    private dialog: MatDialog
  ) { }
  isEditing: boolean = false;
  skills = '';
  selectedFile: File | null = null;
  empEmail: string | null = ''
  punctuality: number = 0;
  performance: number = 0;
  softSkills: number = 0;
  creativity: number = 0;
  bankName = '';
  ifcCode = '';
  accountNumber = '';
  branchName = '';
  departments: {
    id: number;
    name: string;
    description: string;
    employeeCount: number;
  }[] = [];
  teamsList: { id: number; name: string }[] = [];
  designationsList: {
    id: number;
    name: string;
    description: string;
    employeeCount: number;
  }[] = [];
  selectedTeams: { id: number; name: string }[] = [];
  teams: string = '';
  searchTerm: string = '';
  selected: Date | null = null;
  showPopover: boolean = false;
  popoverPosition = { top: 0, left: 0 };
  checkInTime: string = '';
  checkOutTime: string = '';
  mouseEvent!: MouseEvent;
  attendanceData: AttendanceRecord[] = [];
  salary: number = 0
  employeeData: EmployeeInfo = {
    email: '',
    fullName: '',
    gender: 'MALE',
    skills: [],
    reportingManager: '',
    phoneNumber: '',
    dob: '',
    profileImage: '',
    joiningDate: '',
    emergencyContact: '',
    bankDetail: {
      bankName: '',
      ifscCode: '',
      accountNumber: '',
      branch: ''
    },
    rating: {
      punctuality: 0,
      performance: 0,
      softSkills: 0,
      creativity: 0
    },
    age: 0,
    department: {
      id: 0,
      name: '',
      description: '',
      employeeCount: 0
    },
    designation: {
      id: 0,
      name: '',
      description: '',
      employeeCount: 0
    },
    teams: [],
    address: ''
  };
  isAttendanceLoaded: boolean = false;

  ngOnInit(): void {
    this.empEmail = localStorage.getItem('employeeEmail');
    if (!this.empEmail) {
      console.warn("employeeEmail not found in localStorage");
      return;
    }

    this.loadDepartments(this.empEmail);
    this.loadDesignations(this.empEmail);
    this.loadTeams(this.empEmail);
    this.getEmployeeSalary();

    // Load employee info and then attendance
    this.employeeService.getEmployeeInfo(this.empEmail).subscribe({
      next: (data) => {
        this.employeeData = data;
        this.employeeData.profileImage = 'data:image/png;base64,' + data.profileImage;

        this.punctuality = data.rating?.punctuality || 0;
        this.performance = data.rating?.performance || 0;
        this.softSkills = data.rating?.softSkills || 0;
        this.creativity = data.rating?.creativity || 0;

        this.bankName = data.bankDetail.bankName;
        this.ifcCode = data.bankDetail.ifscCode;
        this.accountNumber = data.bankDetail.accountNumber;
        this.branchName = data.bankDetail.branch;
        this.skills = this.employeeData.skills?.join(', ') || '';
        this.selectedTeams = data.teams?.map(team => ({
          id: team.id,
          name: team.name
        })) || [];

        this.teams = this.selectedTeams.map(team => team.name).join(', ');

        const matchedDept = this.departments.find(
          dept => dept.id === data.department.id
        );
        if (matchedDept) {
          this.employeeData.department = matchedDept;
        }

        const matchedDesg = this.designationsList.find(
          desg => desg.id === data.designation.id
        );
        if (matchedDesg) {
          this.employeeData.designation = matchedDesg;
        }

        console.log('Employee Info:', this.employeeData);

        // ✅ Now load attendance data
        this.getAttendanceOfEmployee(this.empEmail!);
      },
      error: (err) => {
        console.error('Error fetching employee info:', err);
      }
    });
  }

  toggleEditMode() {
    this.isEditing = !this.isEditing;
  }
  
  getEmployeeSalary(): void {
    this.salaryService.getEmployeeSalary(this.empEmail!).subscribe(
      (response) => {
        this.salary = response;  // Store the salary in the component's property
      },
      (error) => {
        console.error('Error fetching salary', error);  // Handle error if necessary
      }
    );
  }

  calendarClicked(event: MouseEvent) {
    this.mouseEvent = event;
  }

  onDateSelected(date: Date | null) {
    this.selected = date;
  }

  onCalendarHover(event: MouseEvent) {
    const target = event.target as HTMLElement;
    const cell = target.closest('.mat-calendar-body-cell') as HTMLElement;
  
    if (!cell) return;
  
    const text = cell.innerText.trim();
    if (!text) return;
  
    const activeMonth = this.selected ? this.selected.getMonth() : new Date().getMonth();
    const activeYear = this.selected ? this.selected.getFullYear() : new Date().getFullYear();
    const hoveredDate = new Date(activeYear, activeMonth, +text);
  
    const record = this.attendanceData.find(att => {
      const attDate = new Date(att.date);
      return (
        attDate.getFullYear() === hoveredDate.getFullYear() &&
        attDate.getMonth() === hoveredDate.getMonth() &&
        attDate.getDate() === hoveredDate.getDate()
      );
    });
  
    if (record && record.status !== 'HOLIDAY') {
      this.checkInTime = record.checkIn || '-';
      this.checkOutTime = record.checkOut || '-';
  
      const calendarCard = (event.currentTarget as HTMLElement).getBoundingClientRect();
  
      this.popoverPosition = {
        top: event.clientY - calendarCard.top + 10,
        left: event.clientX - calendarCard.left + 10,
      };
  
      this.showPopover = true;
    } else {
      this.showPopover = false;
    }
  }
  
  hidePopover() {
    this.showPopover = false;
  }

  keepPopoverVisible() {
    this.showPopover = true;
  }


  private loadDepartments(email: string): void {
    this.departmentService.getAllDepartments(email).subscribe((depts) => {
      this.departments = depts.map(dept => ({
        id: dept.id,
        name: dept.name,
        description: dept.description,
        employeeCount: dept.employeeCount
      }));
    });
  }

  private loadDesignations(email: string): void {
    this.designationService.getAllDesignations(email).subscribe((desgs) => {
      this.designationsList = desgs;
      console.log("designation List:", this.designationsList);
    });
  }

  private loadTeams(email: string): void {
    this.teamService.getAllTeams(email).subscribe((teams) => {
      this.teamsList = teams.map(team => ({
        id: team.id,
        name: team.name
      }));
    });
  }

  private getAttendanceOfEmployee(email: string) {
    this.attendanceService.getEmployeeAttendance(email).subscribe({
      next: (data) => {
        this.attendanceData = data;
        this.isAttendanceLoaded = true; // ✅ Show calendar now
        console.log("Attendance data:", this.attendanceData);
      },
      error: (err) => {
        console.error('Error fetching attendance', err);
      }
    });
  }


  dateClass: MatCalendarCellClassFunction<Date> = (date: Date, view: 'month' | 'year' | 'multi-year'): string => {
    if (view !== 'month') return '';
    // if (date.getDay() === 0) {
    //   return 'holiday-date';
    
    // }

    const foundRecord = this.attendanceData.find(att => {
      const attDate = new Date(att.date);
      return attDate.getFullYear() === date.getFullYear() &&
        attDate.getMonth() === date.getMonth() &&
        attDate.getDate() === date.getDate();
    });

    if (foundRecord) {
      const status = foundRecord.status;
      // this.checkInTime = foundRecord?.checkIn || 'N/A';
      // this.checkOutTime = foundRecord?.checkOut || 'N/A';
      console.log("Status:", status)
      if (status === 'PENDING') return 'pending-date';
      else if (status === 'PRESENT') return 'present-date';
      else if (status === 'CHECKEDIN') return 'checkedin-date';
      else if (status === 'HOLIDAY') return 'holiday-date';  
      else return 'absent-date';
    }
    return '';
  };

  getOverallRating(): number {
    const sum = this.punctuality + this.performance + this.softSkills + this.creativity;
    return Math.round((sum / 4) * 10) / 10;
  }

  getStarIcon(rating: number, starIndex: number): string {
    if (rating >= starIndex) {
      return 'images/adminViewProfile/star-filled.png';
    } else if (rating >= starIndex - 0.5) {
      return 'images/adminViewProfile/star-half.png';
    } else {
      return 'images/adminViewProfile/star-empty.png';
    }
  }

  openTeamSelectionDialog(): void {
    const dialogRef = this.dialog.open(TeamsDialogBoxComponent, {
      width: '300px',
      data: {
        selectedTeams: this.selectedTeams,
        teamsList: this.teamsList
      }
    });

    dialogRef.afterClosed().subscribe((result: { id: number; name: string }[] | undefined) => {
      if (result && result.length > 0) {
        this.selectedTeams = result;
        this.teams = result.map(team => team.name).join(', ');
      } else if (result && result.length === 0) {
        this.selectedTeams = [];
        this.teams = '';
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
  
      // Convert to base64 for preview
      const reader = new FileReader();
      reader.onload = () => {
        this.employeeData.profileImage = reader.result as string;
      };
      reader.readAsDataURL(file);
  
      // You can also store file in a variable for later upload
      this.selectedFile = file;
    }
  }
  

  setFullRating(skill: string, starIndex: number): void {
    switch (skill) {
      case 'punctuality':
        this.punctuality = starIndex;
        break;
      case 'performance':
        this.performance = starIndex;
        break;
      case 'softSkills':
        this.softSkills = starIndex;
        break;
      case 'creativity':
        this.creativity = starIndex;
        break;
    }
    console.log(`${skill} full rating set to: ${starIndex}`);
  }

  setHalfRating(skill: string, starIndex: number): void {
    switch (skill) {
      case 'punctuality':
        this.punctuality = starIndex - 0.5;
        break;
      case 'performance':
        this.performance = starIndex - 0.5;
        break;
      case 'softSkills':
        this.softSkills = starIndex - 0.5;
        break;
      case 'creativity':
        this.creativity = starIndex - 0.5;
        break;
    }
    console.log(`${skill} half rating set to: ${starIndex - 0.5}`);
  }

  // changePhoto() {
  //   alert('Change Profile Photo clicked!');
  // }

  saveChanges() {
    alert('Profile changes saved to DB!');
    const requestBody = {
      email: this.employeeData.email,
      adminEmail: this.empEmail,
      gender: this.employeeData.gender,
      skills: this.skills.split(',').map(skill => skill.trim()).filter(skill => skill.length > 0),
      reportingManager: this.employeeData.reportingManager,
      phoneNumber: this.employeeData.phoneNumber,
      address: this.employeeData.address,
      dob:new Date(this.employeeData.dob).toISOString(),
      joiningDate:new Date( this.employeeData.joiningDate).toISOString(),
      emergencyContact: this.employeeData.emergencyContact,
      bankDetail: {
        bankName: this.bankName,
        ifscCode: this.ifcCode,
        accountNumber: this.accountNumber,
        branch: this.branchName
      },
      rating: {
        punctuality: this.punctuality,
        performance: this.performance,
        softSkills: this.softSkills,
        creativity: this.creativity
      }
    };

    console.log("Updated Request body:",requestBody)

    this.employeeService.updateEmployee(requestBody).subscribe({
      next: (response) => {
        if (response) {
          console.log('Employee updated successfully');
        } else {
          console.log('Failed to update employee');
        }
      },
      error: (error) => {
        console.error('Error updating employee:', error);
      }
    });

    this.salaryService.updateEmployeeSalary(this.salary, this.empEmail!).subscribe({
      next: (response) => {
        if (response === true) {
          console.log('Salary updated successfully!');
        } else {
          console.warn('Failed to update salary.');
        }
      },
      error: (err) => {
        console.error('Error updating salary:', err);
      }
    });

    this.updateImage();
    this.updateTeams();
    this.updateDepartment();
    this.updateDesignation();
    this.toggleEditMode();
  }

  updateImage(): void {
    if (!this.selectedFile) {
      console.log('No new image selected, skipping update.');
      return; // Do nothing if image not changed
    }
    const formData = new FormData();
  
    // If a new image was selected, append it. Else, send empty Blob to retain old one.
    if (this.selectedFile) {
      formData.append('newImage', this.selectedFile);
    } else {
      formData.append('newImage', new Blob(), ''); // empty Blob keeps old image
    }
  
    const employeeEmailWrapper = {
      employeeEmail: this.empEmail
    };
  
    formData.append(
      'employeeEmailWrapper',
      new Blob([JSON.stringify(employeeEmailWrapper)], { type: 'application/json' })
    );
  
    this.employeeService.updateProfileImage(formData).subscribe({
      next: () => {
        console.log('Profile image updated successfully');
      },
      error: (error) => {
        console.error('Error updating profile image:', error);
      }
    });
  }

  updateTeams() {
    const employeeEmail = this.empEmail;
    const adminEmail = this.authService.getLoggedInEmail();
    const teams = this.selectedTeams.map(team => team.id); // Replace with actual team IDs

    this.teamService.updateEmployeeTeams(employeeEmail!, adminEmail!, teams).subscribe({
      next: (response) => {
        if (response) {
          console.log('Employee teams updated successfully');
        } else {
          console.log('Failed to update employee teams');
        }
      },
      error: (err) => {
        console.error('Error updating employee teams:', err);
      }
    });
  }

  updateDepartment() {
    const departmentId = this.employeeData.department.id; // Example department ID
    const employeeEmail = this.empEmail;

    this.departmentService.updateCurrentDepartment(departmentId, employeeEmail!).subscribe({
      next: (res) => {
        if (res) {
          console.log('Department updated successfully');
        } else {
          console.log('Failed to update department');
        }
      },
      error: (err) => {
        console.error('Error updating department:', err);
      }
    });
  }

  updateDesignation() {
    const designationId = this.employeeData.designation.id; // example ID
    const employeeEmail = this.empEmail;

    this.designationService.updateCurrentDesignation(designationId, employeeEmail!).subscribe({
      next: (res) => {
        if (res) {
          console.log('Designation updated successfully');
        } else {
          console.log('Update failed');
        }
      },
      error: (err) => {
        console.error('Error:', err);
      }
    });
  }
}
