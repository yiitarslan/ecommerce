import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { SellerRoutingModule } from './seller-routing.module'; // ✅ unutulmuştu

import { SellerComponent } from './seller.component';
import { SellerDashboardComponent } from './pages/seller-dashboard/seller-dashboard.component';
import { ProductAddComponent } from './pages/product-add/product-add.component';
import { ProductListComponent } from './pages/product-list/product-list.component';
import { ProductFormComponent } from './pages/product-form/product-form.component';

@NgModule({
  declarations: [
    SellerComponent,
    SellerDashboardComponent,
    ProductAddComponent,
    ProductListComponent,
    ProductFormComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SellerRoutingModule // ✅ önemli
  ]
})
export class SellerModule {}
