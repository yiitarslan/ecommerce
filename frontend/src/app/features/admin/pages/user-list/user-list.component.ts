import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface User {
  id: number;
  fullName: string;
  email: string;
  role: string;
}

@Component({
  selector: 'app-user-list',
  standalone:false,
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit {
  users: User[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<User[]>('http://localhost:8080/api/admin/users').subscribe(data => {
      this.users = data;
    });
  }

  deleteUser(id: number) {
    if (!confirm("Bu kullanıcıyı silmek istediğine emin misin?")) return;

    this.http.delete(`http://localhost:8080/api/admin/users/${id}`).subscribe(() => {
      this.users = this.users.filter(u => u.id !== id);
      alert("Kullanıcı silindi.");
    });
  }
}
