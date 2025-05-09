import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface Product {
  id: number;
  name: string;
  price: number;
  description: string;
  category: string;
}

@Component({
  selector: 'app-product-list',
  standalone: false,
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  isDeleting: boolean = false;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.getProducts(); // Sayfa yüklendiğinde ürünleri getir
  }

  getProducts() {
    this.http.get<Product[]>('http://localhost:8080/api/admin/products').subscribe(data => {
      this.products = data;
    });
  }

  deleteProduct(id: number) {
    if (!confirm("Bu ürünü silmek istediğine emin misin?")) return;

    this.isDeleting = true;

    this.http.delete(`http://localhost:8080/api/admin/products/${id}`).subscribe(() => {
      alert("Ürün silindi.");
      this.getProducts(); // Silme sonrası listeyi yenile
      this.isDeleting = false;
    });
  }
}
