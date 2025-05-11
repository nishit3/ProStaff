import { Component, OnInit } from '@angular/core';
import { LeaveRequest, LeaveRequestService } from '../services/leave-request.service';
import { AuthServiceService } from '../services/auth-service.service';
import { GrantLeaveComponent } from '../grant-leave/grant-leave.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-admin-leave-management',
  standalone: false,
  templateUrl: './admin-leave-management.component.html',
  styleUrl: './admin-leave-management.component.css'
})
export class AdminLeaveManagementComponent implements OnInit {
  searchTerm: string = '';
  leaves: LeaveRequest[] = [];
  adminEmail: string | null = ''
  constructor(private leaveService: LeaveRequestService, private authService: AuthServiceService,private dialog:MatDialog) { }

  ngOnInit(): void {
    this.adminEmail = this.authService.getLoggedInEmail();
    if (!this.adminEmail) return;
    this.getLeaves();
  }

  getLeaves() {
    if (!this.adminEmail) return;
    this.leaveService.getAllLeaveRequests(this.adminEmail).subscribe((leaves) => {
      console.log("leaves from api:", leaves)
      this.leaves = leaves
    });
  }

  get filteredleaves() {
    return this.leaves.filter(leave =>
      leave.reason.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

 

  rejectLeave(id: number): void { // Replace with actual value

    this.leaveService.denyLeaveRequest(id, this.adminEmail!).subscribe({
      next: (response) => {
        if (response) {
          console.log('Leave denied successfully!');
          this.getLeaves()
        }
      },
      error: (err) => {
        console.error('Error while denying leave:', err);
      }
    });
  }

  openGrantLeaveDialog(id: number,email:string) {
    const dialogRef = this.dialog.open(GrantLeaveComponent, {
      width: '500px',
      data: { leaveId: id ,empMail : email}
    });
  
    dialogRef.afterClosed().subscribe(result => {
      // This will be called when the dialog is closed
      this.getLeaves();
    });
  }
  
  
}
