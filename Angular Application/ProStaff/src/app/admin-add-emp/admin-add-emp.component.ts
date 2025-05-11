import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { EmployeeService } from '../services/employee.service';
import { AuthServiceService } from '../services/auth-service.service';
import { DesignationService } from '../services/designation.service';
import { TeamsService } from '../services/teams.service';
import { DepartmentServiceService } from '../services/department-service.service';
import { MatDialog } from '@angular/material/dialog';
import { TeamsDialogBoxComponent } from '../teams-dialog-box/teams-dialog-box.component';

@Component({
  selector: 'app-admin-add-emp',
  standalone: false,
  templateUrl: './admin-add-emp.component.html',
  styleUrl: './admin-add-emp.component.css'
})
export class AdminAddEmpComponent implements OnInit {
  adminEmail: string | null = '';
  showFileError: boolean = false;
  salary = ""; 
  fullName = '';
  contactNumber = '';
  emergencyContactNumber = '';
  email = '';
  gender = '';
  skills = '';
  dateOfBirth = '';
  joiningDate = '';
  address = '';
  projectManager = '';
  bankName = '';
  ifcCode = '';
  accountNumber = '';
  branchName = '';
  fileName: string | null = null;
  selectedFile: File | null = null;

  departments: { id: number; name: string }[] = [];
  teamsList: { id: number; name: string }[] = [];
  designationsList: { id: number; name: string }[] = [];

  selectedDepartmentId: number | null = null;
  selectedDesignationId: number | null = null;
  selectedTeams: { id: number; name: string }[] = [];
  teams = '';

  constructor(
    private employeeService: EmployeeService,
    private authService: AuthServiceService,
    private designationService: DesignationService,
    private departmentService: DepartmentServiceService,
    private teamService: TeamsService,
    public dialog: MatDialog
  ) { }

  ngOnInit() {
    this.adminEmail = this.authService.getLoggedInEmail();
    if (!this.adminEmail) return;

    this.designationService.getAllDesignations(this.adminEmail).subscribe((desgs) => {
      this.designationsList = desgs.map(desg => ({ id: desg.id, name: desg.name }));
      console.log("Designation list:", this.designationsList);
    });

    this.teamService.getAllTeams(this.adminEmail).subscribe((teams) => {
      this.teamsList = teams.map(team => ({ id: team.id, name: team.name }));
    });

    this.departmentService.getAllDepartments(this.adminEmail).subscribe((depts) => {
      this.departments = depts.map(dept => ({ id: dept.id, name: dept.name }));
    });
  }
  getEmployeesList() {
    if (!this.adminEmail) return;
    this.employeeService.getAllOrganizationEmployees(this.adminEmail).subscribe(
      (employees) => {
        console.log('Employees:', employees);
        this.authService.updateTotalEmp(employees.length);
      },
      (error) => {
        console.error('Failed to fetch employees:', error);
      }
    );

  }
  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      this.fileName = file.name;
    }
  }

  cancelUpload(): void {
    this.selectedFile = null;
    this.fileName = '';
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
  }

  submitForm(form: NgForm) {

    if (form.invalid) {
      alert('Please fill in all required fields.');
      return;
    }

    if (!this.validatePhone(this.contactNumber)) {
      alert('Phone must be 10 digits.');
      return;
    }

    if (!this.validateEmail(this.email)) {
      alert('Invalid email format.');
      return;
    }

    if (!this.selectedDepartmentId || !this.selectedDesignationId || this.selectedTeams.length === 0) {
      alert('Please select department, designation, and at least one team.');
      return;
    }

    const employeeData = {
      email: this.email,
      fullName: this.fullName,
      adminEmail: this.adminEmail,
      gender: this.gender,
      salary: this.salary,
      skills: this.skills.split(',').map(skill => skill.trim()),
      reportingManager: this.projectManager,
      phoneNumber: this.contactNumber,
      dob: new Date(this.dateOfBirth).toISOString(),
      joiningDate: new Date(this.joiningDate).toISOString(),
      emergencyContact: this.emergencyContactNumber,
      bankDetail: {
        bankName: this.bankName,
        ifscCode: this.ifcCode,
        accountNumber: this.accountNumber,
        branch: this.branchName
      },
      department: this.selectedDepartmentId,
      designation: this.selectedDesignationId,
      teams: this.selectedTeams.map(team => team.id),
      address: this.address
    };

    console.log("Employee Details:", employeeData);

    const formData = new FormData();
    formData.append('employeeRegistrationDetail', new Blob([JSON.stringify(employeeData)], { type: 'application/json' }));

    if (this.selectedFile) {
      formData.append('profileImage', this.selectedFile);
    }

    this.employeeService.addEmployee(formData).subscribe({
      next: (res) => {
        if (res) {
          alert('Employee added successfully!');
          this.getEmployeesList();
          this.resetForm();
          form.resetForm();
        } else {
          alert('Failed to add employee.');
        }
      },
      error: (err) => {
        console.error(err);
        alert('API error occurred!');
      }
    });
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

  validatePhone(phone: string): boolean {
    return /^[0-9]{10}$/.test(phone);
  }

  validateEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.toLowerCase());
  }

  resetForm() {
    this.fullName = '';
    this.contactNumber = '';
    this.emergencyContactNumber = '';
    this.email = '';
    this.gender = '';
    this.skills = '';
    this.dateOfBirth = '';
    this.joiningDate = '';
    this.address = '';
    this.projectManager = '';
    this.bankName = '';
    this.ifcCode = '';
    this.accountNumber = '';
    this.branchName = '';
    this.fileName = null;
    this.selectedFile = null;
    this.selectedTeams = [];
    this.teams = '';
    this.selectedDepartmentId = null;
    this.selectedDesignationId = null;

    console.log('Form has been reset.');
  }
}
