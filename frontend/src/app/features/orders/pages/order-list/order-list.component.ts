import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

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
  standalone: false,
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.scss']
})
export class OrderListComponent implements OnInit {
  activeOrders: Order[] = [];
  pastOrders: Order[] = [];
  selectedView: 'active' | 'past' = 'active';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (!token) return;

    this.http.get<Order[]>('http://localhost:8080/orders/user', {
      headers: { Authorization: `Bearer ${token}` }
    }).subscribe({
      next: (res) => {
        this.activeOrders = res.filter(order =>
          ['PROCESSING', 'SHIPPED'].includes(order.status)
        );
        this.pastOrders = res.filter(order =>
          ['COMPLETE', 'DELIVERED', 'CANCELLED'].includes(order.status)
        );
      },
      error: (err) => {
        console.error('Siparişler yüklenemedi: ', err);
      }
    });
  }

  selectView(view: 'active' | 'past') {
    this.selectedView = view;
  }

  cancelOrder(orderId: number) {
    const token = localStorage.getItem('token');
    if (!token) return;

    const confirmCancel = confirm('Bu siparişi iptal etmek istediğinize emin misiniz?');
    if (!confirmCancel) return;

    this.http.put(`http://localhost:8080/orders/${orderId}/cancel`, {}, {
      headers: { Authorization: `Bearer ${token}` }
    }).subscribe({
      next: (res) => {
  const index = this.activeOrders.findIndex(order => order.id === orderId);
  if (index !== -1) {
    const cancelledOrder = {
      ...this.activeOrders[index],
      status: 'CANCELLED'
    };
    this.activeOrders.splice(index, 1);
    this.pastOrders.unshift(cancelledOrder);
  }
  alert('Sipariş başarıyla iptal edildi.');
},
error: (err) => {
  const msg = typeof err.error === 'string'
    ? err.error
    : err.error?.message || 'Sipariş iptal edilemedi.';
  alert(msg);
}

    });
  }

  refundPayment(orderId: number) {
  const confirmRefund = confirm("Bu siparişin ödemesini iade etmek istiyor musunuz?");
  if (!confirmRefund) return;

  // 1. önce orderId'den paymentId'yi bul
  this.http.get<any>(`http://localhost:8080/api/payments/byOrder/${orderId}`).subscribe({
    next: (payment) => {
      const paymentId = payment.id;

      // 2. refund işlemini başlat
      this.http.post(`http://localhost:8080/api/payments/refund/${paymentId}`, {}).subscribe({
        next: () => {
          alert("✅ İade işlemi başarılı.");
        },
        error: (err) => {
          alert("❌ İade işlemi başarısız: " + (err.error?.error || err.message));
        }
      });
    },
    error: (err) => {
      alert("❌ İade işlemi başarısız: Ödeme bulunamadı.");
    }
  });
}

}
