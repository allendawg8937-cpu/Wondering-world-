// src/app/core/layout/app-shell.component.ts
import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { ThemeService } from '../theme/theme.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="app-shell">
      <aside class="sidebar">
        <div class="brand-row">
          <h2>Wondering World</h2>
          <button
            type="button"
            class="theme-toggle-btn"
            data-testid="global-theme-toggle"
            [attr.aria-pressed]="themeService.isHighContrastLight()"
            [attr.aria-label]="
              themeService.isHighContrastLight()
                ? 'Switch to default dark theme'
                : 'Switch to high-contrast light theme'
            "
            (click)="themeService.toggleTheme()"
          >
            {{
              themeService.isHighContrastLight()
                ? '🌙 Dark Theme'
                : '☀️ High-Contrast Light'
            }}
          </button>
        </div>

        <nav aria-label="Main navigation">
          <a routerLink="/dashboard" routerLinkActive="active">Dashboard</a>
          <a routerLink="/profile" routerLinkActive="active">Profile</a>
          <a routerLink="/avatar-3d" routerLinkActive="active">3D Avatar Studio</a>
          <a routerLink="/finance/goals" routerLinkActive="active">Finance Goals</a>
          <a routerLink="/downloads" routerLinkActive="active">APK &amp; SDK</a>
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
        background: var(--bg-app);
        color: var(--text-primary);
        font-family: Arial, sans-serif;
      }

      .app-shell {
        display: grid;
        grid-template-columns: 230px 1fr;
        min-height: 100vh;
      }

      @media (max-width: 780px) {
        .app-shell {
          grid-template-columns: 1fr;
        }
      }

      .sidebar {
        background: var(--bg-card);
        padding: 20px 18px;
        border-right: 1px solid var(--border-subtle);
        display: flex;
        flex-direction: column;
        gap: 18px;
      }

      .brand-row {
        display: grid;
        gap: 10px;
      }

      .sidebar h2 {
        margin: 0;
        font-size: 1.2rem;
        color: var(--text-primary);
      }

      .theme-toggle-btn {
        width: 100%;
        padding: 9px 11px;
        border-radius: 8px;
        border: 2px solid var(--border-strong);
        background: var(--bg-surface);
        color: var(--text-primary);
        font-size: 0.82rem;
        font-weight: 700;
        text-align: left;
        cursor: pointer;
      }

      nav {
        display: flex;
        flex-direction: column;
        gap: 8px;
      }

      nav a {
        color: var(--text-secondary);
        text-decoration: none;
        padding: 10px 12px;
        border-radius: 8px;
        border: 1px solid transparent;
        font-weight: 600;
        transition: background 0.2s ease;
      }

      nav a.active {
        background: var(--accent-primary);
        color: #ffffff;
      }

      nav .logout-btn {
        margin-top: 10px;
        padding: 10px 12px;
        border: 2px solid #dc2626;
        border-radius: 8px;
        background: rgba(220, 38, 38, 0.12);
        color: var(--text-primary);
        font-size: 0.95rem;
        font-weight: 700;
        text-align: left;
        cursor: pointer;
      }

      nav .logout-btn:hover {
        background: #dc2626;
        color: #ffffff;
      }

      .content {
        padding: 28px;
      }
    `,
  ],
})
export class AppShellComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  readonly themeService = inject(ThemeService);

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
