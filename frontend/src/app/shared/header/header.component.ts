import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { AuthService } from '../../core/services/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  standalone: false
})
export class HeaderComponent implements OnInit, OnDestroy {
  isLoggedIn = false;
  userEmail = '';
  isDropdownOpen = false;
  searchText = '';
  cartItemCount = 0;
  private authSub!: Subscription;

  constructor(
    private router: Router,
    private cartService: CartService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.authSub = this.authService.isLoggedIn$.subscribe((status) => {
      this.isLoggedIn = status;
      if (status) {
        const user = this.authService.getCurrentUser();
        this.userEmail = user?.email || '';
      } else {
        this.userEmail = '';
      }
    });

    this.cartService.cartItemCount$.subscribe(count => {
      this.cartItemCount = count;
    });
  }

  ngOnDestroy(): void {
    this.authSub.unsubscribe();
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
    this.authService.logout();
    this.router.navigate(['/']);
  }

  onSearchChange() {}
}
