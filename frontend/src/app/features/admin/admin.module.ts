import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminRoutingModule } from './admin-routing.module';
import { AdminComponent } from './admin.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { UserListComponent } from './pages/user-list/user-list.component';
import { ProductListComponent } from './pages/product-list/product-list.component';
import { AddSellerComponent } from './pages/add-seller/add-seller.component';
import { FormsModule } from '@angular/forms';
import { OrderListComponent } from './pages/order-list/order-list.component';

@NgModule({
  declarations: [
    AdminComponent,
    DashboardComponent,
    UserListComponent,
    ProductListComponent,
    AddSellerComponent,
    OrderListComponent
  ],
  imports: [
    CommonModule,
    AdminRoutingModule,
    FormsModule
  ]
})
export class AdminModule {}