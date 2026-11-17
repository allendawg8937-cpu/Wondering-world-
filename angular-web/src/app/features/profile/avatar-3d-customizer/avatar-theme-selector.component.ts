import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  AVATAR_BASE_MODEL_OPTIONS,
  AVATAR_VISUAL_THEME_PRESETS,
  AvatarBaseModelOption,
  AvatarBaseModelStyle,
  AvatarVisualThemePreset,
  LIFE_STAGE_AVATAR_PRESETS,
  LifeStageAvatarPreset,
  LifeStageId,
} from '../../../data-access/models/avatar-3d.model';

@Component({
  selector: 'app-avatar-theme-selector',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="theme-selector" data-testid="avatar-theme-selector">
      <div class="selector-group">
        <h3>1. Life Stage Base Character</h3>
        <div class="pill-grid">
          @for (preset of stagePresets; track preset.stageId; let i = $index) {
            <button
              type="button"
              class="choice-pill stage-pill"
              [class.active]="selectedStageId === preset.stageId"
              [style.--pill-accent]="preset.accentHex"
              [style.--stagger-index]="i"
              [attr.aria-pressed]="selectedStageId === preset.stageId"
              [attr.data-testid]="'stage-preset-' + preset.stageId"
              (click)="presetSelected.emit(preset)"
            >
              <span class="stage-pill-row">
                <span class="stage-icon" aria-hidden="true">{{ preset.badgeEmoji }}</span>
                <span class="stage-pill-copy">
                  <strong>{{ preset.stageLabel }}</strong>
                  <small>{{ preset.characterName }}</small>
                </span>
              </span>
            </button>
          }
        </div>
      </div>

      <div class="selector-group">
        <h3>2. 3D Base Model Architecture</h3>
        <div class="pill-grid">
          @for (model of baseModelOptions; track model.id) {
            <button
              type="button"
              class="choice-pill"
              [class.active]="selectedBaseModel === model.id"
              [style.--pill-accent]="model.accentHex"
              [attr.aria-pressed]="selectedBaseModel === model.id"
              [attr.data-testid]="'base-model-btn-' + model.id"
              (click)="baseModelSelected.emit(model)"
            >
              <strong>{{ model.label }}</strong>
              <small>{{ model.subtitle }}</small>
            </button>
          }
        </div>
      </div>

      <div class="selector-group">
        <h3>3. Three.js Lighting &amp; Style Theme</h3>
        <div class="theme-row">
          @for (theme of visualThemes; track theme.id) {
            <button
              type="button"
              class="theme-chip"
              [class.active]="selectedThemeId === theme.id"
              [style.--chip-accent]="theme.accentHex"
              [attr.aria-pressed]="selectedThemeId === theme.id"
              [attr.data-testid]="'theme-style-btn-' + theme.id"
              (click)="themeStyleSelected.emit(theme)"
            >
              <span
                class="theme-swatch"
                [style.background]="theme.accentHex"
              ></span>
              <span>{{ theme.label }}</span>
            </button>
          }
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .theme-selector {
        display: grid;
        gap: 14px;
      }

      .selector-group {
        background: var(--bg-card);
        border: 1px solid var(--border-subtle);
        border-radius: 14px;
        padding: 14px;
        display: grid;
        gap: 10px;
      }

      h3 {
        margin: 0;
        font-size: 0.92rem;
        color: var(--text-primary);
      }

      .pill-grid {
        display: grid;
        grid-template-columns: repeat(2, minmax(0, 1fr));
        gap: 8px;
      }

      .choice-pill {
        text-align: left;
        padding: 9px 10px;
        border-radius: 10px;
        border: 1px solid var(--border-subtle);
        background: var(--bg-surface);
        color: var(--text-primary);
        cursor: pointer;
        display: grid;
        gap: 2px;
        transition:
          transform 200ms cubic-bezier(0.22, 1, 0.36, 1),
          border-color 200ms ease,
          background-color 200ms ease,
          box-shadow 200ms ease;
      }

      .stage-pill {
        animation: stagePillEntrance 420ms cubic-bezier(0.22, 1, 0.36, 1) both;
        animation-delay: calc(var(--stagger-index, 0) * 55ms);
      }

      .stage-pill-row {
        display: flex;
        align-items: center;
        gap: 9px;
      }

      .stage-icon {
        width: 30px;
        height: 30px;
        border-radius: 50%;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        font-size: 1rem;
        background: rgba(148, 163, 184, 0.14);
        flex-shrink: 0;
        transition:
          transform 240ms cubic-bezier(0.34, 1.56, 0.64, 1),
          background-color 220ms ease,
          box-shadow 220ms ease;
      }

      .stage-pill-copy {
        display: grid;
        gap: 2px;
        min-width: 0;
      }

      .choice-pill:hover {
        transform: translateY(-2px);
        border-color: var(--pill-accent, #38bdf8);
      }

      .stage-pill:hover .stage-icon {
        transform: scale(1.2) rotate(-8deg);
        background: color-mix(in srgb, var(--pill-accent, #38bdf8) 25%, transparent);
        box-shadow: 0 0 12px color-mix(in srgb, var(--pill-accent, #38bdf8) 45%, transparent);
      }

      .choice-pill.active {
        border-color: var(--pill-accent, #38bdf8);
        background: rgba(56, 189, 248, 0.16);
        box-shadow: 0 0 14px rgba(56, 189, 248, 0.18);
      }

      .stage-pill.active .stage-icon {
        transform: scale(1.14);
        background: color-mix(in srgb, var(--pill-accent, #38bdf8) 28%, transparent);
        animation: stageIconPop 360ms cubic-bezier(0.34, 1.56, 0.64, 1);
      }

      .choice-pill small {
        font-size: 0.72rem;
        color: var(--text-muted);
      }

      @keyframes stagePillEntrance {
        from {
          opacity: 0;
          transform: translateY(10px) scale(0.95);
        }
        to {
          opacity: 1;
          transform: translateY(0) scale(1);
        }
      }

      @keyframes stageIconPop {
        0% {
          transform: scale(0.88) rotate(0deg);
        }
        55% {
          transform: scale(1.26) rotate(8deg);
        }
        100% {
          transform: scale(1.14) rotate(0deg);
        }
      }

      @media (prefers-reduced-motion: reduce) {
        .stage-pill,
        .stage-pill.active .stage-icon {
          animation: none;
        }
        .choice-pill,
        .stage-icon {
          transition: none;
        }
      }

      .theme-row {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
      }

      .theme-chip {
        display: inline-flex;
        align-items: center;
        gap: 7px;
        padding: 7px 12px;
        border-radius: 999px;
        border: 1px solid var(--border-subtle);
        background: var(--bg-surface);
        color: var(--text-secondary);
        font-size: 0.8rem;
        cursor: pointer;
        transition: transform 180ms ease, border-color 180ms ease;
      }

      .theme-chip:hover {
        transform: translateY(-1px);
      }

      .theme-chip.active {
        border-color: var(--chip-accent, #38bdf8);
        background: rgba(56, 189, 248, 0.18);
        color: var(--text-primary);
        font-weight: 700;
      }

      .theme-swatch {
        width: 11px;
        height: 11px;
        border-radius: 50%;
        display: inline-block;
      }
    `,
  ],
})
export class AvatarThemeSelectorComponent {
  @Input() selectedStageId: LifeStageId = 'adult';
  @Input() selectedBaseModel: AvatarBaseModelStyle = 'humanoid-rig';
  @Input() selectedThemeId = 'cyber-neon';

  @Output() readonly presetSelected = new EventEmitter<LifeStageAvatarPreset>();
  @Output() readonly baseModelSelected =
    new EventEmitter<AvatarBaseModelOption>();
  @Output() readonly themeStyleSelected =
    new EventEmitter<AvatarVisualThemePreset>();

  readonly stagePresets = LIFE_STAGE_AVATAR_PRESETS;
  readonly baseModelOptions = AVATAR_BASE_MODEL_OPTIONS;
  readonly visualThemes = AVATAR_VISUAL_THEME_PRESETS;
}
