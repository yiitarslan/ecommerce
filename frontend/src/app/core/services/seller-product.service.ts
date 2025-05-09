import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SellerProduct {
  id: number;
  name: string;
  description: string;
  imageUrl: string;
  price: number;
  stock: number;
  categoryId: number;
  category?: {
    id: number;
    name: string;
  }; // 👈 category ekle
}

@Injectable({
  providedIn: 'root'
})
export class SellerProductService {
  private apiUrl = 'http://localhost:8080/api/seller/products';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  // ✅ BU METOT EKSİKSE Angular hata verir
  addProduct(product: SellerProduct): Observable<any> {
    return this.http.post(this.apiUrl, product, {
      headers: this.getAuthHeaders()
    });
  }

  getMyProducts(): Observable<SellerProduct[]> {
    return this.http.get<SellerProduct[]>(this.apiUrl, {
      headers: this.getAuthHeaders()
    });
  }

  getProductById(id: number): Observable<SellerProduct> {
    return this.http.get<SellerProduct>(`${this.apiUrl}/${id}`, {
      headers: this.getAuthHeaders()
    });
  }
  
  updateProduct(id: number, productData: any): Observable<any> {
    const token = localStorage.getItem('token');
    return this.http.put(`http://localhost:8080/api/seller/products/${id}`, productData, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }
  
  deleteProduct(id: number): Observable<void> {
    const token = localStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };
  
    return this.http.delete<void>(`http://localhost:8080/api/seller/products/${id}`, { headers });
  }
}
