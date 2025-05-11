import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { BehaviorSubject, catchError, Observable, Subject, throwError } from 'rxjs';

// Add in a typings.d.ts or inside the component
declare var Razorpay: any;
interface VerifyOrgRequest {
  adminEmail: string;
  organizationName: string;
}
@Injectable({
  providedIn: 'root'
})

export class AuthServiceService {

  private baseUrl = 'http://localhost:5555';

  constructor(private http: HttpClient, private router: Router) { }
  getToken(): string | null {
    return localStorage.getItem('jwt');
  }

  getLoggedInEmail(): string | null {
    return localStorage.getItem('email');
  }

  getUserRole(): string | null {
    return localStorage.getItem('role');
  }

  private totalEmpSubject = new BehaviorSubject<number>(parseInt(localStorage.getItem('totalEmp') || '0'));

  getTotalEmpObservable(): Observable<number> {
    return this.totalEmpSubject.asObservable();
  }
  
  addAdmin(data: {
    adminEmail: string;
    newAdminEmail: string;
    newAdminFullName: string;
  }): Observable<boolean> {
    return this.http.post<boolean>(`${this.baseUrl}/auth/add-admin`, data);
  }

  updateTotalEmp(count: number) {
    localStorage.setItem('totalEmp', count.toString());
    this.totalEmpSubject.next(count);
  }

  logout(): void {
    localStorage.clear(); // Clear all stored session data
    this.router.navigate(['/'], { replaceUrl: true }); // Reset navigation history
  }

  getUserFullName(adminEmail: string): Observable<string> {
    const body = { "employeeEmail": adminEmail };
    return this.http.post<string>(`${this.baseUrl}/auth/get-user-fullname`, body, {
      responseType: 'text' as 'json'
    });
  }

  verifyNewOrganizationDetails(payload: VerifyOrgRequest): Observable<boolean> {
    const url = `${this.baseUrl}/auth/verify-new-organization-details`;
    return this.http.post<boolean>(url, payload);
  }

  // Login API
  login(email: string, password: string): Observable<{ jwt: string; role: string }> {
    const url = `${this.baseUrl}/auth/login`;
    const body = { email, password };
    // console.log('jwt:',this.jwt)
    return this.http.post<{ jwt: string; role: string }>(url, body).pipe(
      catchError(this.handleError)
    );
  }

  sendResetPasswordMail(email: string): Observable<string> {
    const body = { employeeEmail: email };
    return this.http.post(`${this.baseUrl}/auth/send-reset-password-mail`, body, {
      responseType: 'text'  // 👈 Important: Expect plain text instead of JSON
    });
  }

  requestResetPassword(token: string, newPassword: string): Observable<boolean> {
    const body = {
      token: token,
      newPassword: newPassword
    };
    return this.http.post<boolean>(`${this.baseUrl}/auth/request-reset-password`, body);
  }

  payWithRazorpay(signupForm: any, selectedFile: File): Observable<boolean> {
    const resultSubject = new Subject<boolean>();

    this.http.post<any>(`${this.baseUrl}/auth/create-order`, {}).subscribe(order => {
      const options: any = {
        key: 'rzp_test_shfEe2LrMp8nj7',
        amount: order.amount,
        currency: 'INR',
        name: 'ProStaff',
        description: 'Register Organization',
        order_id: order.orderId,
        handler: (response: any) => {
          const paymentDetails = {
            razorpay_order_id: response.razorpay_order_id,
            razorpay_payment_id: response.razorpay_payment_id,
            razorpay_signature: response.razorpay_signature,
            organizationName: signupForm.value.organizationName,
            adminFullName: signupForm.value.adminFullName,
            adminEmail: signupForm.value.adminEmail
          };

          this.verifyPayment(paymentDetails, selectedFile).subscribe({
            next: (result) => {
              resultSubject.next(true);
              resultSubject.complete();
            },
            error: (err) => {
              resultSubject.next(false);
              resultSubject.complete();
            }
          });
        },
        prefill: {
          name: signupForm.value.adminFullName,
          email: signupForm.value.adminEmail,
          contact: signupForm.value.adminContact,
        },
        theme: {
          color: '#3399cc',
        },
      };

      const rzp = new Razorpay(options);
      rzp.open();

      rzp.on('payment.failed', function (response: any) {
        resultSubject.next(false);
        resultSubject.complete();
      });
    });

    return resultSubject.asObservable();
  }


  // Verify Payment API
  verifyPayment(
    details: {
      razorpay_order_id: string;
      razorpay_payment_id: string;
      razorpay_signature: string;
      organizationName: string;
      adminFullName: string;
      adminEmail: string;
    },
    orgImage: File
  ): Observable<boolean> {
    const url = `${this.baseUrl}/auth/verify-payment`;

    const formData = new FormData();
    const detailsBlob = new Blob([JSON.stringify(details)], { type: 'application/json' });
    formData.append('details', detailsBlob); // Appending JSON as a string
    formData.append('orgImage', orgImage); // File upload

    // ✅ No headers needed here
    return this.http.post<boolean>(url, formData).pipe(
      catchError(this.handleError)
    );
  }


  // Error Handling
  private handleError(error: HttpErrorResponse) {
    console.error('API Error:', error);
    return throwError(() => new Error('Something went wrong; please try again later.'));
  }


}
