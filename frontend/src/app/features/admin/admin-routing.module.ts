import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminComponent } from './admin.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { UserListComponent } from './pages/user-list/user-list.component';
import { ProductListComponent } from './pages/product-list/product-list.component';
import { AddSellerComponent } from './pages/add-seller/add-seller.component';
import { OrderListComponent } from './pages/order-list/order-list.component';
import { AdminAnalyticsComponent } from './pages/admin-analytics/admin-analytics.component';

const routes: Routes = [
  {
    path: '',
    component: AdminComponent,
    children: [
      {
        path: '',
        component: DashboardComponent,
        children: [
          { path: '', redirectTo: 'users', pathMatch: 'full' },
          { path: 'users', component: UserListComponent },
          { path: 'products', component: ProductListComponent },
          { path: 'add-seller', component: AddSellerComponent },
          { path: 'orders', component: OrderListComponent },
          { path: 'analytics', component: AdminAnalyticsComponent }
        ]
      }
    ]
  },
  { path: 'analytics', loadChildren: () => import('./pages/admin-analytics/admin-analytics.module').then(m => m.AdminAnalyticsModule) }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule {}
