import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

// Define interfaces that mimic the data contracts from the backend.
export interface Faq {
  // Adjust properties to match your backend FAQ model (e.g., id, question, answer, etc.).
  question: string;
  answer: string;
}

export interface EmployeeEmailWrapper {
  employeeEmail: string;
}

@Injectable({
  providedIn: 'root'
})
export class EmpFaqService {

   // Replace the base URL with your backend's URL or configure it in your environment settings.
   private baseUrl = 'http://localhost:5555/organization';

   constructor(private http: HttpClient) { }
 
   /**
    * Calls the backend endpoint to get all FAQs.
    * @param wrapper - Object containing the employee's email.
    * @returns an Observable of FAQ array.
    */
   getAllFaqs(wrapper: EmployeeEmailWrapper): Observable<Faq[]> {
     return this.http.post<Faq[]>(`${this.baseUrl}/get-faqs`, wrapper);
   }
}
