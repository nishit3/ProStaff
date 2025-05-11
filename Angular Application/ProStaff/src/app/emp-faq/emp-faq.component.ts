import { Component, OnInit } from '@angular/core';
import { EmpFaqService, EmployeeEmailWrapper, Faq } from '../services/emp-faq.service';
import { AuthServiceService } from '../services/auth-service.service';

@Component({
  selector: 'app-emp-faq',
  standalone: false,
  templateUrl: './emp-faq.component.html',
  styleUrl: './emp-faq.component.css'
})
export class EmpFaqComponent implements OnInit {

  searchTerm: string = '';
  faqs: Faq[] = [];

  constructor(private empFaqService: EmpFaqService, private authService: AuthServiceService) {}

  ngOnInit(): void {
    // Provide the employee email as required by the backend. This can be dynamically set.
    const emailWrapper: EmployeeEmailWrapper = { employeeEmail: this.authService.getLoggedInEmail()! };

    this.empFaqService.getAllFaqs(emailWrapper).subscribe({
      next: (data: Faq[]) => {
        this.faqs = data;
      },
      error: (error) => {
        console.error('Error fetching FAQs', error);
      }
    });
  }

  /**
   * Returns the FAQs filtered by the search term.
   */
  get filteredFaqs(): Faq[] {
    return this.faqs.filter(faq =>
      faq.question.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }


  // searchTerm:string=''
  // faqs = [
  //   { question: 'Holi', answer: 'Colour Celebration' },
  //   { question: 'Diwali', answer: 'Firecracker Celebration' },
  //   { question: 'Kite Flying', answer: 'Kite Festival' }
  // ];
  // get filteredFaqs() {
  //   return this.faqs.filter(faq =>
  //     faq.question.toLowerCase().includes(this.searchTerm.toLowerCase())
  //   );
  // }
}
