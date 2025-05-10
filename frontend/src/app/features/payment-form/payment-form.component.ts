import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ElementRef
} from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import {
  Stripe,
  StripeCardElement,
  StripeElements
} from '@stripe/stripe-js';
import { loadStripe } from '@stripe/stripe-js';
import { PaymentService } from '../../core/services/payment.service';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-payment',
  templateUrl: './payment-form.component.html',
  standalone: false,
  styleUrls: ['./payment-form.component.scss']
})
export class PaymentComponent implements OnInit, AfterViewInit {
  @ViewChild('cardInfo') cardInfo!: ElementRef;
  paymentForm!: FormGroup;
  stripe!: Stripe | null;
  elements!: StripeElements;
  card!: StripeCardElement;
  clientSecret = '';
  loading = false;
  errorMessage = '';
  successMessage = '';
  totalAmount = 0;

  constructor(
    private fb: FormBuilder,
    private paymentService: PaymentService,
    private authService: AuthService,
    private cartService: CartService,
    private router: Router,
    private http: HttpClient
  ) {}

  async ngOnInit(): Promise<void> {
  this.paymentForm = this.fb.group({ amount: [''] });

  // Sepeti yükle ve ardından totalAmount güncelle
  this.cartService.loadCartItems();

  // Sepetin yüklenmesini beklemek için kısa bir bekleme süresi tanımla
  setTimeout(async () => {
    this.totalAmount = this.cartService.getCartTotal();

    this.stripe = await loadStripe('pk_test_51RLM5O4DrMRzjlZKE38GRB99WQRQXD37rdQ8YEo3J0J34Gmocu0WIeF2FIRYsIAqoqIqTs0JfEKG8aaNHMnzPZ6D00CVZNHvPT');
    if (this.stripe) {
      this.elements = this.stripe.elements();
      this.card = this.elements.create('card');
      this.card.mount(this.cardInfo.nativeElement); // güvenle mount edebilirsin
    }
  }, 300); // 300ms sonra sepet verisi %99 hazırdır
}


  ngAfterViewInit(): void {
    setTimeout(() => {
      if (this.card && this.cardInfo) {
        this.card.mount(this.cardInfo.nativeElement);
      }
    }, 100);
  }

  startPayment() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const userId = user.id;

    this.http.post<any>(`http://localhost:8080/orders/create/${userId}`, {}).subscribe({
      next: (orderResponse) => {
        const orderId = orderResponse.id;
        const amount = this.totalAmount;

        this.paymentService.createStripePayment(userId, orderId, amount).subscribe({
          next: (stripeRes: any) => {
            const clientSecret = stripeRes.clientSecret;

            if (this.stripe) {
              this.stripe.confirmCardPayment(clientSecret, {
                payment_method: {
                  card: this.card,
                  billing_details: {
                    name: user.firstName + ' ' + user.lastName,
                    email: user.email
                  }
                }
              }).then(result => {
                if (result.error) {
                  this.errorMessage = result.error.message || 'Ödeme sırasında hata oluştu';
                  this.loading = false;
                } else if (result.paymentIntent.status === 'succeeded') {
                  console.log("✅ Ödeme tamamlandı.");

                  this.http.post('http://localhost:8080/api/payments', {
                    userId: userId,
                    orderId: orderId,
                    amount: amount,
                    paymentMethod: 'CREDIT_CARD',
                    paymentIntentId: result.paymentIntent.id, // 🔥 ekle
                    success: true
                  }).subscribe({
                    next: (res) => {
                      this.successMessage = '✔ Ödeme başarıyla tamamlandı!';
                      this.cartService.clearCart();
                      this.loading = false;

                      setTimeout(() => {
                        this.router.navigate(['/']);
                      }, 2000);
                    },
                    error: (err) => {
                      this.errorMessage = "Ödeme kayıt edilemedi: " + err.message;
                      this.loading = false;
                    }
                  });
                }
              });
            }
          },
          error: (err) => {
            this.errorMessage = "Stripe ödeme başlatılamadı: " + err.message;
            this.loading = false;
          }
        });
      },
      error: (err) => {
        this.errorMessage = "Sipariş oluşturulamadı: " + err.message;
        this.loading = false;
      }
    });
  }
}
