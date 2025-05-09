import { Component, OnInit } from '@angular/core';
import { ProductService, Product } from '../../core/services/product.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  products: Product[] = [];
  currentIndex = 0;
  favorites: number[] = [];

  constructor(
    private productService: ProductService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.productService.getAll().subscribe({
      next: (data) => {
        console.log("Gelen ürünler:", data); // 👈 kontrol et
        this.products = data;
      },
      error: (err) => {
        console.error("HATA:", err); // 👈 hata varsa buraya düşer
      }
    });
  }
  
  currentProduct(): Product | null {
    return this.products[this.currentIndex] || null;
  }

  next(): void {
    if (this.currentIndex < this.products.length - 1) {
      this.currentIndex++;
    }
  }

  prev(): void {
    if (this.currentIndex > 0) {
      this.currentIndex--;
    }
  }

  toggleFavorite(productId: number, event: MouseEvent): void {
    event.stopPropagation();

    const index = this.favorites.indexOf(productId);
    if (index > -1) {
      this.favorites.splice(index, 1);
    } else {
      this.favorites.push(productId);
    }

    localStorage.setItem('favorites', JSON.stringify(this.favorites));
  }

  isFavorite(productId: number): boolean {
    return this.favorites.includes(productId);
  }

  goToDetail(productId: number): void {
    this.router.navigate(['/urun', productId]);
  }
}
