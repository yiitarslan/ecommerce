// src/app/core/services/cart.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Product } from './product.service';
import { Order } from '../models/order.model';

export interface CartItem {
  id: number;
  product: Product;
  quantity: number;
  totalPrice: number;
  userId: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private items: CartItem[] = [];
  private apiUrl = 'http://localhost:8080/cart';

  // Sepet öğelerini yayınlayan subject
  private cartItemsSubject = new BehaviorSubject<CartItem[]>([]);
  public cartItems$ = this.cartItemsSubject.asObservable();

  // Sepetteki toplam adet sayısını yayınlayan subject
  private cartCountSubject = new BehaviorSubject<number>(0);
  public cartItemCount$ = this.cartCountSubject.asObservable();

  constructor(private http: HttpClient) {}

  /** Sepeti backend’den çek ve subject’leri güncelle */
  loadCartItems(): void {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const userId = user.id;
    this.http
      .get<CartItem[]>(`${this.apiUrl}/view/${userId}`, { withCredentials: true })
      .subscribe({
        next: data => {
          this.items = data.map(item => ({
            ...item,
            totalPrice: item.product.price * item.quantity
          }));
          this.syncSubjects();
        },
        error: err => {
          console.error('Sepet verisi alınamadı', err);
          this.items = [];
          this.syncSubjects();
        }
      });
  }

  /** Sepete ürün ekle, sonra yeniden yükle */
  addToCart(product: Product): void {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    this.http
      .post(`${this.apiUrl}/add`,
        { userId: user.id, productId: product.id, quantity: 1 },
        { withCredentials: true }
      )
      .subscribe({
        next: () => this.loadCartItems(),
        error: err => console.error('Sepete ekleme hatası', err)
      });
  }

  /** Sadece ilgili öğeyi güncelle, sonra local state’i senkronize et */
  updateItem(item: CartItem): void {
    this.http
      .put<CartItem>(`${this.apiUrl}/update/${item.id}`,
        { userId: item.userId, quantity: item.quantity },
        { withCredentials: true }
      )
      .subscribe({
        next: updated => {
          const idx = this.items.findIndex(i => i.id === updated.id);
          if (idx > -1) {
            this.items[idx] = {
              ...updated,
              totalPrice: updated.product.price * updated.quantity
            };
            this.syncSubjects();
          }
        },
        error: err => console.error('Güncelleme hatası', err)
      });
  }

  /** Sadece ilgili öğeyi sil, sonra local state’i senkronize et */
  removeItem(item: CartItem): void {
    this.http
      .delete(`${this.apiUrl}/remove/${item.id}`, { withCredentials: true })
      .subscribe({
        next: () => {
          this.items = this.items.filter(i => i.id !== item.id);
          this.syncSubjects();
        },
        error: err => console.error('Silme hatası', err)
      });
  }

  /** Sepeti tamamen temizle */
  clearCart(): void {
    this.items = [];
    this.syncSubjects();
  }

  /** Son halini senkron döner */
  getItems(): CartItem[] {
    return [...this.items];
  }

  /** Sepet toplamını döner */
  getCurrentTotal(): number {
    return this.items.reduce((sum, i) => sum + (i.product.price * i.quantity), 0);
  }

  /** Sipariş oluşturur ve Order nesnesi döner */
  checkoutOrder(): Observable<Order> {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    return this.http.post<Order>(
      'http://localhost:8080/orders/create',
      { userId: user.id, items: this.items },
      { withCredentials: true }
    ).pipe(
      tap(() => this.clearCart())
    );
  }

  /** Subject’leri hem öğe listesi hem adet sayısı ile güncelle */
  private syncSubjects(): void {
    this.cartItemsSubject.next([...this.items]);
    const totalCount = this.items.reduce((sum, it) => sum + it.quantity, 0);
    this.cartCountSubject.next(totalCount);
  }

  getCartTotal(): number {
    return this.items.reduce((total, item) => total + item.totalPrice, 0);
  }
}