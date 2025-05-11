import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { NewPasswordComponent } from './new-password/new-password.component';
import { EmpHomepageComponent } from './emp-homepage/emp-homepage.component';
import { EmpNavbarComponent } from './emp-navbar/emp-navbar.component';
import { FooterComponent } from './footer/footer.component';
import { provideNativeDateAdapter } from '@angular/material/core';
// import {MatDatepickerModule} from '@angular/material/datepicker';
import { MatCardModule } from '@angular/material/card';
import { MatCalendarCellClassFunction, MatDatepickerModule } from '@angular/material/datepicker';
import { EmpProfileComponent } from './emp-profile/emp-profile.component';
import { MatNativeDateModule } from '@angular/material/core';
import { LeaveRequestFormComponent } from './leave-request-form/leave-request-form.component';
import { MatDialogModule } from '@angular/material/dialog';
import { AdminAddEventComponent } from './admin-add-event/admin-add-event.component';
import { AdminUpdateContactUsComponent } from './admin-update-contact-us/admin-update-contact-us.component';
import { AdminAddNotificationsComponent } from './admin-add-notifications/admin-add-notifications.component';
import { TeamsDialogBoxComponent } from './teams-dialog-box/teams-dialog-box.component';
import { DepartmentDialogBoxComponent } from './department-dialog-box/department-dialog-box.component';
import { DesignationDialogBoxComponent } from './designation-dialog-box/designation-dialog-box.component';
import { AdminSidebarComponent } from './admin-sidebar/admin-sidebar.component';
import { AdminNavbarComponent } from './admin-navbar/admin-navbar.component';
import { AdminHomepageComponent } from './admin-homepage/admin-homepage.component';
import { AdminEmployeeManagementComponent } from './admin-employee-management/admin-employee-management.component';
import { AdminViewProfileComponent } from './admin-view-profile/admin-view-profile.component';
import { AdminAddEmpComponent } from './admin-add-emp/admin-add-emp.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AdminNotificationComponent } from './admin-notification/admin-notification.component';
import { AdminFaqComponent } from './admin-faq/admin-faq.component';
import { AdminAddFaqComponent } from './admin-add-faq/admin-add-faq.component';
import { AdminUpcomingEventComponent } from './admin-upcoming-event/admin-upcoming-event.component';
import { AdminAddTeamComponent } from './admin-add-team/admin-add-team.component';
import { AdminTeamsComponent } from './admin-teams/admin-teams.component';
import { AdminDepartmentComponent } from './admin-department/admin-department.component';
import { AdminAddDepartmentComponent } from './admin-add-department/admin-add-department.component';
import { AdminLeaveManagementComponent } from './admin-leave-management/admin-leave-management.component';
import { AdminNotificationFilterComponent } from './admin-notification-filter/admin-notification-filter.component';
import { AdminContactUsDialogComponent } from './admin-contact-us-dialog/admin-contact-us-dialog.component';

import { NgxIntlTelInputModule } from 'ngx-intl-tel-input';
import { NotificationsComponent } from './notifications/notifications.component';
import { RestPasswordQuickDialogComponent } from './rest-password-quick-dialog/rest-password-quick-dialog.component';
import { EmpFaqComponent } from './emp-faq/emp-faq.component';
import { AdminDesignationComponent } from './admin-designation/admin-designation.component';
import { AdminAddDesignationComponent } from './admin-add-designation/admin-add-designation.component';
import { AdminLayoutComponent } from './admin-layout/admin-layout.component';
import { HTTP_INTERCEPTORS, HttpClientModule, provideHttpClient } from '@angular/common/http';
import { SignupComponent } from './signup/signup.component';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { FilterByMessagePipe } from './pipes/filter-by-message.pipe';
import { AdminHolidayComponent } from './admin-holiday/admin-holiday.component';
import { HolidayDialogBoxComponent } from './holiday-dialog-box/holiday-dialog-box.component';
import { EmpHelpDetailsComponent } from './emp-help-details/emp-help-details.component';
import { AdminAddAdminComponent } from './admin-add-admin/admin-add-admin.component';
import AdminFundsComponent from './admin-salary/admin-salary.component';
import { AdminRolloutComponent } from './admin-rollout/admin-rollout.component';
import { AdminAddFundsComponent } from './admin-add-funds/admin-add-funds.component';
import { GrantLeaveComponent } from './grant-leave/grant-leave.component';
import { AdminSalaryRemunerationComponent } from './admin-salary-remuneration/admin-salary-remuneration.component';
@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    ForgotPasswordComponent,
    NewPasswordComponent,
    EmpHomepageComponent,
    EmpNavbarComponent,
    FooterComponent,
    EmpProfileComponent,
    LeaveRequestFormComponent,
    AdminAddEventComponent,
    AdminUpdateContactUsComponent,
    AdminAddNotificationsComponent,
    TeamsDialogBoxComponent,
    DepartmentDialogBoxComponent,
    DesignationDialogBoxComponent,
    AdminSidebarComponent,
    AdminNavbarComponent,
    AdminHomepageComponent,
    AdminEmployeeManagementComponent,
    AdminViewProfileComponent,
    AdminAddEmpComponent,
    AdminNotificationComponent,
    AdminFaqComponent,
    AdminAddFaqComponent,
    AdminUpcomingEventComponent,
    AdminAddTeamComponent,
    AdminTeamsComponent,
    AdminDepartmentComponent,
    AdminAddDepartmentComponent,
    AdminLeaveManagementComponent,
    AdminNotificationFilterComponent,
    AdminContactUsDialogComponent,
    NotificationsComponent,
    RestPasswordQuickDialogComponent,
    EmpFaqComponent,
    AdminDesignationComponent,
    AdminAddDesignationComponent,
    AdminLayoutComponent,
    SignupComponent,
    FilterByMessagePipe,
    AdminHolidayComponent,
    HolidayDialogBoxComponent,
    EmpHelpDetailsComponent,
    AdminAddAdminComponent,
    AdminFundsComponent,
    AdminRolloutComponent,
    AdminAddFundsComponent,
    GrantLeaveComponent,
    AdminSalaryRemunerationComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    MatCardModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatDialogModule,
    ReactiveFormsModule,
    NgxIntlTelInputModule,
    HttpClientModule,
  ],
  providers: [provideNativeDateAdapter(), provideHttpClient(), {
    provide: HTTP_INTERCEPTORS,
    useClass: AuthInterceptor,
    multi: true
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
