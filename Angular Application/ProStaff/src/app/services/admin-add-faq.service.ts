import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Faq {
  id?: number; // optional if you implement backend
  question: string;
  answer: string;
}

interface AdminEmailWrapper {
  adminEmail: string;
}

interface EmployeeEmailWrapper {
  employeeEmail: string;
}

@Injectable({
  providedIn: 'root'
})
export class FaqService {
  private apiUrl = 'http://localhost:5555/organization'; // Adjust to your backend URL

  constructor(private http: HttpClient) {}

  // Fetch all FAQs
  getFaqsByEmail(employeeEmail: string): Observable<Faq[]> {
    const body: EmployeeEmailWrapper = { employeeEmail };
    return this.http.post<Faq[]>(`${this.apiUrl}/get-faqs`, body);
  }

  // Add a new FAQ
  addFaq(adminEmail: string, faq: { question: string, answer: string }): Observable<any> {
    const wrapper = { adminEmail };
    const newFAQ = { question: faq.question, answer: faq.answer };
    
    const formData = new FormData();
    formData.append(
      'wrapper',
      new Blob([JSON.stringify(wrapper)], { type: 'application/json' })
    );
    formData.append(
      'newFAQ',
      new Blob([JSON.stringify(newFAQ)], { type: 'application/json' })
    );

    return this.http.post<boolean>(`${this.apiUrl}/add-faq`, formData);
  }

    // return this.http.post<any>(`${this.apiUrl}/add-faq`, {
    //   adminEmail,
    //   question: faq.question,
    //   answer: faq.answer
    // });
  // }
  

  updateFaq(adminEmail: string, faq: Faq): Observable<boolean> {
    const wrapper = { adminEmail };

    const formData = new FormData();
    formData.append(
      'wrapper',
      new Blob([JSON.stringify(wrapper)], { type: 'application/json' })
    );
    formData.append(
      'updatedFAQ',
      new Blob([JSON.stringify(faq)], { type: 'application/json' })
    );

    return this.http.put<boolean>(`${this.apiUrl}/update-faq`, formData);
  }

  /** DELETE /organization/remove-faq/{id} with body */
  deleteFaq(adminEmail: string, id: number): Observable<boolean> {
    const wrapper = { adminEmail };
    return this.http.delete<boolean>(
      `${this.apiUrl}/remove-faq/${id}`,
      { body: wrapper }
    );
  }

  // // Update existing FAQ
  // updateFaq(id: number, faq: Faq): Observable<Faq> {
  //   return this.http.put<Faq>(`${this.apiUrl}/${id}`, faq);
  // }

  // // Delete an FAQ
  // deleteFaq(id: number): Observable<void> {
  //   return this.http.delete<void>(`${this.apiUrl}/${id}`);
  // }
}
