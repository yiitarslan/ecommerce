import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { loadStripe, Stripe } from '@stripe/stripe-js';
import { from, Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { PaymentRequest, StripePaymentResponse } from '../models/payment.model';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private stripePromise = loadStripe('pk_test_51RLM5O4DrMRzjlZKE38GRB99WQRQXD37rdQ8YEo3J0J34Gmocu0WIeF2FIRYsIAqoqIqTs0JfEKG8aaNHMnzPZ6D00CVZNHvPT');

  constructor(private http: HttpClient) {}

  /**
   * Backend'de PaymentIntent oluşturarak clientSecret ve paymentId döne

  /**
   * Stripe objesini yükleyip döner
   */
  getStripe(): Observable<Stripe> {
    return from(this.stripePromise).pipe(
      switchMap((stripe) => {
        if (!stripe) {
          throw new Error('Stripe yüklenemedi');
        }
        return from(Promise.resolve(stripe));
      })
    );
  }

  createStripePayment(userId: number, orderId: number, amount: number): Observable<any> {
    return this.http.post('http://localhost:8080/api/payments/stripe', {
      userId,
      orderId,
      amount
    }, { withCredentials: true });
  }
}