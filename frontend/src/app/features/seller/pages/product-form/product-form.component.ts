import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SellerProductService } from '../../../../core/services/seller-product.service';
import { SellerCategoryService } from '../../../../core/services/selle-category.service';
import { Category } from '../../../../core/services/selle-category.service';

@Component({
  selector: 'app-product-form',
  standalone:false,
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.scss']
})
export class ProductFormComponent implements OnInit {
  form!: FormGroup;
  categories: Category[] = [];
  productId!: number;

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private productService: SellerProductService,
    private categoryService: SellerCategoryService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.productId = Number(this.route.snapshot.paramMap.get('id'));

    this.form = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      imageUrl: ['', Validators.required],
      price: ['', [Validators.required, Validators.min(1)]],
      stock: ['', [Validators.required, Validators.min(0)]],
      categoryId: ['', Validators.required]
    });

    // Kategorileri çek
    this.categoryService.getAll().subscribe(data => {
      this.categories = data;
    });

    // Ürünü çek ve forma doldur
    this.productService.getProductById(this.productId).subscribe(product => {
      this.form.patchValue({
        name: product.name,
        description: product.description,
        imageUrl: product.imageUrl,
        price: product.price,
        stock: product.stock,
        categoryId: product.category?.id ?? product.categoryId // ✨ Kritik kısım
      });
    });
  }

  submit() {
    if (this.form.invalid) return;

    this.productService.updateProduct(this.productId, this.form.value).subscribe({
      next: () => {
        alert('✅ Ürün başarıyla güncellendi.');
        this.router.navigate(['/seller/products']);
      },
      error: err => {
        console.error('❌ Güncelleme hatası:', err);
        alert('Ürün güncellenemedi.');
      }
    });
  }
}
