import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-order-list',
  standalone: false,
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.scss']
})
export class OrderListComponent implements OnInit {
  orders: any[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<any[]>('http://localhost:8080/orders')
      .subscribe({
        next: (data) => this.orders = data,
        error: (err) => console.error('Siparişler alınamadı', err)
      });
  }

  onStatusChange(event: Event, orderId: number) {
    const selectElement = event.target as HTMLSelectElement;
    const newStatus = selectElement.value;

    this.http.put(`http://localhost:8080/orders/updateStatus?id=${orderId}&status=${newStatus}`, {})
      .subscribe({
        next: () => {
          this.orders = this.orders.map(o =>
            o.id === orderId ? { ...o, status: newStatus } : o
          );
        },
        error: err => console.error("Durum güncellenemedi", err)
      });
  }

  deleteOrder(orderId: number) {
  if (!confirm("Bu siparişi silmek istediğinizden emin misiniz? Bu işlem refund içeriyorsa ödeme iade edilecektir.")) return;

  this.http.delete(`http://localhost:8080/orders/${orderId}`).subscribe({
    next: () => {
      alert("✅ Sipariş silindi ve varsa ödeme iade edildi.");
      this.orders = this.orders.filter(o => o.id !== orderId);
    },
    error: err => {
      console.error("Sipariş silinemedi", err);
      alert("❌ Sipariş silinirken hata oluştu: " + err.error.message);
    }
  });
}

}
