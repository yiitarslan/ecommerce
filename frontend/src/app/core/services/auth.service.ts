import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, catchError, tap, throwError } from 'rxjs';

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface User {
  id: number;
  fullName: string;
  email: string;
  password?: string;
  role: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  // 🔥 Oturum durumunu anlık takip etmek için
  private isLoggedInSubject = new BehaviorSubject<boolean>(this.hasInitialToken());
  isLoggedIn$ = this.isLoggedInSubject.asObservable();

  constructor(private http: HttpClient) {}

  private hasInitialToken(): boolean {
    return localStorage.getItem('user') !== null && localStorage.getItem('token') !== null;
  }

 register(data: RegisterRequest): Observable<User> {
  return this.http.post<User>(`${this.apiUrl}/register`, data).pipe(
    // ❌ localStorage'a yazma veya giriş durumu tetikleme yok
    catchError((err) => {
      console.error('Register error:', err);
      return throwError(() => new Error(err.error || 'Kayıt başarısız'));
    })
  );
}


  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, data).pipe(
      tap((res) => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('user', JSON.stringify(res.user));
        this.isLoggedInSubject.next(true); // 🔁 Giriş anında güncelle
        console.log('🟢 Giriş başarılı:', res.user);
      }),
      catchError((err) => throwError(() => new Error(err.error || 'Giriş başarısız')))
    );
  }

  logout(): void {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    this.isLoggedInSubject.next(false); // 🔁 Çıkışta sıfırla
  }

  isLoggedIn(): boolean {
    return this.hasInitialToken();
  }

  getUser(): User | null {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  updateUser(id: number, updated: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, updated);
  }

  getCurrentUser(): any {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  isAdmin(): boolean {
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  return user?.role === 'ADMIN';
  }
}
