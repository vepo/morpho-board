import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';


export interface AuthResponse {
  token: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private tokenKey = 'jwt_token';
  private readonly API_URL = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  login(email: string, password: string) {
    return this.http.post<AuthResponse>(`${this.API_URL}/auth/login`, { email, password }).pipe(
      tap(res => {
        if (res.token) this.saveToken(res.token);
      })
    );
  }

  saveToken(token: string) {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getRoles(): string[] {
    const token = this.getToken();
    if (!token) return [];
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.groups || [];
  }

  hasRole(role: string): boolean {
    return this.getRoles()
               .includes(role);
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
} 