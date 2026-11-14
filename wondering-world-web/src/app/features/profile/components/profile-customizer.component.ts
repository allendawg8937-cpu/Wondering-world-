// src/app/features/profile/components/profile-customizer.component.ts
import { Component, inject, signal } from '@angular/core';
import {
  ProfileThemeId,
  ProfileWidgetType,
} from '../../../data-access/models/profile-customization.model';
import { ProfileCustomizationService } from '../../../data-access/services/profile-customization.service';

type CustomizerTab = 'themes' | 'css' | 'widgets';

const SAMPLE_RETRO_CSS = `.profile-card {
  border-width: 2px;
  border-style: dashed;
  box-shadow: 0 0 24px rgba(244, 63, 94, 0.35);
}
h1 {
  letter-spacing: 0.08em;
  text-transform: uppercase;
}`;

@Component({
  selector: 'app-profile-customizer',
  standalone: true,
  template: `
    <section class="customizer-card" aria-label="Profile customization studio">
      <header class="customizer-header">
        <div>
          <p class="eyebrow">Profile Personalization Studio</p>
          <h3>Themes, Sanitized Custom CSS &amp; Interactive Widgets</h3>
        </div>

        <div class="tab-bar" role="tablist">
          <button
            type="button"
            role="tab"
            class="tab-btn"
            [class.active]="activeTab() === 'themes'"
            [attr.aria-selected]="activeTab() === 'themes'"
            data-testid="customizer-tab-themes"
            (click)="activeTab.set('themes')"
          >
            Themes ({{ themes.length }})
          </button>
          <button
            type="button"
            role="tab"
            class="tab-btn"
            [class.active]="activeTab() === 'css'"
            [attr.aria-selected]="activeTab() === 'css'"
            data-testid="customizer-tab-css"
            (click)="activeTab.set('css')"
          >
            Custom CSS
          </button>
          <button
            type="button"
            role="tab"
            class="tab-btn"
            [class.active]="activeTab() === 'widgets'"
            [attr.aria-selected]="activeTab() === 'widgets'"
            data-testid="customizer-tab-widgets"
            (click)="activeTab.set('widgets')"
          >
            Widgets ({{ widgets().length }})
          </button>
        </div>
      </header>

      <!-- Pre-designed Themes Library -->
      @if (activeTab() === 'themes') {
        <div class="theme-grid" data-testid="theme-library-grid">
          @for (theme of themes; track theme.id) {
            <button
              type="button"
              class="theme-tile"
              [class.selected]="selectedThemeId() === theme.id"
              [attr.data-testid]="'theme-option-' + theme.id"
              [attr.aria-pressed]="selectedThemeId() === theme.id"
              (click)="chooseTheme(theme.id)"
            >
              <div class="swatches">
                @for (swatch of theme.swatchColors; track swatch) {
                  <span class="swatch-dot" [style.background]="swatch"></span>
                }
              </div>
              <strong>{{ theme.name }}</strong>
              <span class="theme-tagline">{{ theme.tagline }}</span>
            </button>
          }
        </div>
      }

      <!-- Advanced User Custom CSS + Robust Sanitizer -->
      @if (activeTab() === 'css') {
        <div class="css-editor-panel" data-testid="custom-css-panel">
          <p class="security-note">
            Custom CSS is strictly sanitized using an allowlist and scoped to your
            profile container. Scripts, tags, <code>@import</code>,
            <code>url()</code>, <code>expression()</code>, and fixed overlays are
            automatically stripped.
          </p>

          <textarea
            rows="6"
            class="css-textarea"
            [value]="cssDraft()"
            (input)="onCssInput($event)"
            placeholder=".profile-card { border-radius: 24px; box-shadow: 0 0 20px rgba(56, 189, 248, 0.35); }"
            aria-label="Custom profile CSS"
            data-testid="custom-css-textarea"
          ></textarea>

          <div class="css-actions">
            <button
              type="button"
              class="primary-btn"
              data-testid="apply-custom-css-btn"
              (click)="applyCustomCss()"
            >
              Sanitize &amp; Apply CSS
            </button>

            <label class="upload-btn">
              Upload .css File
              <input
                type="file"
                accept=".css,text/css"
                data-testid="upload-custom-css-input"
                (change)="onCssFileSelected($event)"
              />
            </label>

            <button
              type="button"
              class="secondary-btn"
              data-testid="load-sample-css-btn"
              (click)="loadSampleCss()"
            >
              Load Retro Preset
            </button>

            <button
              type="button"
              class="secondary-btn"
              data-testid="clear-custom-css-btn"
              (click)="clearCss()"
            >
              Clear CSS
            </button>
          </div>

          <div
            class="sanitization-status"
            [class.warn]="sanitizationReport().blockedPatterns.length > 0"
            data-testid="css-sanitization-report"
            role="status"
          >
            <span>
              Active scoped rules:
              <strong>{{ sanitizationReport().ruleCount }}</strong>
            </span>
            @if (sanitizationReport().blockedPatterns.length > 0) {
              <p class="blocked-list" data-testid="css-blocked-patterns">
                Blocked for security:
                {{ sanitizationReport().blockedPatterns.join(', ') }}
              </p>
            }
          </div>
        </div>
      }

      <!-- Interactive Profile Widgets Manager -->
      @if (activeTab() === 'widgets') {
        <div class="widgets-manager" data-testid="widgets-manager-panel">
          <div class="add-widget-bar">
            <input
              type="text"
              class="title-input"
              [value]="widgetTitleDraft()"
              (input)="onWidgetTitleInput($event)"
              placeholder="Optional custom widget title..."
              aria-label="Custom widget title"
              data-testid="widget-title-input"
            />
            <button
              type="button"
              class="add-btn"
              data-testid="add-widget-clock-btn"
              (click)="addWidget('clock')"
            >
              + Add Clock
            </button>
            <button
              type="button"
              class="add-btn"
              data-testid="add-widget-weather-btn"
              (click)="addWidget('weather')"
            >
              + Add Weather
            </button>
            <button
              type="button"
              class="add-btn"
              data-testid="add-widget-game-btn"
              (click)="addWidget('game')"
            >
              + Add HTML5 Game
            </button>
            <button
              type="button"
              class="add-btn"
              data-testid="add-widget-quote-btn"
              (click)="addWidget('quote')"
            >
              + Add Daily Quote
            </button>
          </div>

          <div class="widget-order-list">
            @for (widget of widgets(); track widget.id) {
              <div class="widget-order-row">
                <div>
                  <strong>{{ widget.title }}</strong>
                  <span class="type-chip">{{ widget.type }}</span>
                </div>
                <div class="row-actions">
                  <button
                    type="button"
                    class="icon-btn"
                    aria-label="Move widget up"
                    (click)="moveWidget(widget.id, 'up')"
                  >
                    ↑
                  </button>
                  <button
                    type="button"
                    class="icon-btn"
                    aria-label="Move widget down"
                    (click)="moveWidget(widget.id, 'down')"
                  >
                    ↓
                  </button>
                  <button
                    type="button"
                    class="remove-btn"
                    [attr.data-testid]="'remove-widget-' + widget.id"
                    (click)="removeWidget(widget.id)"
                  >
                    Remove
                  </button>
                </div>
              </div>
            }
          </div>
        </div>
      }
    </section>
  `,
  styles: [
    `
      .customizer-card {
        margin-top: 20px;
        background: var(--profile-card, #1e293b);
        border: 1px solid var(--profile-border, #334155);
        border-radius: 18px;
        padding: 24px;
        display: grid;
        gap: 18px;
      }

      .customizer-header {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        align-items: center;
        gap: 14px;
      }

      .eyebrow {
        margin: 0 0 4px;
        text-transform: uppercase;
        letter-spacing: 0.08em;
        font-size: 0.72rem;
        color: var(--profile-accent, #38bdf8);
      }

      h3 {
        margin: 0;
        font-size: 1.25rem;
      }

      .tab-bar {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
      }

      .tab-btn,
      .primary-btn,
      .secondary-btn,
      .upload-btn,
      .add-btn,
      .icon-btn,
      .remove-btn {
        min-height: 38px;
        padding: 8px 14px;
        border-radius: 10px;
        border: 1px solid #475569;
        background: #0f172a;
        color: #f8fafc;
        font-size: 0.84rem;
        font-weight: 600;
        cursor: pointer;
        display: inline-flex;
        align-items: center;
        justify-content: center;
      }

      .tab-btn.active,
      .primary-btn,
      .add-btn {
        border-color: var(--profile-accent, #38bdf8);
        background: rgba(56, 189, 248, 0.16);
      }

      .upload-btn input {
        display: none;
      }

      .theme-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(175px, 1fr));
        gap: 12px;
      }

      .theme-tile {
        min-height: 98px;
        padding: 14px;
        border-radius: 14px;
        border: 1px solid #334155;
        background: #0f172a;
        color: #f8fafc;
        display: grid;
        gap: 6px;
        text-align: left;
        cursor: pointer;
      }

      .theme-tile.selected {
        border: 2px solid var(--profile-accent, #38bdf8);
      }

      .swatches {
        display: flex;
        gap: 6px;
      }

      .swatch-dot {
        width: 18px;
        height: 18px;
        border-radius: 50%;
        border: 1px solid rgba(255, 255, 255, 0.35);
      }

      .theme-tagline {
        font-size: 0.78rem;
        color: #94a3b8;
      }

      .css-editor-panel,
      .widgets-manager {
        display: grid;
        gap: 12px;
      }

      .security-note {
        margin: 0;
        font-size: 0.84rem;
        color: #cbd5e1;
      }

      .css-textarea,
      .title-input {
        width: 100%;
        padding: 12px;
        border-radius: 10px;
        border: 1px solid #475569;
        background: #0f172a;
        color: #f8fafc;
        font-family: 'Courier New', monospace;
        font-size: 0.86rem;
      }

      .css-actions,
      .add-widget-bar {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
      }

      .title-input {
        flex: 1;
        min-width: 190px;
        font-family: inherit;
      }

      .sanitization-status {
        padding: 10px 12px;
        border-radius: 10px;
        background: rgba(16, 185, 129, 0.14);
        border: 1px solid #10b981;
        font-size: 0.84rem;
      }

      .sanitization-status.warn {
        background: rgba(245, 158, 11, 0.14);
        border-color: #f59e0b;
        color: #fde68a;
      }

      .blocked-list {
        margin: 6px 0 0;
      }

      .widget-order-list {
        display: grid;
        gap: 8px;
      }

      .widget-order-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 10px;
        padding: 10px 12px;
        border-radius: 10px;
        background: #0f172a;
        border: 1px solid #334155;
      }

      .type-chip {
        margin-left: 8px;
        padding: 2px 8px;
        border-radius: 999px;
        background: #1e293b;
        color: var(--profile-accent, #38bdf8);
        font-size: 0.74rem;
        text-transform: uppercase;
      }

      .row-actions {
        display: flex;
        gap: 6px;
      }

      .remove-btn {
        background: rgba(239, 68, 68, 0.16);
        border-color: #ef4444;
        color: #fecaca;
      }
    `,
  ],
})
export class ProfileCustomizerComponent {
  private readonly customizationService = inject(ProfileCustomizationService);

  readonly themes = this.customizationService.themes;
  readonly selectedThemeId = this.customizationService.selectedThemeId;
  readonly widgets = this.customizationService.widgets;
  readonly sanitizationReport = this.customizationService.sanitizationReport;

  readonly activeTab = signal<CustomizerTab>('themes');
  readonly cssDraft = signal<string>(this.customizationService.customCssRaw());
  readonly widgetTitleDraft = signal<string>('');

  chooseTheme(themeId: ProfileThemeId): void {
    this.customizationService.selectTheme(themeId);
  }

  onCssInput(event: Event): void {
    const textarea = event.target as HTMLTextAreaElement | null;
    this.cssDraft.set(textarea?.value ?? '');
  }

  applyCustomCss(): void {
    this.customizationService.applyCustomCss(this.cssDraft());
  }

  loadSampleCss(): void {
    this.cssDraft.set(SAMPLE_RETRO_CSS);
    this.customizationService.applyCustomCss(SAMPLE_RETRO_CSS);
  }

  clearCss(): void {
    this.cssDraft.set('');
    this.customizationService.clearCustomCss();
  }

  onCssFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement | null;
    const file = input?.files?.[0];
    if (!file) {
      return;
    }
    const reader = new FileReader();
    reader.onload = () => {
      if (typeof reader.result === 'string') {
        this.cssDraft.set(reader.result);
        this.customizationService.applyCustomCss(reader.result);
      }
    };
    reader.readAsText(file);
  }

  onWidgetTitleInput(event: Event): void {
    const input = event.target as HTMLInputElement | null;
    this.widgetTitleDraft.set(input?.value ?? '');
  }

  addWidget(type: ProfileWidgetType): void {
    this.customizationService.addWidget(type, this.widgetTitleDraft());
    this.widgetTitleDraft.set('');
  }

  moveWidget(widgetId: string, direction: 'up' | 'down'): void {
    this.customizationService.moveWidget(widgetId, direction);
  }

  removeWidget(widgetId: string): void {
    this.customizationService.removeWidget(widgetId);
  }
}
