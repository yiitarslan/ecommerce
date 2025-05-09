import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-add-seller',
  templateUrl: './add-seller.component.html',
  styleUrls: ['./add-seller.component.scss'],
  standalone: false
})
export class AddSellerComponent {
  fullName = '';
  email = '';
  password = '';
  message = '';

  constructor(private http: HttpClient) {}

  registerSeller() {
    const payload = {
      fullName: this.fullName,
      email: this.email,
      password: this.password,
      role: 'SELLER'
    };

    this.http.post('http://localhost:8080/api/auth/register', payload)
      .subscribe({
        next: (res: any) => {
          this.message = '✅ Kayıt başarılı: ' + res.email;
          this.fullName = '';
          this.email = '';
          this.password = '';
        },
        error: err => {
          this.message = '❌ Hata: ' + (err.error?.message || 'Bilinmeyen hata');
        }
      });
  }
}
