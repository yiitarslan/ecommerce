import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SellerGuard } from './core/guards/seller.guard';
import { AdminGuard } from './core/guards/admin.guard';

const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./features/home/home.module').then(m => m.HomeModule)
  },
  {
    path: 'giris',
    loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: 'urun/:id',
    loadChildren: () => import('./features/product-detail/product-detail.module').then(m => m.ProductDetailModule)
  },
  {
    path: 'hesabim',
    loadChildren: () => import('./features/user-detail/user-detail.module').then(m => m.UserDetailModule)
  },
  {
    path: 'kategori',
    loadChildren: () => import('./features/category/category.module').then(m => m.CategoryModule)
  },
 
  {
    path: 'seller',
    canActivate: [SellerGuard],
    loadChildren: () => import('./features/seller/seller.module').then(m => m.SellerModule)
  },
  {
    path: 'admin',
    loadChildren: () => import('./features/admin/admin.module').then(m => m.AdminModule),
    canActivate: [AdminGuard]
  },
  {
    path: 'odeme',
    loadChildren: () =>
      import('./features/payment-form/payment-form.module')
        .then(m => m.PaymentFormModule)
  },
  {
    path: 'sepetim',
    loadChildren: () => import('./features/cart/cart.module').then(m => m.CartModule)
  },
]
  
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
