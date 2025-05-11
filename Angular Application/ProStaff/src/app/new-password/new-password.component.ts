import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthServiceService } from '../services/auth-service.service';
@Component({
  selector: 'app-new-password',
  standalone: false,
  templateUrl: './new-password.component.html',
  styleUrl: './new-password.component.css'
})
export class NewPasswordComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthServiceService
  ) { }
  token: string = '';
  showNewPassword: boolean = false;
  showConfPassword: boolean = false;
  passwordMismatch: boolean = false;
  showAlert = false;
  alertMessage = '';
  isError = false;

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
    });
  }

  toggleNewPasswordVisibility() {
    this.showNewPassword = !this.showNewPassword;
  }

  toggleConfPasswordVisibility() {
    this.showConfPassword = !this.showConfPassword;
  }


  changePassword(form: NgForm) {
    if (form.value.newPassword !== form.value.confPassword) {
      console.log("Passwords do not match.");
      this.passwordMismatch = true;
      return; // ⛔ Prevent API call if mismatch
    }
  
    this.passwordMismatch = false;
  
    this.authService.requestResetPassword(this.token, form.value.newPassword).subscribe({
      next: (response: boolean) => {
        if (response) {
          this.alertMessage = "Password has been successfully reset!";
          this.isError = false;
          this.showResetSuccessAlert(true); // ✅ Pass flag to trigger navigation
        } else {
          this.alertMessage = "Reset failed. Please try again.";
          this.isError = true;
          this.showResetSuccessAlert(false);
        }
      },
      error: (err) => {
        console.error("Request failed:", err);
        this.alertMessage = "Something went wrong. Please try again.";
        this.isError = true;
        this.showResetSuccessAlert(false);
      }
    });
  }
  

  showResetSuccessAlert(shouldNavigate: boolean) {
    this.showAlert = true;
  
    // Scroll to alert container
    document.querySelector('.welcome-container')?.scrollIntoView({ behavior: 'smooth' });
  
    // Add "show" class
    setTimeout(() => {
      const alertBox = document.querySelector('.global-alert');
      if (alertBox) {
        alertBox.classList.add('show');
      }
    }, 500);
  
    // Add exit animation
    setTimeout(() => {
      const alertBox = document.querySelector('.global-alert');
      if (alertBox) {
        alertBox.classList.add('exit-animation');
      }
    }, 2000);
  
    // Hide alert + navigate
    setTimeout(() => {
      this.showAlert = false;
  
      if (shouldNavigate) {
        this.router.navigate(["/"]); // ✅ Navigate only after alert is hidden
      }
    }, 2500);
  }
}
