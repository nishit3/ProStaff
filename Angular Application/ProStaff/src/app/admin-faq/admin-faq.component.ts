import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AdminAddFaqComponent } from '../admin-add-faq/admin-add-faq.component';
import { Faq, FaqService } from '../services/admin-add-faq.service';
import { AuthServiceService } from '../services/auth-service.service';

@Component({
  selector: 'app-admin-faq',
  standalone: false,
  templateUrl: './admin-faq.component.html',
  styleUrls: ['./admin-faq.component.css'],
})
export class AdminFaqComponent implements OnInit {
  searchTerm: string = '';
  faqs: Faq[] = [];
  adminEmail = '';
  constructor(
    public dialog: MatDialog,
    private faqService: FaqService,
    private authService: AuthServiceService
  ) {}
  
  ngOnInit(): void {
    const email = this.authService.getLoggedInEmail();
    if (!email) {
      console.error('No admin email found');
      return;
    }
    this.adminEmail = email;
    this.loadFaqs();
  }

  loadFaqs(): void {
    this.faqService.getFaqsByEmail(this.adminEmail).subscribe({
      next: faqs => this.faqs = faqs,
      error: err => console.error('Error fetching FAQs:', err)
    });
  }

  get filteredFaqs(): Faq[] {
    return this.faqs.filter(f =>
      f.question.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  openDialog(faq: Faq | null = null, index: number | null = null): void {
    const dialogRef = this.dialog.open(AdminAddFaqComponent, {
      width: '500px',
      data: faq ? { question: faq.question, answer: faq.answer, index } : null
    });

    dialogRef.afterClosed().subscribe(result => {
      if (!result) return;

      if (result.index !== null && faq) {
        // ─── UPDATE ───
        const updated: Faq = {
          id: faq.id,
          question: result.question,
          answer: result.answer
        };
        this.faqService.updateFaq(this.adminEmail, updated).subscribe({
          next: () => this.loadFaqs(),
          error: err => {
            console.error('Error updating FAQ:', err);
            alert('Failed to update FAQ.');
          }
        });
      } else {
        // ─── CREATE ───
        this.faqService.addFaq(this.adminEmail, {
          question: result.question,
          answer: result.answer
        }).subscribe({
          next: () => this.loadFaqs(),
          error: err => {
            console.error('Error adding FAQ:', err);
            alert('Failed to add FAQ.');
          }
        });
      }
    });
  }

  editFaq(index: number): void {
    this.openDialog(this.faqs[index], index);
  }

  deleteFAQ(index: number): void {
    const faq = this.faqs[index];
    if (!faq) return;

    if (confirm('Are you sure you want to delete this FAQ?')) {
      this.faqService.deleteFaq(this.adminEmail, faq.id!).subscribe({
        next: () => this.loadFaqs(),
        error: err => {
          console.error('Error deleting FAQ:', err);
          alert('Failed to delete FAQ.');
        }
      });
    }
  }


  // ngOnInit(): void {
  //   // Get employee email from local storage
  //   this.adminEmail = this.authService.getLoggedInEmail();
  //   if (!this.adminEmail) return;
  //   this.loadFaqs();
  // }

  // loadFaqs(): void {
  //   this.faqService.getFaqsByEmail(this.adminEmail!).subscribe({
  //     next: (data) => (this.faqs = data),
  //     error: (err) => console.error('Error fetching FAQs:', err),
  //   });
  // }

  // // // Example FAQ data
  // // faqs = [
  // //   { question: 'What is Angular?', answer: 'Angular is a front-end framework.' },
  // //   { question: 'How to use Material UI?', answer: 'Material UI is a design system.' },
  // //   { question: 'What is TypeScript?', answer: 'TypeScript is a superset of JavaScript.' }
  // // ];

  // // Filtered FAQ list based on search input
  // get filteredFaqs() {
  //   return this.faqs.filter((faq) =>
  //     faq.question.toLowerCase().includes(this.searchTerm.toLowerCase())
  //   );
  // }

  // // Open dialog for adding or editing an FAQ
  // openDialog(faq: any = null, index: number | null = null) {
  //   const dialogRef = this.dialog.open(AdminAddFaqComponent, {
  //     width: '500px',
  //     data: faq ? { question: faq.question, answer: faq.answer, index } : null,
  //   });

  //   dialogRef.afterClosed().subscribe((result) => {
  //     if (result) {
  //       if (result.index !== undefined && result.index !== null) {
  //         // For now still update locally (until update API is integrated)
  //         this.faqs[result.index] = {
  //           ...this.faqs[result.index],
  //           question: result.question,
  //           answer: result.answer,
  //         };
  //       } else {
  //         // Call service to add FAQ to backend
  //         this.faqService
  //           .addFaq(this.adminEmail!, {
  //             question: result.question,
  //             answer: result.answer,
  //           })
  //           .subscribe({
  //             next: (res) => {
  //               // Optionally show success message here
  //               this.loadFaqs(); // Reload FAQs from backend
  //             },
  //             error: (err) => {
  //               console.error('Error adding FAQ:', err);
  //               alert('Failed to add FAQ.');
  //             },
  //           });
  //       }
  //     }
  //   });
  // }

  // // Open the dialog for editing an FAQ
  // editFaq(index: number) {
  //   this.openDialog(this.faqs[index], index);
  // }

  // // Delete an FAQ from the list
  // deleteFAQ(index: number): void {
  //   const faq = this.faqs[index];
  //   if (confirm('Are you sure you want to delete this FAQ?')) {
  //     this.faqs.splice(index, 1);
  //   }
  // }
}