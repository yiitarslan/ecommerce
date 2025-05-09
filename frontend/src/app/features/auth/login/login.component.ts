import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, LoginRequest } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  email: string = '';
  password: string = '';

  constructor(private router: Router, private authService: AuthService) {}

  login(): void {
    const loginData: LoginRequest = {
      email: this.email,
      password: this.password
    };

    this.authService.login(loginData).subscribe({
      next: (res) => {
        console.log('Giriş başarılı!', res.user);
        localStorage.setItem('token', res.token);
        localStorage.setItem('user', JSON.stringify(res.user));

        // 🎯 Rol kontrolü ve yönlendirme
        if (res.user.role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else if(res.user.role === 'SELLER'){
          this.router.navigate(['/seller']);
        }else {
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        console.error('Giriş hatası:', err);
        alert(err.message || 'Giriş başarısız.');
      }
    });
  }

  goToRegister() {
    this.router.navigate(['/giris/kayit']);
  }
}
