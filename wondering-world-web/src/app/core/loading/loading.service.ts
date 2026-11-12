// src/app/core/loading/loading.service.ts
import { computed, Injectable, signal } from '@angular/core';
import { defer, Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class LoadingService {
  private readonly activeRequests = signal(0);
  private readonly message = signal('Synchronizing with Firebase...');

  readonly isLoading = computed(() => this.activeRequests() > 0);
  readonly loadingMessage = computed(() => this.message());

  show(customMessage = 'Synchronizing with Firebase...'): void {
    this.message.set(customMessage);
    this.activeRequests.update(count => count + 1);
  }

  hide(): void {
    this.activeRequests.update(count => Math.max(0, count - 1));
  }

  trackObservable<T>(
    source$: Observable<T>,
    customMessage = 'Fetching data from Firebase...'
  ): Observable<T> {
    return defer(() => {
      this.show(customMessage);
      return source$.pipe(finalize(() => this.hide()));
    });
  }
}
