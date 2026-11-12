// src/app/core/auth/auth.service.ts
import { inject, Injectable, signal } from '@angular/core';
import { LoadingService } from '../loading/loading.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly loadingService = inject(LoadingService);
  private readonly loggedIn = signal(true);

  isAuthenticated(): boolean {
    return this.loggedIn();
  }

  async login(email: string, password: string): Promise<boolean> {
    this.loadingService.show('Authenticating with Firebase...');
    try {
      console.log('Login attempt:', { email, password });
      await new Promise(resolve => setTimeout(resolve, 250));
      this.loggedIn.set(true);
      return true;
    } finally {
      this.loadingService.hide();
    }
  }

  logout(): void {
    this.loggedIn.set(false);
  }
}
