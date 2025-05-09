import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductService, Product } from '../../core/services/product.service';
import { CartService } from '../../core/services/cart.service';

@Component({
  selector: 'app-product-detail',
  standalone:false,
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss']
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      console.log('Gelen ID:', id);
      if (id) {
        this.productService.getById(+id).subscribe({
          next: (data) => {
            console.log('Gelen ürün:', data);
            this.product = data;
          },
          error: (err) => {
            console.error('Ürün alınamadı:', err);
          }
        });
      }
    });
  }

  sepeteEkle() {
    if (this.product) {
      this.cartService.addToCart(this.product);
      alert('Ürün sepete eklendi!');
    } else {
      alert('Ürün yüklenemedi, lütfen tekrar deneyin.');
    }
  }
}
