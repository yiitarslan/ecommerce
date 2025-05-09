import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service'; // kendi servis yolunu yaz
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean | Observable<boolean> {
    const user = this.authService.getCurrentUser(); // localStorage'dan oku vs.
    
    if (user && user.role === 'ADMIN') {
      return true;
    } else {
      this.router.navigate(['/']); // veya anasayfaya
      return false;
    }
  }
}
