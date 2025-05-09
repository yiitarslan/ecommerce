import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { User } from '../../core/services/auth.service';

@Component({
  selector: 'app-user-detail',
  standalone: false,
  templateUrl: './user-detail.component.html',
  styleUrls: ['./user-detail.component.scss']
})
export class UserDetailComponent implements OnInit {
  user!: User;
  editMode = false;
  newEmail = '';
  newPassword = '';

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    const stored = localStorage.getItem('user');
    if (stored) {
      const localUser = JSON.parse(stored);

      if (!localUser.id) {
        console.error("❌ Kullanıcı ID'si undefined!");
        return;
      }

      this.authService.getUserById(localUser.id).subscribe({
        next: (data) => {
          this.user = data;
          this.newEmail = data.email; // 🔧 email input'u doldur
        },
        error: (err) => {
          console.error("Kullanıcı bilgileri alınamadı:", err);
        }
      });
    }
  }

  enableEdit() {
    this.editMode = true;
  }

  cancelEdit() {
    this.editMode = false;
    this.newEmail = this.user.email;
    this.newPassword = '';
  }

  saveChanges() {
    const updatedData: Partial<User> = {
      id: this.user.id,
      fullName: this.user.fullName, // 🔒 fullname'in silinmesini engeller
      email: this.newEmail
    };

    if (this.newPassword) {
      updatedData.password = this.newPassword;
    }

    this.authService.updateUser(this.user.id, updatedData).subscribe({
      next: (updated) => {
        this.user = updated;
        localStorage.setItem('user', JSON.stringify(updated));
        this.editMode = false;
      },
      error: (err) => {
        console.error('🛑 Güncelleme başarısız:', err);
      }
    });
  }
}
