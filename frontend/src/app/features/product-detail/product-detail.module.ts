import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductDetailComponent } from './product-detail.component';
import { ProductDetailRoutingModule } from './product-detail-routing.module';

@NgModule({
  declarations: [ProductDetailComponent],
  imports: [
    CommonModule,
    ProductDetailRoutingModule
  ]
})
export class ProductDetailModule {}
