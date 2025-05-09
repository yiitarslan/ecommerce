import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { OrderListComponent } from './pages/order-list/order-list.component';

const routes: Routes = [
  { path: '', component: OrderListComponent }
];

@NgModule({
  declarations: 
  [OrderListComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes)
  ]
})
export class OrdersModule {}
