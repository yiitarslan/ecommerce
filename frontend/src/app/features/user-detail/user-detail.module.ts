import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { UserDetailComponent } from './user-detail.component';

const routes: Routes = [
  { path: '', component: UserDetailComponent }
];

@NgModule({
  declarations: [UserDetailComponent], // ✅ Çünkü standalone değil
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes)
  ]
})
export class UserDetailModule {}
