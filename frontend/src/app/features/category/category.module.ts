import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CategoryComponent } from './category.component';
import { CategoryRoutingModule } from './category-routing.module';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [CategoryComponent],
  imports: [
    CommonModule,
    FormsModule,
    CategoryRoutingModule
  ]
})
export class CategoryModule {}
