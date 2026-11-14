// src/app/core/layout/app-shell.component.ts
import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { PersonaService } from '../../data-access/services/persona.service';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="app-shell">
      <aside class="sidebar">
        <h2>Wondering World</h2>

        <div
          class="active-persona-badge"
          [style.border-color]="activePersona().accentColor"
        >
          <span class="persona-kicker">Active Stage</span>
          <strong>{{ activePersona().label }}</strong>
          <small>{{ activePersona().ageRange }}</small>
          <button
            type="button"
            class="quick-cycle-btn"
            (click)="cyclePersona()"
          >
            Switch Stage
          </button>
        </div>

        <nav>
          <a routerLink="/dashboard" routerLinkActive="active">Dashboard</a>
          <a routerLink="/resources" routerLinkActive="active">Community Resources</a>
          <a routerLink="/photo-studio" routerLinkActive="active">Photo Studio</a>
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
        margin: 0 0 18px;
        font-size: 1.2rem;
      }

      .active-persona-badge {
        margin-bottom: 20px;
        padding: 12px;
        border-radius: 12px;
        border: 1px solid #334155;
        background: #0f172a;
        display: grid;
        gap: 4px;
      }

      .persona-kicker {
        font-size: 0.68rem;
        text-transform: uppercase;
        letter-spacing: 0.08em;
        color: #94a3b8;
      }

      .active-persona-badge small {
        color: #94a3b8;
        font-size: 0.76rem;
      }

      .quick-cycle-btn {
        margin-top: 6px;
        padding: 6px 10px;
        border-radius: 8px;
        border: 1px solid #334155;
        background: #1e293b;
        color: #f8fafc;
        font-size: 0.78rem;
        font-weight: 600;
        cursor: pointer;
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
  private readonly personaService = inject(PersonaService);
  private readonly router = inject(Router);

  readonly activePersona = this.personaService.activePersona;

  cyclePersona(): void {
    this.personaService.cycleNextPersona();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
