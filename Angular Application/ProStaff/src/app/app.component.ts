import { AfterViewInit, Component, Input, OnInit } from '@angular/core';
import { NavigationEnd, Router, RouterEvent } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.css'
})
export class AppComponent implements AfterViewInit {
  constructor(private router: Router) {}
  title = 'ProStaff';
  rating: number = 2;
  get stars() {
    return Array(Math.floor(this.rating)).fill(0);
  }

  ngAfterViewInit() {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        document.documentElement.scrollTop = 0; // Scroll to top
        document.body.scrollTop = 0; // For Safari
      }
    });
  }
}
