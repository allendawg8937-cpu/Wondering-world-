// src/app/core/layout/app-shell.component.ts
import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="app-shell">
      <aside class="sidebar">
        <h2>Wondering World</h2>

        <nav>
          <a routerLink="/dashboard" routerLinkActive="active">Dashboard</a>
          <a routerLink="/profile" routerLinkActive="active">Profile</a>
          <a routerLink="/finance/goals" routerLinkActive="active">Finance Goals</a>
          <button type="button" class="logout-btn" (click)="logout()">
            Logout
          </button>
        </nav>
      </aside>

      <main class="content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [
    `
      :host {
        display: block;
        min-height: 100vh;
        background: #0f172a;
        color: #e2e8f0;
        font-family: Arial, sans-serif;
      }

      .app-shell {
        display: grid;
        grid-template-columns: 220px 1fr;
        min-height: 100vh;
      }

      .sidebar {
        background: #111827;
        padding: 24px 20px;
        border-right: 1px solid #1f2937;
      }

      .sidebar h2 {
        margin: 0 0 24px;
        font-size: 1.2rem;
      }

      nav {
        display: flex;
        flex-direction: column;
        gap: 10px;
      }

      nav a {
        color: #cbd5e1;
        text-decoration: none;
        padding: 10px 12px;
        border-radius: 8px;
        transition: background 0.2s ease;
      }

      nav a.active {
        background: #1d4ed8;
        color: white;
      }

      nav .logout-btn {
        margin-top: 12px;
        padding: 10px 12px;
        border: 1px solid #ef4444;
        border-radius: 8px;
        background: rgba(239, 68, 68, 0.14);
        color: #fca5a5;
        font-size: 1rem;
        font-weight: 600;
        text-align: left;
        cursor: pointer;
        transition: background 0.2s ease, color 0.2s ease;
      }

      nav .logout-btn:hover {
        background: #ef4444;
        color: #ffffff;
      }

      .content {
        padding: 32px;
      }
    `,
  ],
})
export class AppShellComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
