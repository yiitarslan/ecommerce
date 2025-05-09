import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  imageUrl: string;
  category: any;
}

@Injectable({
  providedIn: 'root'
})
export class SellerProductService {
  private API = 'http://localhost:8080/api/seller/products';

  constructor(private http: HttpClient) {}

  getSellerProducts(): Observable<Product[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    return this.http.get<Product[]>(this.API, { headers });
  }
}
