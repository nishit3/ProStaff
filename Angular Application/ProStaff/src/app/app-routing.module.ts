import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { NewPasswordComponent } from './new-password/new-password.component';
import { EmpHomepageComponent } from './emp-homepage/emp-homepage.component';
import { EmpProfileComponent } from './emp-profile/emp-profile.component';
import { AdminHomepageComponent } from './admin-homepage/admin-homepage.component';
import { AdminEmployeeManagementComponent } from './admin-employee-management/admin-employee-management.component';
import { AdminViewProfileComponent } from './admin-view-profile/admin-view-profile.component';
import { AdminAddEmpComponent } from './admin-add-emp/admin-add-emp.component';
import { AdminNotificationComponent } from './admin-notification/admin-notification.component';
import { AdminFaqComponent } from './admin-faq/admin-faq.component';
import { AdminUpcomingEventComponent } from './admin-upcoming-event/admin-upcoming-event.component';
import { AdminTeamsComponent } from './admin-teams/admin-teams.component';
import { AdminDepartmentComponent } from './admin-department/admin-department.component';
import { AdminLeaveManagementComponent } from './admin-leave-management/admin-leave-management.component';
import { EmpFaqComponent } from './emp-faq/emp-faq.component';
import { AdminDesignationComponent } from './admin-designation/admin-designation.component';
import { AdminLayoutComponent } from './admin-layout/admin-layout.component';
import { SignupComponent } from './signup/signup.component';
import { AuthGuard } from './authguard/auth.guard';
import { AdminAddAdminComponent } from './admin-add-admin/admin-add-admin.component';
import AdminFundsComponent from './admin-salary/admin-salary.component';
import AdminSalaryComponent from './admin-salary/admin-salary.component';

const routes: Routes = [
  { path: "", component: LoginComponent },
  { path: "forgotPassword", component: ForgotPasswordComponent },
  { path: "Zp4Lq6dYtXv0RAfMnJw82EoKCyHgBb9TuVNsx3QZLiPmWkUDG7rFahceoMTlXq1SvnbJy", component: NewPasswordComponent},  //add 'reset-password' as route and the token 
  { path: "empHome", component: EmpHomepageComponent },
  { path: "empProfile", component: EmpProfileComponent },
  { path: "empFAQ", component: EmpFaqComponent },
  { path: "adminHomepage", component: AdminHomepageComponent,canActivate: [AuthGuard] },
  { path: "adminEmpManagement", component: AdminEmployeeManagementComponent,canActivate: [AuthGuard] },
  { path: "adminViewProfile", component: AdminViewProfileComponent },
  { path: "adminAddEmployee", component: AdminAddEmpComponent ,canActivate: [AuthGuard]},
  { path: "adminNotifications", component: AdminNotificationComponent,canActivate: [AuthGuard] },
  { path: "adminFaq", component: AdminFaqComponent },
  { path: "adminUpcomingEvents", component: AdminUpcomingEventComponent },
  { path: "adminTeams", component: AdminTeamsComponent,canActivate: [AuthGuard] },
  { path: "adminDepartment", component: AdminDepartmentComponent,canActivate: [AuthGuard] },
  { path: "adminLeaveManagement", component: AdminLeaveManagementComponent,canActivate: [AuthGuard] },
  { path: "adminDesignation", component: AdminDesignationComponent },
  { path: "adminSalary", component: AdminSalaryComponent },
  { path:"adminHome",component:AdminLayoutComponent},
  { path:"registerOrganization",component:SignupComponent},
  { path: "**", redirectTo: "/login" }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
