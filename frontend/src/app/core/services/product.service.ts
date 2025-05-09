import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  imageUrl: string;
  category: {
    id: number;
    name: string;
  };
  stock: number;
  quantity?: number; // Burada quantity özelliğini ekliyoruz
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private api = 'http://localhost:8080/api/products';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  // ✅ Tüm ürünleri getir
  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.api);
  }

  // ✅ Tek ürün getir
  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.api}/${id}`);
  }

  // ✅ Yeni ürün ekle
  add(product: Product): Observable<Product> {
    return this.http.post<Product>(this.api, product, {
      headers: this.getAuthHeaders()
    });
  }

  // ✅ Ürün güncelle
  update(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.api}/${id}`, product, {
      headers: this.getAuthHeaders()
    });
  }

  // ✅ Ürün sil
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`, {
      headers: this.getAuthHeaders()
    });
  }

  // ✅ Kategoriye göre ürünleri getir
  getProductsByCategory(categoryName: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.api}/category/${categoryName}`);
  }
}
