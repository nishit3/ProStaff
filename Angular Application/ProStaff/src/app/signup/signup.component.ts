import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthServiceService } from '../services/auth-service.service';

@Component({
  selector: 'app-signup',
  standalone: false,
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  signupForm: FormGroup;
  selectedFile: File | null = null;
  selectedFilePreview: string | null = null;
  showAlert = false;
  alertMessage = '';
  isError = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthServiceService // Inject AuthService
  ) {
    this.signupForm = this.fb.group({
      organizationName: ['', [Validators.required]],
      adminFullName: ['', [Validators.required]],
      adminEmail: ['', [Validators.required, Validators.email]],
      adminContact: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]]
    });
  }

  onDigitOnly(event: any): void {
    const input = event.target;
    input.value = input.value.replace(/[^0-9]/g, '');
    // Optionally update the form control if needed:
    this.signupForm.controls['adminContact'].setValue(input.value);
  }


  // Handle File Selection
  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];

      // Preview Image
      const reader = new FileReader();
      reader.onload = () => {
        this.selectedFilePreview = reader.result as string;
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }

  // Form Submission

  navigateToLogin() {
    this.router.navigate(['/']);
  }


  // Form Submission
  // onSubmit() {
  //   this.signupForm.markAllAsTouched();

  //   if (this.signupForm.valid && this.selectedFile) {
  //     const details = {
  //       razorpay_order_id: 'sample_order_id', // Replace with actual ID
  //       razorpay_payment_id: 'sample_payment_id', // Replace with actual ID
  //       razorpay_signature: 'sample_signature', // Replace with actual Signature
  //       organizationName: this.signupForm.value.organizationName,
  //       adminFullName: this.signupForm.value.adminFullName,
  //       adminEmail: this.signupForm.value.adminEmail
  //     };

  //     // In your component.ts
  //     this.authService.verifyNewOrganizationDetails({
  //       adminEmail: this.signupForm.value.adminEmail,
  //       organizationName: this.signupForm.value.organizationName
  //     }).subscribe(isValid => {
  //       if (isValid) {
  //         console.log('Organization details are valid');
  //         // this.authService.payWithRazorpay(this.signupForm, this.selectedFile!).subscribe(isValid => {

  //         this.authService.verifyPayment(details, this.selectedFile!).subscribe(isValid => {
  //           this.router.navigate(['/'])
  //         }, (error) => { console.log("Error in payment") });
  //       } else {
  //         console.log('Invalid organization details');
  //       }
  //     },
  //       error => {
  //         this.alertMessage = error.error?.errorMessage || "Something went wrong"; // <-- extract the message here
  //         this.isError = true;
  //         this.showResetSuccessAlert();
  //       }
  //     );
  //   }
  // }

  onSubmit() {
    this.signupForm.markAllAsTouched();

    if (this.signupForm.valid && this.selectedFile) {
      const details = {
        razorpay_order_id: 'sample_order_id', // Replace with actual ID
        razorpay_payment_id: 'sample_payment_id', // Replace with actual ID
        razorpay_signature: 'sample_signature', // Replace with actual Signature
        organizationName: this.signupForm.value.organizationName,
        adminFullName: this.signupForm.value.adminFullName,
        adminEmail: this.signupForm.value.adminEmail
      };

      // In your component.ts
      this.authService.verifyNewOrganizationDetails({
        adminEmail: this.signupForm.value.adminEmail,
        organizationName: this.signupForm.value.organizationName
      }).subscribe(isValid => {
        if (isValid) {
          console.log('Organization details are valid');
          this.authService.payWithRazorpay(this.signupForm, this.selectedFile!).subscribe(isValid => {
            this.router.navigate(['/'])
          }, (error) => { console.log("Error in payment") });
        } else {
          console.log('Invalid organization details');
        }
      },
        error => {
          this.alertMessage = error.error?.errorMessage || "Something went wrong"; // <-- extract the message here
          this.isError = true;
          this.showResetSuccessAlert();
        }
      );
    }
  }

  showResetSuccessAlert() {
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
    }, 2500);
  }

}
