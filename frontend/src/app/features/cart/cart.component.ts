import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { CartService, CartItem } from '../../core/services/cart.service';

@Component({
  selector: 'app-cart',
  standalone: false,
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit, OnDestroy {
  cartItems: CartItem[] = [];
  private sub!: Subscription;

  constructor(
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Sepeti backend’den çek
    this.cartService.loadCartItems();

    // Sepet öğelerini dinle
    this.sub = this.cartService.cartItems$.subscribe(items => {
      this.cartItems = items;
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  /** Dinamik olarak her quantity değiştiğinde hesaplanır */
  get totalPrice(): number {
    return this.cartItems
      .reduce((sum, item) => sum + item.product.price * item.quantity, 0);
  }

  increaseQuantity(item: CartItem): void {
    item.quantity++;
    this.cartService.updateItem(item);
  }
  

  decreaseQuantity(item: CartItem): void {
    if (item.quantity > 1) {
      item.quantity--;
      this.cartService.updateItem(item);
    }
  }

  removeFromCart(item: CartItem): void {
    this.cartService.removeItem(item);
  }

  /** Ödeme sayfasına yönlendir */
  checkout(): void {
    this.router.navigate(['/checkout']);
  }
}
