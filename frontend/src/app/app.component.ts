import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone:false,
  styleUrls: ['./app.component.scss'] // 🔧 doğru yazımı "styleUrls"
})
export class AppComponent {
  title = 'yimu-ecommerce';

  constructor(public router: Router) {}

  isAdminOrSellerRoute(): boolean {
    return this.router.url.startsWith('/admin') || this.router.url.startsWith('/seller');
  }
  
}
