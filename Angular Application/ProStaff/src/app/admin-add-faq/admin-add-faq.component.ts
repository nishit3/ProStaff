import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-admin-add-faq',
  standalone: false,
  templateUrl: './admin-add-faq.component.html',
  styleUrls: ['./admin-add-faq.component.css']
})
export class AdminAddFaqComponent {
  question: string = '';
  answer: string = '';
  index: number | null = null; // Track if editing

  constructor(
    public dialogRef: MatDialogRef<AdminAddFaqComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    if (data) {
      this.question = data.question || '';  // Set question if exists
      this.answer = data.answer || '';      // Set answer if exists
      this.index = data.index ?? null;      // Store index for update
    }
  }

  // Handle form submission (Add or Edit)
  submitFaq(): void {
    if (!this.question.trim() || !this.answer.trim()) {
      alert('Please fill all fields before submitting.');
      return;
    }

    const faq = {
      question: this.question,
      answer: this.answer,
      index: this.index // Pass index back to parent
    };

    // Send data back to the parent component
    this.dialogRef.close(faq);
  }

  // Handle cancel button
  closeDialog(): void {
    this.dialogRef.close(null);
  }
}
