import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthServiceService } from '../services/auth-service.service';


@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthServiceService, private router: Router) {}

  canActivate(): boolean {
    const token = this.authService.getToken(); // however you get JWT
    if (!token) {
      this.router.navigate(['/'], { replaceUrl: true });
      return false;
    }
    return true;
  }
}
