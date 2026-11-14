// src/app/features/community/components/community-resources.component.ts
import { NgFor, NgIf } from '@angular/common';
import {
  Component,
  computed,
  DestroyRef,
  inject,
  Input,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { BehaviorSubject, combineLatest, EMPTY } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import {
  CommunityAdviceItem,
  CuratedResourceLink,
  PersonaCommunityBundle,
  ResourceCategory,
} from '../../../data-access/models/community-resource.model';
import { LifePersonaId } from '../../../data-access/models/persona.model';
import { CommunityResourcesService } from '../../../data-access/services/community-resources.service';
import { PersonaService } from '../../../data-access/services/persona.service';

@Component({
  selector: 'app-community-resources',
  standalone: true,
  imports: [NgFor, NgIf, RouterLink],
  template: `
    <section
      class="resources-shell"
      aria-label="Curated community resources and advice"
      [style.--resource-accent]="activeAccentColor()"
    >
      <header class="resources-header">
        <div>
          <div class="badge-row">
            <span class="persona-badge" data-testid="resources-persona-badge">
              {{ bundle()?.personaLabel ?? activePersona().label }} Resources
            </span>
            <span
              class="source-pill"
              *ngIf="bundle() as current"
              [class.fallback]="current.source === 'offline-fallback'"
            >
              {{
                current.source === 'dynamic-feed'
                  ? 'Dynamic Persona Feed'
                  : 'Offline Starter Guide'
              }}
            </span>
          </div>
          <h2>{{ bundle()?.headline ?? ('Curated Guidance for ' + activePersona().label) }}</h2>
          <p class="subtitle">
            {{ bundle()?.summary ?? activePersona().summary }}
          </p>
        </div>

        <button
          type="button"
          class="refresh-btn"
          data-testid="resources-refresh-btn"
          [disabled]="loading()"
          (click)="refresh()"
        >
          {{ loading() ? 'Loading...' : 'Refresh Feed' }}
        </button>
      </header>

      <div class="toolbar">
        <label class="search-field">
          <span class="sr-only">Filter curated advice and links</span>
          <input
            type="search"
            data-testid="resources-search-input"
            [value]="searchQuery()"
            (input)="onSearchInput($event)"
            placeholder="Filter advice, topics, or resource providers..."
          />
        </label>

        <div class="filter-pills" role="group" aria-label="Resource categories">
          <button
            *ngFor="let category of availableCategories()"
            type="button"
            class="category-pill"
            [class.active]="selectedCategory() === category"
            [attr.data-testid]="'resource-category-' + category.toLowerCase()"
            [attr.aria-pressed]="selectedCategory() === category"
            (click)="selectCategory(category)"
          >
            {{ category }}
          </button>

          <button
            type="button"
            class="category-pill saved-toggle"
            data-testid="resources-saved-only-btn"
            [class.active]="savedOnly()"
            [attr.aria-pressed]="savedOnly()"
            (click)="toggleSavedOnly()"
          >
            Saved ({{ savedAdviceIds().length }})
          </button>
        </div>
      </div>

      <div
        class="state-card loading-card"
        *ngIf="loading()"
        data-testid="resources-loading"
        role="status"
      >
        Fetching curated advice and links for {{ activePersona().label }}...
      </div>

      <div
        class="state-card error-card"
        *ngIf="errorMessage() as error"
        data-testid="resources-error"
        role="alert"
      >
        <p>{{ error }}</p>
        <div class="error-actions">
          <button
            type="button"
            class="retry-btn"
            data-testid="resources-retry-btn"
            (click)="refresh()"
          >
            Retry Fetch
          </button>
          <button
            type="button"
            class="fallback-btn"
            data-testid="resources-fallback-btn"
            (click)="loadOfflineGuide()"
          >
            Load Offline Guide
          </button>
        </div>
      </div>

      <ng-container *ngIf="!loading() && bundle() as current">
        <div class="content-grid">
          <div class="column-panel">
            <div class="panel-heading">
              <h3>Curated Advice</h3>
              <span class="count-chip">{{ filteredAdvice().length }}</span>
            </div>

            <p
              class="empty-note"
              *ngIf="filteredAdvice().length === 0"
              data-testid="advice-empty-state"
            >
              No advice items match the current filter.
            </p>

            <article
              *ngFor="let item of filteredAdvice()"
              class="advice-card"
              [attr.data-testid]="'advice-card-' + item.id"
            >
              <div class="card-top">
                <span class="tag">{{ item.category }}</span>
                <button
                  type="button"
                  class="bookmark-btn"
                  [class.saved]="isSaved(item.id)"
                  [attr.data-testid]="'bookmark-advice-' + item.id"
                  [attr.aria-pressed]="isSaved(item.id)"
                  (click)="toggleBookmark(item.id)"
                >
                  {{ isSaved(item.id) ? 'Saved ★' : 'Save ☆' }}
                </button>
              </div>

              <h4>{{ item.title }}</h4>
              <p class="guidance">{{ item.guidance }}</p>

              <div class="action-callout">
                <strong>Next Step:</strong> {{ item.actionStep }}
              </div>
            </article>
          </div>

          <div class="column-panel">
            <div class="panel-heading">
              <h3>Recommended Links</h3>
              <span class="count-chip">{{ filteredLinks().length }}</span>
            </div>

            <p
              class="empty-note"
              *ngIf="filteredLinks().length === 0"
              data-testid="links-empty-state"
            >
              No curated links match the current filter.
            </p>

            <ng-container *ngFor="let link of filteredLinks()">
              <a
                *ngIf="link.isExternal"
                class="link-card"
                [href]="link.url"
                target="_blank"
                rel="noopener noreferrer"
                [attr.data-testid]="'resource-link-' + link.id"
              >
                <div class="link-meta">
                  <span class="tag">{{ link.category }}</span>
                  <span class="provider">{{ link.provider }} · External ↗</span>
                </div>
                <h4>{{ link.title }}</h4>
                <p>{{ link.description }}</p>
              </a>

              <a
                *ngIf="!link.isExternal"
                class="link-card"
                [routerLink]="link.url"
                [attr.data-testid]="'resource-link-' + link.id"
              >
                <div class="link-meta">
                  <span class="tag">{{ link.category }}</span>
                  <span class="provider">{{ link.provider }} · In-App →</span>
                </div>
                <h4>{{ link.title }}</h4>
                <p>{{ link.description }}</p>
              </a>
            </ng-container>
          </div>
        </div>
      </ng-container>
    </section>
  `,
  styles: [
    `
      .resources-shell {
        --resource-accent: #3b82f6;
        background: #111827;
        border: 1px solid #334155;
        border-radius: 18px;
        padding: 24px;
        display: grid;
        gap: 20px;
      }

      .resources-header {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        align-items: flex-start;
        gap: 16px;
      }

      .badge-row {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        margin-bottom: 8px;
      }

      .persona-badge,
      .source-pill {
        display: inline-block;
        padding: 4px 10px;
        border-radius: 999px;
        font-size: 0.75rem;
        font-weight: 700;
      }

      .persona-badge {
        background: rgba(59, 130, 246, 0.16);
        color: var(--resource-accent);
        border: 1px solid var(--resource-accent);
      }

      .source-pill {
        background: rgba(16, 185, 129, 0.14);
        color: #34d399;
      }

      .source-pill.fallback {
        background: rgba(245, 158, 11, 0.16);
        color: #fbbf24;
      }

      .resources-header h2 {
        margin: 0 0 6px;
        font-size: 1.3rem;
        color: #f8fafc;
      }

      .subtitle {
        margin: 0;
        color: #cbd5e1;
        max-width: 72ch;
        line-height: 1.5;
      }

      .refresh-btn {
        min-height: 44px;
        padding: 10px 16px;
        border-radius: 10px;
        border: 1px solid var(--resource-accent);
        background: #0f172a;
        color: #f8fafc;
        font-weight: 600;
        cursor: pointer;
      }

      .refresh-btn:disabled {
        opacity: 0.6;
        cursor: wait;
      }

      .toolbar {
        display: grid;
        gap: 12px;
      }

      .search-field input {
        width: 100%;
        min-height: 44px;
        padding: 10px 14px;
        border-radius: 10px;
        border: 1px solid #334155;
        background: #0f172a;
        color: #f8fafc;
        font-size: 0.95rem;
      }

      .sr-only {
        position: absolute;
        width: 1px;
        height: 1px;
        overflow: hidden;
        clip: rect(0, 0, 0, 0);
        white-space: nowrap;
      }

      .filter-pills {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
      }

      .category-pill {
        min-height: 36px;
        padding: 6px 12px;
        border-radius: 999px;
        border: 1px solid #334155;
        background: #0f172a;
        color: #cbd5e1;
        font-size: 0.82rem;
        font-weight: 600;
        cursor: pointer;
      }

      .category-pill.active {
        border-color: var(--resource-accent);
        background: rgba(59, 130, 246, 0.18);
        color: #f8fafc;
      }

      .state-card {
        padding: 16px;
        border-radius: 12px;
        border: 1px solid #334155;
        background: #0f172a;
      }

      .loading-card {
        color: #93c5fd;
      }

      .error-card {
        border-color: #ef4444;
        background: rgba(239, 68, 68, 0.12);
        color: #fecaca;
        display: grid;
        gap: 12px;
      }

      .error-card p {
        margin: 0;
      }

      .error-actions {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
      }

      .retry-btn,
      .fallback-btn {
        min-height: 38px;
        padding: 8px 14px;
        border-radius: 8px;
        border: none;
        font-weight: 600;
        cursor: pointer;
      }

      .retry-btn {
        background: #ef4444;
        color: white;
      }

      .fallback-btn {
        background: #1e293b;
        color: #f8fafc;
        border: 1px solid #475569;
      }

      .content-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(290px, 1fr));
        gap: 18px;
      }

      .column-panel {
        background: #0f172a;
        border: 1px solid #1e293b;
        border-radius: 14px;
        padding: 16px;
        display: grid;
        gap: 12px;
        align-content: start;
      }

      .panel-heading {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 8px;
      }

      .panel-heading h3 {
        margin: 0;
        font-size: 1.05rem;
        color: #f8fafc;
      }

      .count-chip {
        padding: 2px 10px;
        border-radius: 999px;
        background: #1e293b;
        color: #cbd5e1;
        font-size: 0.78rem;
        font-weight: 700;
      }

      .advice-card,
      .link-card {
        display: grid;
        gap: 8px;
        padding: 14px;
        border-radius: 12px;
        border: 1px solid #334155;
        background: #111827;
      }

      .link-card {
        text-decoration: none;
        color: inherit;
        transition: border-color 0.2s ease, transform 0.15s ease;
      }

      .link-card:hover {
        border-color: var(--resource-accent);
        transform: translateY(-1px);
      }

      .card-top,
      .link-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 8px;
      }

      .tag {
        display: inline-block;
        padding: 2px 8px;
        border-radius: 999px;
        background: rgba(56, 189, 248, 0.14);
        color: var(--resource-accent);
        font-size: 0.74rem;
        font-weight: 700;
      }

      .provider {
        font-size: 0.76rem;
        color: #94a3b8;
      }

      .bookmark-btn {
        min-height: 32px;
        padding: 4px 10px;
        border-radius: 8px;
        border: 1px solid #334155;
        background: #0f172a;
        color: #cbd5e1;
        font-size: 0.76rem;
        font-weight: 600;
        cursor: pointer;
      }

      .bookmark-btn.saved {
        border-color: #f59e0b;
        color: #fbbf24;
        background: rgba(245, 158, 11, 0.14);
      }

      .advice-card h4,
      .link-card h4 {
        margin: 0;
        font-size: 1rem;
        color: #f8fafc;
      }

      .guidance,
      .link-card p,
      .empty-note {
        margin: 0;
        color: #cbd5e1;
        font-size: 0.9rem;
        line-height: 1.45;
      }

      .action-callout {
        padding: 10px 12px;
        border-radius: 8px;
        border-left: 3px solid var(--resource-accent);
        background: #0f172a;
        font-size: 0.85rem;
        color: #e2e8f0;
      }
    `,
  ],
})
export class CommunityResourcesComponent {
  private readonly personaService = inject(PersonaService);
  private readonly resourcesService = inject(CommunityResourcesService);
  private readonly destroyRef = inject(DestroyRef);

  private readonly overridePersonaId$ = new BehaviorSubject<
    LifePersonaId | undefined
  >(undefined);
  private readonly refreshTick$ = new BehaviorSubject<number>(0);

  @Input() set personaId(value: LifePersonaId | undefined) {
    this.overridePersonaId$.next(value);
  }

  readonly activePersona = this.personaService.activePersona;
  readonly bundle = signal<PersonaCommunityBundle | null>(null);
  readonly loading = signal<boolean>(true);
  readonly errorMessage = signal<string | null>(null);
  readonly searchQuery = signal<string>('');
  readonly selectedCategory = signal<ResourceCategory | 'All'>('All');
  readonly savedOnly = signal<boolean>(false);
  readonly savedAdviceIds = signal<readonly string[]>([]);

  readonly activeAccentColor = computed<string>(() => {
    const currentId = this.bundle()?.personaId ?? this.activePersona().id;
    return this.personaService.getPersonaById(currentId).accentColor;
  });

  readonly availableCategories = computed<readonly (ResourceCategory | 'All')[]>(
    () => {
      const current = this.bundle();
      if (!current) {
        return ['All'];
      }
      const unique = new Set<ResourceCategory>();
      for (const item of current.advice) {
        unique.add(item.category);
      }
      for (const link of current.links) {
        unique.add(link.category);
      }
      return ['All', ...Array.from(unique)];
    }
  );

  readonly filteredAdvice = computed<readonly CommunityAdviceItem[]>(() => {
    const current = this.bundle();
    if (!current) {
      return [];
    }
    const query = this.searchQuery().trim().toLowerCase();
    const category = this.selectedCategory();
    const onlySaved = this.savedOnly();
    const savedIds = this.savedAdviceIds();

    return current.advice.filter(item => {
      if (category !== 'All' && item.category !== category) {
        return false;
      }
      if (onlySaved && !savedIds.includes(item.id)) {
        return false;
      }
      if (!query) {
        return true;
      }
      return (
        item.title.toLowerCase().includes(query) ||
        item.guidance.toLowerCase().includes(query) ||
        item.actionStep.toLowerCase().includes(query) ||
        item.category.toLowerCase().includes(query)
      );
    });
  });

  readonly filteredLinks = computed<readonly CuratedResourceLink[]>(() => {
    const current = this.bundle();
    if (!current) {
      return [];
    }
    const query = this.searchQuery().trim().toLowerCase();
    const category = this.selectedCategory();

    return current.links.filter(link => {
      if (category !== 'All' && link.category !== category) {
        return false;
      }
      if (!query) {
        return true;
      }
      return (
        link.title.toLowerCase().includes(query) ||
        link.description.toLowerCase().includes(query) ||
        link.provider.toLowerCase().includes(query) ||
        link.category.toLowerCase().includes(query)
      );
    });
  });

  constructor() {
    combineLatest([
      this.personaService.activePersona$,
      this.overridePersonaId$,
      this.refreshTick$,
    ])
      .pipe(
        map(([active, override]) => override ?? active.id),
        switchMap(targetId => {
          this.loading.set(true);
          this.errorMessage.set(null);
          return this.resourcesService.getResourcesForPersona(targetId).pipe(
            catchError(() => {
              this.errorMessage.set(
                `Unable to load live curated resources for ${targetId}. Retry or open the offline guide.`
              );
              this.loading.set(false);
              return EMPTY;
            })
          );
        }),
        takeUntilDestroyed()
      )
      .subscribe(fetched => {
        this.bundle.set(fetched);
        this.selectedCategory.set('All');
        this.loading.set(false);
      });
  }

  refresh(): void {
    this.refreshTick$.next(this.refreshTick$.value + 1);
  }

  loadOfflineGuide(): void {
    const targetId = this.overridePersonaId$.value ?? this.activePersona().id;
    this.errorMessage.set(null);
    this.resourcesService
      .getOfflineFallback(targetId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(fallback => {
        this.bundle.set(fallback);
        this.selectedCategory.set('All');
        this.loading.set(false);
      });
  }

  onSearchInput(event: Event): void {
    const input = event.target as HTMLInputElement | null;
    this.searchQuery.set(input?.value ?? '');
  }

  selectCategory(category: ResourceCategory | 'All'): void {
    this.selectedCategory.set(category);
  }

  toggleSavedOnly(): void {
    this.savedOnly.update(current => !current);
  }

  toggleBookmark(adviceId: string): void {
    this.savedAdviceIds.update(current =>
      current.includes(adviceId)
        ? current.filter(id => id !== adviceId)
        : [...current, adviceId]
    );
  }

  isSaved(adviceId: string): boolean {
    return this.savedAdviceIds().includes(adviceId);
  }
}
