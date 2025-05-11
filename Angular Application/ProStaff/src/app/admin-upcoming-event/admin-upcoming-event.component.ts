import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AdminAddEventComponent } from '../admin-add-event/admin-add-event.component';
import { AdminUpcomingEventsService, UpcomingEvent } from '../services/admin-upcoming-events.service';
import { AuthServiceService } from '../services/auth-service.service';

@Component({
  selector: 'app-admin-upcoming-event',
  standalone: false,
  templateUrl: './admin-upcoming-event.component.html',
  styleUrl: './admin-upcoming-event.component.css'
})
export class AdminUpcomingEventComponent implements OnInit {

  events: UpcomingEvent[] = [];
  searchTerm = '';
  adminEmail = '';

  constructor(public dialog: MatDialog, private eventsService: AdminUpcomingEventsService, private authService: AuthServiceService) {}

  ngOnInit(): void {
    const email = this.authService.getLoggedInEmail();
    if (!email) {
      console.error('No admin email found');
      return;
    }
    this.adminEmail = email;
    this.loadEvents();
  }

  loadEvents(): void {
    this.eventsService
      .getUpcomingEventsByEmail(this.adminEmail)
      .subscribe({
        next: evs => (this.events = evs),
        error: err => console.error('Error fetching events:', err)
      });
  }

  get filteredEvents(): UpcomingEvent[] {
    const term = this.searchTerm.toLowerCase();
  
    return this.events.filter(e => {
      // if e.title is missing, treat it as empty string
      const name = e.name || '';
      return name.toLowerCase().includes(term);
    });
  } 

  openDialog(event: UpcomingEvent | null = null, index: number | null = null): void {
    const dialogRef = this.dialog.open(AdminAddEventComponent, {
      width: '500px',
      data: event
        ? {
            date: event.date,
            name: event.name,
            description: event.description,
            index
          }
        : null
    });

    dialogRef.afterClosed().subscribe(result => {
      if (!result) {
        return;
      }

      if (result.index !== null && event) {
        // ─── UPDATE ───
        const updated: UpcomingEvent = {
          id: event.id,
          name: result.name,
          date: result.date,
          description: result.description
        };
        this.eventsService
          .updateUpcomingEvent(this.adminEmail, updated)
          .subscribe({
            next: () => this.loadEvents(),
            error: err => {
              console.error('Error updating event:', err);
              alert('Failed to update event.');
            }
          });
      } else {
        // ─── CREATE ───
        this.eventsService
          .addUpcomingEvent(this.adminEmail, {
            name: result.name,
            date: result.date,
            description: result.description
          })
          .subscribe({
            next: () => this.loadEvents(),
            error: err => {
              console.error('Error adding event:', err);
              alert('Failed to add event.');
            }
          });
      }
    });
  }

  editEvent(index: number): void {
    this.openDialog(this.events[index], index);
  }

  deleteEvent(index: number): void {
    const ev = this.events[index];
    if (!ev) {
      return;
    }
    if (confirm('Are you sure you want to delete this event?')) {
      this.eventsService
        .deleteUpcomingEvent(this.adminEmail, ev.id)
        .subscribe({
          next: () => this.loadEvents(),
          error: err => {
            console.error('Error deleting event:', err);
            alert('Failed to delete event.');
          }
        });
    }
  }

  // events = [
  //   { title: 'Holi', date: '2025-03-13', description: 'Colour Celebration' },
  //   { title: 'Diwali', date: '2025-03-23', description: 'Firecracker Celebration' },
  //   { title: 'Kite Flying',date: '2025-03-05', description: 'Kite Festival' },
  //   { title: 'Navratri', date: '2025-03-21', description: 'Playing Garba' },
  //   { title: 'Rangoli', date: '2025-03-18', description: 'Create Rangoli' },
  //   { title: 'Mahandi', date: '2025-03-11', description: 'Create Mahandi' },
  //   { title: 'Free Dress', date: '2025-03-10', description: 'Dress Code' }
  // ];

  // deleteEvent(index: number) {
  //   // Remove from array or call an API
  //   this.events.splice(index, 1);
  // }
  // get filteredEvents() {
  //     return this.events.filter(event =>
  //       event.title.toLowerCase().includes(this.searchTerm.toLowerCase())
  //     );
  //   }

  //   openDialog(event: any = null, index: number | null = null) {
  //     const dialogRef = this.dialog.open(AdminAddEventComponent, {
  //       width: '500px',
  //       data: event ? { date: event.date, title: event.title, description: event.description, index } : null
  //     });
  
  //     // Handle the dialog result when it closes
  //     dialogRef.afterClosed().subscribe(result => {
  //       if (result) {
  //         if (result.index !== undefined && result.index !== null) {
  //           // Update existing event
  //           this.events[result.index] = { date: result.date, title: result.title, description: result.description };
  //         } else {
  //           // Add new event
  //           this.events.push({ date: result.date, title: result.title, description: result.description });
  //         }
  //       }
  //     });
  //   }
  
  //   // Open the dialog for editing an event
  //   editEvent(index: number) {
  //     this.openDialog(this.events[index], index);
  //   }
}
