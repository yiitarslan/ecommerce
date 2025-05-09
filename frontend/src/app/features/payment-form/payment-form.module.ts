import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

import { PaymentFormRoutingModule } from './payment-form-routing.module';
import { PaymentComponent } from './payment-form.component';

@NgModule({
  declarations: [PaymentComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PaymentFormRoutingModule
  ]
})
export class PaymentFormModule {}