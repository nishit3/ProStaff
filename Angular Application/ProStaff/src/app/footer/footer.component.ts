import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-footer',
  standalone: false,
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent implements OnInit{
  isAdminHomepage: boolean = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    console.log("admin :", this.isAdminHomepage);
    this.router.events.subscribe(() => {
      this.isAdminHomepage = this.router.url.includes('adminHomepage') || this.router.url.includes('adminEmpManagement') || this.router.url.includes('adminViewProfile')
      || this.router.url.includes('adminAddEmployee') || this.router.url.includes('adminNotifications') || this.router.url.includes('adminFaq') || this.router.url.includes('adminUpcomingEvents')
      || this.router.url.includes('adminTeams') || this.router.url.includes('adminDepartment') || this.router.url.includes('adminLeaveManagement') || this.router.url.includes('adminContactUs') 
      || this.router.url.includes('adminDesignation') || this.router.url.includes('adminHome') || this.router.url.includes('adminSalary'); 
    });
  }
}
