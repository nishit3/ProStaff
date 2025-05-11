import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthServiceService } from '../services/auth-service.service';

@Component({
  selector: 'app-forgot-password',
  standalone: false,
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css'
})
export class ForgotPasswordComponent {
  constructor(private router: Router, private authService: AuthServiceService) { }

  showAlert: boolean = false;
  alertMessage: string = '';
  isError: boolean = false;

  saveForgotPass(forgotPass: NgForm) {
    const email = forgotPass.value.email;

    this.authService.sendResetPasswordMail(email).subscribe({
      next: (response: string) => {
        console.log("inside reset password");

        const normalizedResponse = response.trim().toLowerCase();

        if (normalizedResponse.includes("password reset link sent")) {
          this.alertMessage = response;
          this.isError = false;
        } else {
          this.alertMessage = response;
          this.isError = true;
        }

        this.showResetSuccessAlert();
      },
      error: (err) => {
        console.error('API error:', err);
        this.alertMessage = "Something went wrong. Please try again.";
        this.isError = true;
        this.showResetSuccessAlert();
      }
    });
  }

  showResetSuccessAlert() {
    this.showAlert = true;
    console.log("alert method");

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

    // Hide alert
    setTimeout(() => {
      this.showAlert = false;
    }, 2500);
  }
}
