import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { SalaryService } from '../services/salary.service';
import { AuthServiceService } from '../services/auth-service.service';
declare var Razorpay: any;
@Component({
  selector: 'app-admin-add-funds',
  standalone: false,
  templateUrl: './admin-add-funds.component.html',
  styleUrl: './admin-add-funds.component.css'
})
export class AdminAddFundsComponent implements OnInit {
  funds: number = 0;
  index: number | null = null; // Track if editing
  adminEmail: string | null = null;
  constructor(
    private salaryService: SalaryService,
    private authService: AuthServiceService,
    public dialogRef: MatDialogRef<AdminAddFundsComponent>,
  ) {
  }

  ngOnInit(): void {
    this.adminEmail = this.authService.getLoggedInEmail();
    if (!this.adminEmail) return
  }

  // Handle form submission (Add or Edit)
  submitFunds(): void {
    const amount = this.funds;

    if (!amount || isNaN(amount) || amount <= 0) {
      alert('Please enter a valid number greater than 0.');
      return;
    }

    if (!this.adminEmail) {
      alert('Admin email not found.');
      return;
    }

    // Dummy paymentDetails for testing
    const dummyPaymentDetails = {
      razorpay_order_id: 'test_order_123',
      razorpay_payment_id: 'test_payment_123',
      razorpay_signature: 'test_signature_abc'
    };

    // Call verifyPayment with dummy data
    // this.verifyPayment(dummyPaymentDetails, this.adminEmail, amount);
    this.addFundsAndVerify(amount, this.adminEmail)
  }

  verifyPayment(paymentDetails: any, adminEmail: string, amount: number) {
    const payload = {
      razorpay_order_id: paymentDetails.razorpay_order_id,
      razorpay_payment_id: paymentDetails.razorpay_payment_id,
      razorpay_signature: paymentDetails.razorpay_signature,
      adminEmail: adminEmail,
      amount: amount
    };

    this.salaryService.verifyFundPayment(payload).subscribe({
      next: (res) => {
        if (res) {
          console.log('Payment verified successfully!');
          alert('Funds added and verified!');
          this.dialogRef.close({ success: true });  // ✅ Close with success flag
        } else {
          alert('Payment verification failed.');
          this.dialogRef.close({ success: false });
        }
      },
      error: (err) => {
        console.error('Verification error:', err);
        alert('Error verifying payment. Please try again.');
        this.dialogRef.close({ success: false });
      }
    });
  }




  addFundsAndVerify(amount: number, adminEmail: string) {
    // Step 1: Create Razorpay Order
    this.salaryService.addFunds(amount).subscribe({
      next: (res) => {
        const orderId = res.orderId; // Get order ID from backend
        console.log('Order ID:', orderId);

        // Step 2: Configure Razorpay Payment Options
        const options: any = {
          key: 'rzp_test_shfEe2LrMp8nj7', // Replace with your Razorpay key
          amount: amount * 100, // Razorpay accepts amount in paisa
          currency: 'INR',
          name: 'ProStaff',
          description: 'Fund Wallet',
          order_id: orderId,
          handler: (response: any) => {
            // Step 4: Payment Success — Verify
            this.verifyPayment(response, adminEmail, amount);
          },
          prefill: {
            email: adminEmail
          },
          theme: {
            color: '#3399cc'
          }
        };

        // Step 3: Open Razorpay Payment Modal
        const razorpay = new Razorpay(options);
        razorpay.open();
      },
      error: (err) => {
        console.error('Error creating Razorpay order:', err);
        alert('Failed to initiate payment.');
      }
    });
  }

  // Handle cancel button
  closeDialog(): void {
    this.dialogRef.close(null);
  }
}
