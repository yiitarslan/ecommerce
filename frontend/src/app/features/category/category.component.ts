import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService, Product } from '../../core/services/product.service';

@Component({
  selector: 'app-category',
  standalone: false,
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.scss']
})
export class CategoryComponent implements OnInit {
  products: Product[] = [];
  originalProducts: Product[] = [];
  categoryName: string = '';
  sortOption: string = ''; // 🔸 yeni eklendi

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.categoryName = params['name'];
      this.getProductsByCategory();
    });
  }

  getProductsByCategory() {
    this.productService.getProductsByCategory(this.categoryName).subscribe(data => {
      this.products = data;
      this.originalProducts = [...data]; // 🔸 sıralama için referans
    });
  }

  sortProducts(): void {
    switch (this.sortOption) {
      case 'priceLow':
        this.products.sort((a, b) => a.price - b.price);
        break;
      case 'priceHigh':
        this.products.sort((a, b) => b.price - a.price);
        break;
      case 'nameAsc':
        this.products.sort((a, b) => a.name.localeCompare(b.name));
        break;
      case 'nameDesc':
        this.products.sort((a, b) => b.name.localeCompare(a.name));
        break;
      default:
        this.products = [...this.originalProducts];
        break;
    }
  }

  goToDetail(id: number) {
    this.router.navigate(['/urun', id]);
  }
}
