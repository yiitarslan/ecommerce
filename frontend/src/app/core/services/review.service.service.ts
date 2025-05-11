import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Review {
  id?: number;
  comment: string;
  rating: number;
  createdAt?: string; // 🔹 tarih alanı eklendi
  product: { id: number };
  user: {
    id: number;
    fullName?: string; // 🔹 kullanıcı adı eklendi
  };
}


@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private baseUrl = 'http://localhost:8080/api/reviews';

  constructor(private http: HttpClient) {}

  getReviewsByProduct(productId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.baseUrl}/product/${productId}`);
  }

  addReview(review: Review): Observable<Review> {
    return this.http.post<Review>(this.baseUrl, review);
  }

  deleteReview(id: number): Observable<void> {
  return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
