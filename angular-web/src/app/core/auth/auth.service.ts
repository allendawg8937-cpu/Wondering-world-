// src/app/core/auth/auth.service.ts
import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly loggedIn = signal(true);
  private readonly currentEmail = signal('jordan@wonderingworld.app');

  isAuthenticated(): boolean {
    return this.loggedIn();
  }

  userEmail(): string {
    return this.currentEmail();
  }

  async login(email: string, password: string): Promise<boolean> {
    const cleanEmail = email.trim();
    if (!cleanEmail || !cleanEmail.includes('@') || password.length < 6) {
      return false;
    }
    this.currentEmail.set(cleanEmail);
    this.loggedIn.set(true);
    return true;
  }

  logout(): void {
    this.loggedIn.set(false);
  }
}
