import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class SellerGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(): boolean {
    const user = localStorage.getItem('user');
    const parsedUser = user ? JSON.parse(user) : null;

    if (parsedUser && parsedUser.role === 'SELLER') {
      return true;
    }

    this.router.navigate(['/']); // Yetkisizse ana sayfaya yönlendir
    return false;
  }
}
