// src/app/core/auth/auth.service.ts
import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly loggedIn = signal(true);

  isAuthenticated(): boolean {
    return this.loggedIn();
  }

  async login(email: string, password: string): Promise<boolean> {
    console.log('Login attempt:', { email, password });
    this.loggedIn.set(true);
    return true;
  }

  logout(): void {
    this.loggedIn.set(false);
  }
}
