// src/app/features/dashboard/components/persona-switcher.component.ts
import { NgFor, NgIf } from '@angular/common';
import { Component, EventEmitter, inject, Output } from '@angular/core';
import { RouterLink } from '@angular/router';
import {
  LifePersonaId,
  PersonaDefinition,
} from '../../../data-access/models/persona.model';
import { PersonaService } from '../../../data-access/services/persona.service';
import { PersonaStageIconComponent } from './persona-stage-icon.component';

@Component({
  selector: 'app-persona-switcher',
  standalone: true,
  imports: [NgFor, NgIf, RouterLink, PersonaStageIconComponent],
  template: `
    <section
      class="persona-card"
      aria-label="Life stage persona switcher"
      [style.--persona-accent]="activePersona().accentColor"
      [style.--persona-glow]="activePersona().badgeBackground"
    >
      <header class="persona-header">
        <div>
          <p class="eyebrow">Interactive Life Stage Navigator</p>
          <h2>Choose Your Active Persona View</h2>
        </div>

        <div class="nav-controls">
          <button
            type="button"
            class="cycle-btn secondary"
            data-testid="persona-prev-btn"
            aria-label="Switch to previous life persona"
            (click)="previousPersona()"
          >
            ← Prev
          </button>
          <button
            type="button"
            class="cycle-btn"
            data-testid="persona-cycle-btn"
            aria-label="Switch to next life persona"
            (click)="cyclePersona()"
          >
            Next Stage ({{ activePersona().label }}) →
          </button>
        </div>
      </header>

      <div
        class="journey-rail"
        aria-label="Life stage progression"
        data-testid="persona-journey-rail"
      >
        <button
          *ngFor="let persona of personas; let idx = index"
          type="button"
          class="rail-step"
          [class.active]="persona.id === activePersona().id"
          [class.passed]="idx < activePersonaIndex()"
          [style.--step-accent]="persona.accentColor"
          [attr.aria-label]="'Jump to ' + persona.label + ' view'"
          (click)="selectPersona(persona.id)"
        >
          <span class="rail-dot">{{ idx + 1 }}</span>
          <span class="rail-name">{{ persona.label }}</span>
        </button>
      </div>

      <div
        class="persona-grid"
        role="group"
        aria-label="Life personas"
        (keydown)="onGridKeydown($event)"
      >
        <button
          *ngFor="let persona of personas"
          type="button"
          class="persona-pill"
          [class.active]="persona.id === activePersona().id"
          [attr.data-testid]="'persona-option-' + persona.id"
          [attr.aria-pressed]="persona.id === activePersona().id"
          [style.--pill-accent]="persona.accentColor"
          [style.--pill-bg]="persona.badgeBackground"
          (click)="selectPersona(persona.id)"
        >
          <div class="pill-top">
            <app-persona-stage-icon
              [personaId]="persona.id"
              [active]="persona.id === activePersona().id"
              [size]="44"
            />
            <span class="pill-badge">
              {{ persona.id === activePersona().id ? 'ACTIVE' : persona.shortCode }}
            </span>
          </div>

          <div class="pill-copy">
            <span class="pill-title">{{ persona.label }}</span>
            <span class="pill-age">{{ persona.ageRange }}</span>
          </div>
        </button>
      </div>

      <article
        class="stage-view"
        *ngIf="activePersona() as active"
        [attr.data-testid]="'persona-stage-view-' + active.id"
      >
        <div class="stage-banner">
          <div class="stage-hero-intro">
            <app-persona-stage-icon
              [personaId]="active.id"
              [active]="true"
              [size]="58"
            />
            <div>
              <span
                class="stage-chip"
                [style.background]="active.badgeBackground"
                [style.color]="active.accentColor"
              >
                {{ active.label }} View · {{ active.ageRange }}
              </span>
              <h3>{{ active.tagline }}</h3>
              <p class="stage-summary">{{ active.summary }}</p>
            </div>
          </div>

          <div class="progress-ring-card">
            <span class="progress-label">Stage Focus</span>
            <strong class="progress-value">{{ completionProgress() }}%</strong>
          </div>
        </div>

        <div class="metrics-row">
          <div class="metric-box" *ngFor="let metric of active.metrics">
            <span class="metric-label">{{ metric.label }}</span>
            <strong class="metric-value">{{ metric.value }}</strong>
          </div>
        </div>

        <div class="stage-columns">
          <div class="checklist-panel">
            <h4>Priority Actions for {{ active.label }}</h4>
            <button
              *ngFor="let task of active.focusAreas"
              type="button"
              class="task-row"
              [class.completed]="isCompleted(active.id, task)"
              [attr.aria-pressed]="isCompleted(active.id, task)"
              (click)="toggleTask(active.id, task)"
            >
              <span class="checkbox-indicator">
                {{ isCompleted(active.id, task) ? '✓' : '' }}
              </span>
              <span>{{ task }}</span>
            </button>
          </div>

          <div class="shortcuts-panel">
            <h4>Tailored Modules</h4>
            <a
              *ngFor="let module of active.recommendedModules"
              [routerLink]="module.route"
              class="shortcut-link"
            >
              <div>
                <strong>{{ module.title }}</strong>
                <span>{{ module.subtitle }}</span>
              </div>
              <span class="arrow" aria-hidden="true">→</span>
            </a>

            <a routerLink="/resources" class="shortcut-link resources-shortcut">
              <div>
                <strong>{{ active.label }} Community Advice</strong>
                <span>Browse curated guides &amp; external resources</span>
              </div>
              <span class="arrow" aria-hidden="true">↗</span>
            </a>

            <div class="cta-footer">
              <span class="active-cta-badge">{{ active.primaryCta }}</span>
            </div>
          </div>
        </div>
      </article>
    </section>
  `,
  styles: [
    `
      .persona-card {
        --persona-accent: #3b82f6;
        --persona-glow: rgba(59, 130, 246, 0.16);
        background: radial-gradient(
            circle at top right,
            var(--persona-glow),
            transparent 48%
          ),
          #111827;
        border: 1px solid #334155;
        border-radius: 18px;
        padding: 24px;
        display: grid;
        gap: 20px;
        transition: border-color 0.3s ease;
      }

      .persona-header {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        align-items: center;
        gap: 12px;
      }

      .eyebrow {
        margin: 0 0 4px;
        text-transform: uppercase;
        letter-spacing: 0.08em;
        font-size: 0.72rem;
        color: #94a3b8;
      }

      .persona-header h2 {
        margin: 0;
        font-size: 1.35rem;
        color: #f8fafc;
      }

      .nav-controls {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
      }

      .cycle-btn {
        min-height: 44px;
        padding: 10px 16px;
        border-radius: 10px;
        border: 1px solid var(--persona-accent);
        background: rgba(15, 23, 42, 0.92);
        color: #f8fafc;
        font-weight: 600;
        cursor: pointer;
        transition: background 0.2s ease, transform 0.15s ease, box-shadow 0.2s ease;
      }

      .cycle-btn.secondary {
        border-color: #475569;
        color: #cbd5e1;
      }

      .cycle-btn:hover {
        background: #1e293b;
        transform: translateY(-1px);
        box-shadow: 0 6px 18px rgba(15, 23, 42, 0.45);
      }

      .journey-rail {
        display: grid;
        grid-template-columns: repeat(6, minmax(0, 1fr));
        gap: 8px;
        padding: 10px 12px;
        border-radius: 14px;
        background: #0f172a;
        border: 1px solid #1e293b;
      }

      .rail-step {
        --step-accent: #3b82f6;
        border: none;
        background: transparent;
        color: #94a3b8;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 6px;
        padding: 6px 4px;
        border-radius: 8px;
        cursor: pointer;
        font-size: 0.78rem;
        font-weight: 600;
        transition: background 0.2s ease, color 0.2s ease;
      }

      .rail-step:hover {
        background: rgba(148, 163, 184, 0.1);
        color: #f8fafc;
      }

      .rail-dot {
        width: 22px;
        height: 22px;
        border-radius: 999px;
        border: 1px solid #475569;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        font-size: 0.72rem;
        flex-shrink: 0;
        transition: background 0.25s ease, border-color 0.25s ease, transform 0.2s ease;
      }

      .rail-step.passed .rail-dot {
        border-color: var(--step-accent);
        color: var(--step-accent);
      }

      .rail-step.active {
        color: #f8fafc;
        background: rgba(255, 255, 255, 0.06);
      }

      .rail-step.active .rail-dot {
        background: var(--step-accent);
        border-color: var(--step-accent);
        color: #020817;
        transform: scale(1.1);
      }

      @media (max-width: 720px) {
        .rail-name {
          display: none;
        }
      }

      .persona-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
        gap: 12px;
      }

      .persona-pill {
        --pill-accent: #3b82f6;
        --pill-bg: rgba(59, 130, 246, 0.16);
        min-height: 108px;
        padding: 14px;
        border-radius: 16px;
        border: 1px solid #334155;
        background: #0f172a;
        color: var(--pill-accent);
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        gap: 10px;
        cursor: pointer;
        text-align: left;
        transition: border-color 0.25s ease, background 0.25s ease,
          transform 0.2s ease, box-shadow 0.25s ease;
      }

      .persona-pill:hover {
        border-color: var(--pill-accent);
        transform: translateY(-2px);
      }

      .persona-pill.active {
        border: 2px solid var(--pill-accent);
        background: var(--pill-bg);
        box-shadow: 0 8px 24px rgba(2, 8, 23, 0.45);
      }

      .pill-top {
        width: 100%;
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        gap: 8px;
      }

      .pill-badge {
        font-size: 0.68rem;
        font-weight: 700;
        letter-spacing: 0.06em;
        padding: 3px 8px;
        border-radius: 999px;
        background: rgba(255, 255, 255, 0.1);
        color: var(--pill-accent);
      }

      .pill-copy {
        display: grid;
        gap: 2px;
      }

      .pill-title {
        font-size: 1rem;
        font-weight: 700;
        color: #f8fafc;
      }

      .pill-age {
        font-size: 0.76rem;
        color: #94a3b8;
      }

      .stage-view {
        background: #0f172a;
        border: 1px solid #1e293b;
        border-left: 4px solid var(--persona-accent);
        border-radius: 14px;
        padding: 20px;
        display: grid;
        gap: 18px;
        animation: stageReveal 0.28s cubic-bezier(0.22, 1, 0.36, 1);
      }

      @keyframes stageReveal {
        from {
          opacity: 0.65;
          transform: translateY(6px);
        }
        to {
          opacity: 1;
          transform: translateY(0);
        }
      }

      .stage-banner {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        align-items: flex-start;
        gap: 16px;
      }

      .stage-hero-intro {
        display: flex;
        align-items: flex-start;
        gap: 16px;
        color: var(--persona-accent);
      }

      .stage-chip {
        display: inline-block;
        padding: 4px 10px;
        border-radius: 999px;
        font-size: 0.78rem;
        font-weight: 700;
        margin-bottom: 8px;
      }

      .stage-banner h3 {
        margin: 0 0 6px;
        font-size: 1.15rem;
        color: #f8fafc;
      }

      .stage-summary {
        margin: 0;
        color: #cbd5e1;
        max-width: 66ch;
        line-height: 1.5;
      }

      .progress-ring-card {
        min-width: 112px;
        padding: 12px 16px;
        border-radius: 12px;
        background: #111827;
        border: 1px solid #334155;
        text-align: center;
      }

      .progress-label {
        display: block;
        font-size: 0.75rem;
        color: #94a3b8;
      }

      .progress-value {
        font-size: 1.4rem;
        color: var(--persona-accent);
      }

      .metrics-row {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
        gap: 12px;
      }

      .metric-box {
        background: #111827;
        border: 1px solid #1e293b;
        border-radius: 12px;
        padding: 12px 14px;
        display: grid;
        gap: 4px;
      }

      .metric-label {
        font-size: 0.78rem;
        color: #94a3b8;
      }

      .metric-value {
        font-size: 1.05rem;
        color: #f8fafc;
      }

      .stage-columns {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
        gap: 16px;
      }

      .checklist-panel,
      .shortcuts-panel {
        background: #111827;
        border: 1px solid #1e293b;
        border-radius: 12px;
        padding: 16px;
        display: grid;
        gap: 10px;
        align-content: start;
      }

      .checklist-panel h4,
      .shortcuts-panel h4 {
        margin: 0 0 4px;
        font-size: 0.95rem;
        color: #f8fafc;
      }

      .task-row {
        min-height: 44px;
        padding: 10px 12px;
        border-radius: 10px;
        border: 1px solid #334155;
        background: #0f172a;
        color: #e2e8f0;
        display: flex;
        align-items: center;
        gap: 10px;
        text-align: left;
        cursor: pointer;
      }

      .task-row.completed {
        border-color: #10b981;
        background: rgba(16, 185, 129, 0.12);
        color: #a7f3d0;
      }

      .checkbox-indicator {
        width: 22px;
        height: 22px;
        border-radius: 6px;
        border: 1px solid #475569;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
        font-weight: 700;
      }

      .task-row.completed .checkbox-indicator {
        background: #10b981;
        border-color: #10b981;
        color: #022c22;
      }

      .shortcut-link {
        min-height: 52px;
        padding: 10px 14px;
        border-radius: 10px;
        border: 1px solid #334155;
        background: #0f172a;
        text-decoration: none;
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 12px;
        transition: border-color 0.2s ease, transform 0.15s ease;
      }

      .shortcut-link:hover {
        border-color: var(--persona-accent);
        transform: translateX(2px);
      }

      .shortcut-link strong {
        display: block;
        color: #f8fafc;
        font-size: 0.92rem;
      }

      .shortcut-link span {
        font-size: 0.8rem;
        color: #94a3b8;
      }

      .arrow {
        color: var(--persona-accent) !important;
        font-size: 1.1rem !important;
        font-weight: 700;
      }

      .cta-footer {
        padding-top: 4px;
      }

      .active-cta-badge {
        display: inline-block;
        padding: 8px 12px;
        border-radius: 8px;
        background: rgba(59, 130, 246, 0.14);
        color: var(--persona-accent);
        font-size: 0.85rem;
        font-weight: 600;
      }
    `,
  ],
})
export class PersonaSwitcherComponent {
  private readonly personaService = inject(PersonaService);

  @Output() readonly personaChanged = new EventEmitter<PersonaDefinition>();

  readonly personas = this.personaService.personas;
  readonly activePersona = this.personaService.activePersona;
  readonly completionProgress = this.personaService.activeCompletionProgress;

  activePersonaIndex(): number {
    const activeId = this.activePersona().id;
    const index = this.personas.findIndex(p => p.id === activeId);
    return index >= 0 ? index : 0;
  }

  selectPersona(personaId: LifePersonaId): void {
    const selected = this.personaService.switchPersona(personaId);
    this.personaChanged.emit(selected);
  }

  cyclePersona(): void {
    const selected = this.personaService.cycleNextPersona();
    this.personaChanged.emit(selected);
  }

  previousPersona(): void {
    const selected = this.personaService.cyclePreviousPersona();
    this.personaChanged.emit(selected);
  }

  onGridKeydown(event: KeyboardEvent): void {
    if (event.key === 'ArrowRight' || event.key === 'ArrowDown') {
      event.preventDefault();
      this.cyclePersona();
    } else if (event.key === 'ArrowLeft' || event.key === 'ArrowUp') {
      event.preventDefault();
      this.previousPersona();
    } else if (event.key === 'Home') {
      event.preventDefault();
      this.selectPersona(this.personas[0].id);
    } else if (event.key === 'End') {
      event.preventDefault();
      this.selectPersona(this.personas[this.personas.length - 1].id);
    }
  }

  toggleTask(personaId: LifePersonaId, taskTitle: string): void {
    this.personaService.toggleFocusTask(personaId, taskTitle);
  }

  isCompleted(personaId: LifePersonaId, taskTitle: string): boolean {
    return this.personaService.isTaskCompleted(personaId, taskTitle);
  }
}
