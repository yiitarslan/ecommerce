import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { ProductService, Product } from '../../core/services/product.service';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';
import { Subscription, Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';

@Component({
  selector: 'app-header',
  standalone: false,
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy {
  isLoggedIn = false;
  userEmail = '';
  isDropdownOpen = false;
  searchText = '';
  cartItemCount = 0;
  searchResults: Product[] = [];

  private authSub!: Subscription;
  private searchSubject: Subject<string> = new Subject<string>(); // ✅ Eksik olan tanım

  constructor(
    private router: Router,
    private cartService: CartService,
    private authService: AuthService,
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    this.authSub = this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
      this.userEmail = status ? this.authService.getCurrentUser()?.email || '' : '';
    });

    this.cartService.cartItemCount$.subscribe(count => {
      this.cartItemCount = count;
    });

    // ✅ Debounce ile arama işlemi (300ms sonra tetiklenir)
    this.searchSubject.pipe(debounceTime(300)).subscribe((term: string) => {
      const search = term.trim().toLowerCase();
      if (!search) {
        this.searchResults = [];
        return;
      }

      this.productService.getAll().subscribe(products => {
        this.searchResults = products.filter(p =>
          p.name.toLowerCase().includes(search)
        );
      });
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

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  // 🔄 Kullanıcı her yazdığında bu çağrılır ama debounce uygulandığı için API spamlenmez
  onSearchChange(): void {
    this.searchSubject.next(this.searchText);
  }

  goToDetail(id: number): void {
    this.searchText = '';
    this.searchResults = [];
    this.router.navigate(['/urun', id]);
  }

  showDropdown() {
    this.isDropdownOpen = true;
  }

  hideDropdown() {
    this.isDropdownOpen = false;
  }
}
