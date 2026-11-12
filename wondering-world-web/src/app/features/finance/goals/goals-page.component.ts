// src/app/features/finance/goals/goals-page.component.ts
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FinanceService } from '../../../data-access/services/finance.service';

@Component({
  selector: 'app-goals-page',
  standalone: true,
  imports: [AsyncPipe, NgFor, NgIf],
  template: `
    <section class="goals">
      <h1>Finance goals</h1>

      <div class="goal-list" *ngIf="goals$ | async as goals">
        <article class="goal" *ngFor="let goal of goals">
          <div class="top-row">
            <h3>{{ goal.title }}</h3>
            <span>{{ ((goal.currentAmount / goal.targetAmount) * 100).toFixed(0) }}%</span>
          </div>

          <div class="progress">
            <div class="bar" [style.width.%]="(goal.currentAmount / goal.targetAmount) * 100"></div>
          </div>

          <p>\${{ goal.currentAmount }} / \${{ goal.targetAmount }}</p>
        </article>
      </div>
    </section>
  `,
  styles: [
    `
      .goals {
        display: grid;
        gap: 24px;
      }

      h1 {
        margin: 0;
        font-size: 2rem;
      }

      .goal-list {
        display: grid;
        gap: 20px;
      }

      .goal {
        background: #111827;
        border: 1px solid #334155;
        border-radius: 16px;
        padding: 20px;
      }

      .top-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
      }

      .progress {
        height: 12px;
        background: #1f2937;
        border-radius: 999px;
        overflow: hidden;
        margin-bottom: 12px;
      }

      .bar {
        height: 100%;
        background: linear-gradient(90deg, #22c55e, #3b82f6);
        border-radius: 999px;
      }

      p {
        margin: 0;
        color: #cbd5e1;
      }
    `,
  ],
})
export class GoalsPageComponent {
  private readonly financeService = inject(FinanceService);

  readonly goals$ = this.financeService.getGoals();
}
