import { Component } from '@angular/core';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  standalone: false,
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent {
  email: string = '';

  sendResetLink() {
    console.log('Şifre sıfırlama bağlantısı gönderildi:', this.email);
  }
}
