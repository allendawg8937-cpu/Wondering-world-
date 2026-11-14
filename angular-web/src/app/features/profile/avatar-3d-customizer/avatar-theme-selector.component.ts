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
          @for (preset of stagePresets; track preset.stageId) {
            <button
              type="button"
              class="choice-pill"
              [class.active]="selectedStageId === preset.stageId"
              [style.--pill-accent]="preset.accentHex"
              [attr.aria-pressed]="selectedStageId === preset.stageId"
              [attr.data-testid]="'stage-preset-' + preset.stageId"
              (click)="presetSelected.emit(preset)"
            >
              <strong>{{ preset.stageLabel }}</strong>
              <small>{{ preset.characterName }}</small>
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
        background: #111827;
        border: 1px solid #1f2937;
        border-radius: 14px;
        padding: 14px;
        display: grid;
        gap: 10px;
      }

      h3 {
        margin: 0;
        font-size: 0.92rem;
        color: #e2e8f0;
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
        border: 1px solid #334155;
        background: #0f172a;
        color: #e2e8f0;
        cursor: pointer;
        display: grid;
        gap: 2px;
      }

      .choice-pill.active {
        border-color: var(--pill-accent, #38bdf8);
        background: rgba(56, 189, 248, 0.14);
      }

      .choice-pill small {
        font-size: 0.72rem;
        color: #94a3b8;
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
        border: 1px solid #334155;
        background: #0f172a;
        color: #cbd5e1;
        font-size: 0.8rem;
        cursor: pointer;
      }

      .theme-chip.active {
        border-color: var(--chip-accent, #38bdf8);
        background: rgba(56, 189, 248, 0.16);
        color: #ffffff;
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
