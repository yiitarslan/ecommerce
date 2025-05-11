import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductService, Product } from '../../core/services/product.service';
import { CartService } from '../../core/services/cart.service';
import { ReviewService, Review } from '../../core/services/review.service.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-product-detail',
  standalone: false,
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss']
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;
  reviews: Review[] = [];
  newComment: string = '';
  newRating: number = 0;
  isAdmin: boolean = false;

  // 🔹 Eklenen kısım
  sizes: string[] = ['S', 'M', 'L', 'XL'];
  selectedSize: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
    private reviewService: ReviewService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();

    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.productService.getById(+id).subscribe({
          next: (data) => {
            this.product = data;
            this.loadReviews(+id);
          },
          error: (err) => {
            console.error('Ürün alınamadı:', err);
          }
        });
      }
    });
  }

  // 🔹 Beden seçme
  selectSize(size: string) {
    this.selectedSize = size;
  }

  sepeteEkle() {
    if (this.product && this.selectedSize) {
      this.cartService.addToCart(this.product);
      alert(`Ürün (${this.selectedSize} beden) sepete eklendi!`);
    } else if (!this.selectedSize) {
      alert('Lütfen bir beden seçin.');
    } else {
      alert('Ürün yüklenemedi, lütfen tekrar deneyin.');
    }
  }

  loadReviews(productId: number) {
    this.reviewService.getReviewsByProduct(productId).subscribe({
      next: (data) => this.reviews = data,
      error: (err) => console.error('Yorumlar alınamadı:', err)
    });
  }

  submitReview() {
    if (!this.product) return;

    const review: Review = {
      comment: this.newComment,
      rating: this.newRating,
      product: { id: this.product.id },
      user: { id: 2 } // TODO: login sonrası kullanıcı ID alınacak
    };

    this.reviewService.addReview(review).subscribe(() => {
      this.newComment = '';
      this.newRating = 0;
      this.loadReviews(this.product!.id);
    });
  }

  selectRating(star: number) {
    this.newRating = star;
  }

  deleteReview(id: number) {
    if (!confirm('Bu yorumu silmek istiyor musunuz?')) return;

    this.reviewService.deleteReview(id).subscribe(() => {
      this.reviews = this.reviews.filter(r => r.id !== id);
    });
  }

  get averageRating(): number {
    if (this.reviews.length === 0) return 0;
    const total = this.reviews.reduce((sum, review) => sum + review.rating, 0);
    return parseFloat((total / this.reviews.length).toFixed(1));
  }

  showSizeOptions(): boolean {
  const name = this.product?.category?.name?.toLowerCase()?.trim();
  return name === "men's clothing" || name === "women's clothing";
}
}
