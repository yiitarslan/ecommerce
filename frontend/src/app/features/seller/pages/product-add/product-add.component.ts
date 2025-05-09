import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { SellerProductService } from '../../../../core/services/seller-product.service';
import { SellerCategoryService } from '../../../../core/services/selle-category.service';
import { Category } from '../../../../core/services/selle-category.service';

@Component({
  selector: 'app-product-add',
  standalone:false,
  templateUrl: './product-add.component.html',
  styleUrls: ['./product-add.component.scss']
})
export class ProductAddComponent implements OnInit {
  form!: FormGroup;
  categories: Category[] = [];

  constructor(
    private fb: FormBuilder,
    private categoryService: SellerCategoryService,
    private productService: SellerProductService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      imageUrl: ['', Validators.required],
      price: ['', [Validators.required, Validators.min(1)]],
      stock: ['', [Validators.required, Validators.min(0)]],
      categoryId: ['', Validators.required]  // sadece id gönderiyoruz
    });
    

    this.categoryService.getAll().subscribe((data) => {
      this.categories = data;
    });
  }

  submit() {
    if (this.form.invalid) return;

    this.productService.addProduct(this.form.value).subscribe({
      next: () => {
        alert('Ürün başarıyla eklendi!');
        this.router.navigate(['/seller/products']);
      },
      error: (err: any) => {
        console.error('Hata:', err);
        alert('Ürün eklenemedi.');
      }
    });
  }
}
