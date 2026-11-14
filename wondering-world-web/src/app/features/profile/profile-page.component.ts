// src/app/features/profile/profile-page.component.ts
import { CommonModule, DOCUMENT } from '@angular/common';
import {
  Component,
  DestroyRef,
  effect,
  inject,
  OnInit,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import {
  ReportReason,
  ReportTargetType,
} from '../../data-access/models/content-report.model';
import { AvatarStudioSavedEvent } from '../../data-access/models/photo-studio.model';
import { UserProfile } from '../../data-access/models/user-profile.model';
import { ContentReportingService } from '../../data-access/services/content-reporting.service';
import { ProfileCustomizationService } from '../../data-access/services/profile-customization.service';
import { ProfileService } from '../../data-access/services/profile.service';
import {
  PhotoStudioComponent,
  STUDIO_FILTERS,
  STUDIO_FRAMES,
} from './components/photo-studio.component';
import { ProfileClockWidgetComponent } from './components/profile-clock-widget.component';
import { ProfileCustomizerComponent } from './components/profile-customizer.component';
import { ProfileGameWidgetComponent } from './components/profile-game-widget.component';
import { ProfileWeatherWidgetComponent } from './components/profile-weather-widget.component';
import { DailyMotivationalQuoteWidgetComponent } from '../dashboard/components/daily-motivational-quote-widget.component';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    PhotoStudioComponent,
    ProfileCustomizerComponent,
    ProfileClockWidgetComponent,
    ProfileWeatherWidgetComponent,
    ProfileGameWidgetComponent,
    DailyMotivationalQuoteWidgetComponent,
  ],
  template: `
    <section
      *ngIf="user"
      class="profile-scope"
      data-profile-scope="active"
      [attr.data-theme-id]="activeTheme().id"
      [style.background]="activeTheme().surfaceBackground"
      [style.color]="activeTheme().textColor"
      [style.font-family]="activeTheme().fontFamily"
      [style.--profile-card]="activeTheme().cardBackground"
      [style.--profile-border]="activeTheme().borderColor"
      [style.--profile-accent]="activeTheme().accentColor"
    >
      <header class="profile-top">
        <div>
          <h1>Profile</h1>
          <p class="active-theme-pill">
            Theme: {{ activeTheme().name }}
          </p>
        </div>
      </header>

      <div class="profile-card">
        <img
          [src]="user.avatarUrl"
          [alt]="user.displayName"
          [style.filter]="activeFilterCss()"
          [style.border]="activeFrameBorder()"
          [style.box-shadow]="activeFrameShadow()"
          [class.polaroid-avatar]="user.avatarFrameId === 'polaroid'"
        />
        <div>
          <h2>{{ user.displayName }}</h2>
          <p>{{ user.email }}</p>
          <p>{{ user.bio }}</p>
        </div>
      </div>

      <app-profile-customizer />

      @if (widgets().length > 0) {
        <section class="widgets-board" aria-label="Profile interactive widgets">
          <div class="widgets-header">
            <h3>Interactive Profile Widgets</h3>
            <span>{{ widgets().length }} active</span>
          </div>

          <div class="widgets-grid">
            @for (widget of widgets(); track widget.id) {
              <article
                class="widget-card"
                [attr.data-testid]="'profile-widget-' + widget.type"
              >
                <div class="widget-titlebar">
                  <strong>{{ widget.title }}</strong>
                  <button
                    type="button"
                    class="widget-close-btn"
                    aria-label="Remove widget"
                    (click)="removeWidget(widget.id)"
                  >
                    ×
                  </button>
                </div>

                @if (widget.type === 'clock') {
                  <app-profile-clock-widget [widget]="widget" />
                } @else if (widget.type === 'weather') {
                  <app-profile-weather-widget [widget]="widget" />
                } @else if (widget.type === 'game') {
                  <app-profile-game-widget [widget]="widget" />
                } @else if (widget.type === 'quote') {
                  <app-daily-motivational-quote-widget [widget]="widget" />
                }
              </article>
            }
          </div>
        </section>
      }

      <app-photo-studio (avatarSaved)="onAvatarSaved($event)" />

      <div class="report-card">
        <h3>Report a Post or Profile for Administrator Review</h3>
        <div class="report-controls">
          <label>
            Target Type
            <select [(ngModel)]="targetType">
              <option value="post">Post</option>
              <option value="comment">Comment</option>
              <option value="profile">Profile</option>
            </select>
          </label>

          <label>
            Target Post / Profile Name
            <input
              type="text"
              [(ngModel)]="targetLabel"
              placeholder="e.g. Community Post #42 or @username"
            />
          </label>

          <label>
            Reason
            <select [(ngModel)]="selectedReason">
              <option
                *ngFor="let reason of reportingService.reasonOptions"
                [value]="reason"
              >
                {{ reason }}
              </option>
            </select>
          </label>

          <label>
            Additional Details
            <input
              type="text"
              [(ngModel)]="details"
              placeholder="Details for platform administrators..."
            />
          </label>

          <button type="button" (click)="submitFlag()">
            Flag for Admin Review
          </button>
        </div>

        <p class="confirmation" *ngIf="confirmationMessage">
          {{ confirmationMessage }}
        </p>

        <div class="admin-queue">
          <h4>
            Administrator Flagged Queue ({{
              reportingService.pendingAdminReviewCount()
            }}
            Pending)
          </h4>
          <div
            class="queue-item"
            *ngFor="let report of reportingService.reports()"
          >
            <div>
              <strong>[{{ report.targetType | uppercase }}]</strong>
              {{ report.targetLabel }} —
              <span class="reason-badge">{{ report.reason }}</span>
              <span class="status-badge">{{ report.status }}</span>
              <p class="notes">{{ report.details }}</p>
            </div>
            <div
              class="admin-actions"
              *ngIf="report.status === 'flagged_for_review'"
            >
              <button
                type="button"
                class="approve-btn"
                (click)="reportingService.resolveReport(report.id, 'approved')"
              >
                Approve
              </button>
              <button
                type="button"
                class="remove-btn"
                (click)="reportingService.resolveReport(report.id, 'removed')"
              >
                Remove
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>
  `,
  styles: [
    `
      .profile-scope {
        border-radius: 20px;
        padding: 24px;
        border: 1px solid var(--profile-border, #334155);
        transition: background 0.3s ease, border-color 0.3s ease;
      }

      .profile-top {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .profile-top h1 {
        margin: 0;
      }

      .active-theme-pill {
        margin: 6px 0 0;
        font-size: 0.85rem;
        color: var(--profile-accent, #38bdf8);
        font-weight: 600;
      }

      .profile-card,
      .report-card,
      .widget-card {
        margin-top: 20px;
        background: var(--profile-card, #1e293b);
        border: 1px solid var(--profile-border, #334155);
        border-radius: 16px;
        padding: 24px;
      }

      .widgets-board {
        margin-top: 24px;
      }

      .widgets-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .widgets-header h3 {
        margin: 0;
      }

      .widgets-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(290px, 1fr));
        gap: 16px;
      }

      .widget-card {
        margin-top: 12px;
        padding: 18px;
        display: grid;
        gap: 12px;
        align-content: start;
      }

      .widget-titlebar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 8px;
      }

      .widget-close-btn {
        padding: 2px 10px;
        background: transparent;
        border: 1px solid #475569;
        color: #cbd5e1;
      }

      .profile-card {
        display: flex;
        gap: 20px;
        align-items: center;
        max-width: 640px;
      }

      img {
        width: 88px;
        height: 88px;
        border-radius: 50%;
        object-fit: cover;
        transition: filter 0.25s ease, border 0.25s ease, box-shadow 0.25s ease;
      }

      img.polaroid-avatar {
        border-radius: 14px;
      }

      .report-controls {
        display: grid;
        gap: 12px;
        max-width: 520px;
      }

      label {
        display: grid;
        gap: 6px;
        font-size: 0.9rem;
      }

      input,
      select {
        padding: 10px;
        border-radius: 8px;
        border: 1px solid #475569;
        background: #0f172a;
        color: #f8fafc;
      }

      button {
        padding: 10px 14px;
        border: none;
        border-radius: 8px;
        background: #e11d48;
        color: white;
        font-weight: 600;
        cursor: pointer;
      }

      .confirmation {
        margin-top: 12px;
        color: #34d399;
        font-weight: 600;
      }

      .admin-queue {
        margin-top: 20px;
        display: grid;
        gap: 10px;
      }

      .queue-item {
        background: #0f172a;
        border: 1px solid #334155;
        border-radius: 10px;
        padding: 12px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 12px;
      }

      .reason-badge {
        color: #fbbf24;
        font-weight: 700;
      }

      .status-badge {
        margin-left: 8px;
        font-size: 0.8rem;
        color: #38bdf8;
      }

      .notes {
        margin: 4px 0 0;
        color: #94a3b8;
        font-size: 0.85rem;
      }

      .admin-actions {
        display: flex;
        gap: 8px;
      }

      .approve-btn {
        background: #10b981;
      }

      .remove-btn {
        background: #ef4444;
      }
    `,
  ],
})
export class ProfilePageComponent implements OnInit {
  private readonly profileService = inject(ProfileService);
  private readonly customizationService = inject(ProfileCustomizationService);
  private readonly documentRef = inject(DOCUMENT);
  private readonly destroyRef = inject(DestroyRef);
  readonly reportingService = inject(ContentReportingService);

  readonly activeTheme = this.customizationService.activeTheme;
  readonly widgets = this.customizationService.widgets;

  user?: UserProfile;
  targetType: ReportTargetType = 'post';
  targetLabel = '';
  selectedReason: ReportReason = 'harassment';
  details = '';
  confirmationMessage = '';
  private customStyleElement?: HTMLStyleElement;

  constructor() {
    effect(() => {
      const css = this.customizationService.sanitizedCss();
      if (!this.customStyleElement) {
        this.customStyleElement = this.documentRef.createElement('style');
        this.customStyleElement.setAttribute('data-profile-custom-css', 'true');
        this.documentRef.head.appendChild(this.customStyleElement);
      }
      this.customStyleElement.textContent = css;
    });

    this.destroyRef.onDestroy(() => {
      this.customStyleElement?.remove();
    });
  }

  ngOnInit(): void {
    this.profileService
      .observeCurrentUser()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(user => {
        this.user = user;
      });
  }

  removeWidget(widgetId: string): void {
    this.customizationService.removeWidget(widgetId);
  }

  activeFilterCss(): string {
    const id = this.user?.avatarFilterId ?? 'original';
    return STUDIO_FILTERS.find(f => f.id === id)?.cssFilter ?? 'none';
  }

  activeFrameBorder(): string {
    const id = this.user?.avatarFrameId ?? 'none';
    return STUDIO_FRAMES.find(f => f.id === id)?.borderStyle ?? '2px solid #334155';
  }

  activeFrameShadow(): string {
    const id = this.user?.avatarFrameId ?? 'none';
    return STUDIO_FRAMES.find(f => f.id === id)?.glowShadow ?? 'none';
  }

  onAvatarSaved(event: AvatarStudioSavedEvent): void {
    if (this.user) {
      this.user = {
        ...this.user,
        avatarUrl: event.avatarUrl,
        avatarFilterId: event.filterId,
        avatarFrameId: event.frameId,
      };
    }
  }

  async submitFlag(): Promise<void> {
    const label = this.targetLabel.trim() || `Flagged ${this.targetType}`;
    await this.reportingService.submitReport(
      `${this.targetType}_${Date.now()}`,
      this.targetType,
      label,
      this.selectedReason,
      this.details
    );
    this.confirmationMessage = `Flagged ${this.targetType} ("${label}") for administrator review (${this.selectedReason}).`;
    this.targetLabel = '';
    this.details = '';
  }
}
