// src/app/core/loading/loading-overlay.component.ts
import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { LoadingService } from './loading.service';

@Component({
  selector: 'app-loading-overlay',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div
      class="loading-overlay-backdrop"
      *ngIf="loadingService.isLoading()"
      role="status"
      aria-live="polite"
    >
      <div class="loading-card">
        <div class="spinner-ring"></div>
        <p class="loading-title">{{ loadingService.loadingMessage() }}</p>
        <span class="loading-subtitle">Firebase & API Request Active</span>
      </div>
    </div>
  `,
  styles: [
    `
      .loading-overlay-backdrop {
        position: fixed;
        inset: 0;
        z-index: 9999;
        background: rgba(15, 23, 42, 0.78);
        backdrop-filter: blur(6px);
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .loading-card {
        background: #1e293b;
        border: 1px solid #38bdf8;
        border-radius: 16px;
        padding: 24px 32px;
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 12px;
        box-shadow: 0 20px 40px rgba(0, 0, 0, 0.45);
      }

      .spinner-ring {
        width: 48px;
        height: 48px;
        border: 4px solid rgba(56, 189, 248, 0.25);
        border-top-color: #38bdf8;
        border-radius: 50%;
        animation: spin 0.85s linear infinite;
      }

      .loading-title {
        margin: 0;
        color: #f8fafc;
        font-weight: 700;
        font-size: 1rem;
      }

      .loading-subtitle {
        color: #94a3b8;
        font-size: 0.82rem;
      }

      @keyframes spin {
        to {
          transform: rotate(360deg);
        }
      }
    `,
  ],
})
export class LoadingOverlayComponent {
  readonly loadingService = inject(LoadingService);
}
