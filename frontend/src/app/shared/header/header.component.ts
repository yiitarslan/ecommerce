// src/app/features/header/header.component.ts
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { AuthService } from '../../core/services/auth.service';  // ← ekleyin

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  standalone: false
})
export class HeaderComponent implements OnInit {
  isLoggedIn = false;
  userEmail = '';
  isDropdownOpen = false;
  searchText = '';
  cartItemCount = 0;

  constructor(
    private router: Router,
    private cartService: CartService,
    private authService: AuthService      // ← ekleyin
  ) {}

  ngOnInit(): void {
    const user = localStorage.getItem('user');
    if (user) {
      const parsed = JSON.parse(user);
      this.isLoggedIn = true;
      this.userEmail = parsed.email;
    }

    this.cartService.cartItemCount$.subscribe(count => {
      this.cartItemCount = count;
    });
  }

  get displayName(): string {
    if (!this.userEmail) return 'Hesap';
    const namePart = this.userEmail.split('@')[0];
    return namePart.length > 6 ? namePart.slice(0, 6) + '...' : namePart;
  }

  showDropdown() {
    this.isDropdownOpen = true;
  }

  hideDropdown() {
    this.isDropdownOpen = false;
  }

  logout(): void {
    // AuthService üzerinden çıkışı yapın
    this.authService.logout();

    // Anasayfaya yönlendirip tam sayfa yenileyin
    this.router.navigate(['/']).then(() => {
      window.location.reload();
    });
  }

  onSearchChange() {
    // Arama tetikleyici
  }
}
