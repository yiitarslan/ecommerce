import { Component, OnInit } from '@angular/core';
import { SellerProductService, SellerProduct as Product } from '../../../../core/services/seller-product.service';



@Component({
  selector: 'app-product-list',
  standalone:false,
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];

  constructor(private productService: SellerProductService) {}

  ngOnInit(): void {
    this.productService.getMyProducts().subscribe({
      next: (data) => {
        this.products = data;
        console.log('Ürünler:', data);
      },
      error: (err) => {
        console.error('Hata:', err);
      }
    });
  }

  deleteProduct(id: number) {
    if (!confirm('Bu ürünü silmek istediğinizden emin misiniz?')) return;
  
    this.productService.deleteProduct(id).subscribe({
      next: () => {
        this.products = this.products.filter(p => p.id !== id); // Sayfadan da sil
        alert('Ürün silindi.');
      },
      error: (err: any) => {  // <--- err: any olarak belirtmelisin
        console.error('Silme hatası:', err);
        alert('Ürün silinemedi.');
      }
    });
  }
}
