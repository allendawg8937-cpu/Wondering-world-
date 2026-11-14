import { TestBed } from '@angular/core/testing';
import { AvatarThemeSelectorComponent } from './avatar-theme-selector.component';
import {
  AVATAR_BASE_MODEL_OPTIONS,
  AVATAR_VISUAL_THEME_PRESETS,
  LIFE_STAGE_AVATAR_PRESETS,
} from '../../../data-access/models/avatar-3d.model';

describe('AvatarThemeSelectorComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvatarThemeSelectorComponent],
    }).compileComponents();
  });

  it('should render buttons for all life stages, base model styles, and theme presets', () => {
    const fixture = TestBed.createComponent(AvatarThemeSelectorComponent);
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;

    for (const preset of LIFE_STAGE_AVATAR_PRESETS) {
      expect(
        el.querySelector(`[data-testid="stage-preset-${preset.stageId}"]`)
      ).toBeTruthy();
    }
    for (const model of AVATAR_BASE_MODEL_OPTIONS) {
      expect(
        el.querySelector(`[data-testid="base-model-btn-${model.id}"]`)
      ).toBeTruthy();
    }
    for (const theme of AVATAR_VISUAL_THEME_PRESETS) {
      expect(
        el.querySelector(`[data-testid="theme-style-btn-${theme.id}"]`)
      ).toBeTruthy();
    }
  });

  it('should emit presetSelected, baseModelSelected, and themeStyleSelected when buttons are clicked', () => {
    const fixture = TestBed.createComponent(AvatarThemeSelectorComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;

    const events: string[] = [];
    component.presetSelected.subscribe(p => events.push(`preset:${p.stageId}`));
    component.baseModelSelected.subscribe(m => events.push(`model:${m.id}`));
    component.themeStyleSelected.subscribe(t => events.push(`theme:${t.id}`));

    (
      el.querySelector('[data-testid="stage-preset-business"]') as HTMLButtonElement
    ).click();
    (
      el.querySelector('[data-testid="base-model-btn-mech-guardian"]') as HTMLButtonElement
    ).click();
    (
      el.querySelector('[data-testid="theme-style-btn-solar-gold"]') as HTMLButtonElement
    ).click();

    expect(events).toEqual([
      'preset:business',
      'model:mech-guardian',
      'theme:solar-gold',
    ]);
  });
});
