import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartComponent } from './cart.component';
import { CartRoutingModule } from './cart-routing.module'; // ✅ bunu ekle
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [CartComponent],
  imports: [
    CommonModule,
    RouterModule,
    CartRoutingModule,
    FormsModule // ✅ buraya eklenmeli!
  ]
})
export class CartModule {}
