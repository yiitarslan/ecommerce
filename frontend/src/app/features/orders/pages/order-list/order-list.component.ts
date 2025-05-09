import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

interface OrderItem {
  productName: string;
  quantity: number;
  unitPrice: number;
}

interface Order {
  id: number;
  customerName: string;
  totalPrice: number;
  status: string;
  items: OrderItem[];
}

@Component({
  selector: 'app-order-list',
  standalone:false,
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.scss']
})
export class OrderListComponent implements OnInit {
  orders: Order[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
  const token = localStorage.getItem('token');
  if (!token) return;

  this.http.get<Order[]>('http://localhost:8080/orders/user', {
    headers: { Authorization: `Bearer ${token}` }
  }).subscribe({
    next: (res) => {
      this.orders = res.filter(order => order.status !== 'PENDING');
    },
    error: (err) => {
      console.error('Siparişler yüklenemedi: ', err);
    }
  });
}

}
