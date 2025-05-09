import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SellerComponent } from './seller.component';
import { SellerDashboardComponent } from './pages/seller-dashboard/seller-dashboard.component';
import { ProductListComponent } from './pages/product-list/product-list.component';
import { ProductFormComponent } from './pages/product-form/product-form.component';
import { ProductAddComponent } from './pages/product-add/product-add.component';

const routes: Routes = [
  {
    path: '',
    component: SellerComponent,
    children: [
      { path: '', component: SellerDashboardComponent },
      { path: 'products', component: ProductListComponent },
      { path: 'products/new', component: ProductAddComponent },
      { path: 'products/edit/:id', component: ProductFormComponent },
      { path: 'products/add', component: ProductAddComponent }
    ]
  }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SellerRoutingModule {}
