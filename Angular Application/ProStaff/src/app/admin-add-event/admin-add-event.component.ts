import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-admin-add-event',
  standalone: false,
  templateUrl: './admin-add-event.component.html',
  styleUrls: ['./admin-add-event.component.css']
})
export class AdminAddEventComponent {
  eventDate: string | null = null;
  eventName: string = '';
  eventDescription: string = '';
  index: number | null = null;

  /** bound to <input type="date">, e.g. "2025-05-01" */
  eventDateISO = '';

  /** dd‑MM‑yyyy, for backend/display, e.g. "01-05-2025" */
  eventDateFormatted = '';

  constructor(
    public dialogRef: MatDialogRef<AdminAddEventComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    if (data) {
      // data.date comes in as "dd-MM-yyyy"
      const [d, m, y] = data.date.split('-'); 
      this.eventDateFormatted = data.date;
      this.eventDateISO = `${y}-${m}-${d}`;

      this.eventName = data.name;
      this.eventDescription = data.description;
      this.index = data.index;

      // this.eventDate = data.date || null;
      // this.eventName = data.name || '';
      // this.eventDescription = data.description || '';
      // this.index = data.index ?? null;
    }
  }

   /** whenever the date‐picker changes, recalc the formatted string */
   onDateChange(iso: string) {
    this.eventDateISO = iso;
    if (!iso) {
      this.eventDateFormatted = '';
      return;
    }
    // const [y, m, d] = iso.split('-');
    // this.eventDateFormatted = `${d}-${m}-${y}`;

    // Prevent timezone offset error by parsing manually
    const parts = iso.split('-'); // [yyyy, MM, dd]
    const year = +parts[0];
    const month = +parts[1] - 1; // JS month is 0-based
    const day = +parts[2] + 1 ;

    const localDate = new Date(year, month, day); // Local time date

    const dd = String(localDate.getDate()).padStart(2, '0');
    const mm = String(localDate.getMonth() + 1).padStart(2, '0');
    const yyyy = localDate.getFullYear();

    this.eventDateFormatted = `${dd}-${mm}-${yyyy}`;
  }

  // Handle form submission (Add or Edit)
  submitEvent(): void {
    if (!this.eventDateISO || !this.eventName.trim() || !this.eventDescription.trim()) {
      alert('Please fill all fields before submitting.');
      return;
    }

    // Convert "yyyy-MM-dd" → "dd-MM-yyyy"
    // const [year, month, day] = this.eventDate.split('-');
    // const formattedDate = `${day}-${month}-${year}`;

  //   // Normalize to "yyyy-MM-dd"
  // const d = new Date(this.eventDate);
  // const isoDate = isNaN(d.getTime())
  //   ? this.eventDate   // fallback if parsing failed
  //   : d.toISOString().split('T')[0];

    const event = {
      date: this.eventDateFormatted, // Use the formatted date
      // date: isoDate, // Use the ISO formatted date
      name: this.eventName,
      description: this.eventDescription,
      index: this.index // Pass index back to parent if editing
    };

    // Send data back to the parent component
    this.dialogRef.close(event);
  }

  // Handle cancel button
  closeDialog(): void {
    this.dialogRef.close(null);
  }
}
