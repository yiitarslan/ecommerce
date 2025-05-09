import { Component } from '@angular/core';
import { AuthService, RegisterRequest } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { NgZone } from '@angular/core';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  fullName: string = '';
  email: string = '';
  password: string = '';
  confirmPassword: string = '';

  message: string = '';
  isError: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private ngZone: NgZone
  ) {}

  register() {
    if (this.password !== this.confirmPassword) {
      this.message = "❌ Şifreler uyuşmuyor.";
      this.isError = true;
      return;
    }

    const registerData: RegisterRequest = {
      fullName: this.fullName,
      email: this.email,
      password: this.password,
      confirmPassword: this.confirmPassword
    };

    this.authService.register(registerData).subscribe({
      next: () => {
        this.message = "✅ Kayıt başarılı! Giriş sayfasına yönlendiriliyorsunuz...";
        this.isError = false;

        setTimeout(() => {
          this.ngZone.run(() => this.router.navigate(['/giris']));
        }, 2000);
      },
      error: (err) => {
        this.message = err.message || '❌ Kayıt başarısız.';
        this.isError = true;
      }
    });
  }
}
