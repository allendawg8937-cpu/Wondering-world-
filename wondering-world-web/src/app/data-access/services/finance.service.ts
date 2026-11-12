// src/app/data-access/services/finance.service.ts
import { inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { LoadingService } from '../../core/loading/loading.service';
import { FinanceGoal } from '../models/finance-goal.model';

@Injectable({
  providedIn: 'root',
})
export class FinanceService {
  private readonly loadingService = inject(LoadingService);

  getGoals(): Observable<FinanceGoal[]> {
    return this.loadingService.trackObservable(
      of([
        {
          id: 'g1',
          title: 'Emergency Fund',
          targetAmount: 5000,
          currentAmount: 3200,
        },
        {
          id: 'g2',
          title: 'Travel Fund',
          targetAmount: 3000,
          currentAmount: 1500,
        },
        {
          id: 'g3',
          title: 'Home Office Setup',
          targetAmount: 2000,
          currentAmount: 850,
        },
      ]).pipe(delay(250)),
      'Fetching finance goals from Firebase...'
    );
  }
}
