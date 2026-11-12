// src/app/features/dashboard/dashboard-page.component.ts
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FinanceService } from '../../data-access/services/finance.service';
import { ProfileService } from '../../data-access/services/profile.service';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [AsyncPipe, NgFor, NgIf],
  template: `
    <section class="dashboard">
      <header class="hero" *ngIf="profile$ | async as profile">
        <div>
          <p class="eyebrow">Welcome back</p>
          <h1>{{ profile.displayName }}</h1>
        </div>

        <img [src]="profile.avatarUrl" alt="user avatar" />
      </header>

      <div class="grid">
        <article class="panel">
          <h3>Quick stats</h3>
          <ul>
            <li>Goals: 3</li>
            <li>Saved this month: $1,140</li>
            <li>Community streak: 14 days</li>
          </ul>
        </article>

        <article class="panel">
          <h3>Finance snapshot</h3>
          <ul *ngIf="goals$ | async as goals">
            <li *ngFor="let goal of goals">
              {{ goal.title }}: \${{ goal.currentAmount }} / \${{ goal.targetAmount }}
            </li>
          </ul>
        </article>
      </div>
    </section>
  `,
  styles: [
    `
      .dashboard {
        display: grid;
        gap: 24px;
      }

      .hero {
        display: flex;
        justify-content: space-between;
        align-items: center;
        background: linear-gradient(135deg, #1d4ed8, #0f172a);
        border-radius: 18px;
        padding: 24px;
        color: white;
      }

      .eyebrow {
        margin: 0 0 8px;
        text-transform: uppercase;
        opacity: 0.8;
        letter-spacing: 0.08em;
        font-size: 0.7rem;
      }

      h1 {
        margin: 0;
        font-size: clamp(1.8rem, 2vw, 2.5rem);
      }

      .hero img {
        width: 72px;
        height: 72px;
        border-radius: 50%;
        object-fit: cover;
        border: 2px solid rgba(255, 255, 255, 0.4);
      }

      .grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
        gap: 20px;
      }

      .panel {
        background: #111827;
        border: 1px solid #334155;
        border-radius: 16px;
        padding: 20px;
      }

      ul {
        margin: 0;
        padding-left: 18px;
        display: grid;
        gap: 8px;
      }
    `,
  ],
})
export class DashboardPageComponent {
  private readonly profileService = inject(ProfileService);
  private readonly financeService = inject(FinanceService);

  readonly profile$ = this.profileService.getCurrentUser();
  readonly goals$ = this.financeService.getGoals();
}
