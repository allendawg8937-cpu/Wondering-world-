import { TestBed } from '@angular/core/testing';
import { Avatar3dCustomizerComponent } from './avatar-3d-customizer.component';
import {
  AVATAR_BASE_MODEL_OPTIONS,
  AVATAR_LIGHTING_PRESETS,
  AVATAR_VISUAL_THEME_PRESETS,
  LIFE_STAGE_AVATAR_PRESETS,
} from '../../../data-access/models/avatar-3d.model';

describe('Avatar3dCustomizerComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Avatar3dCustomizerComponent],
    }).compileComponents();
  });

  it('should create and expose all six life stage 3D presets', () => {
    const fixture = TestBed.createComponent(Avatar3dCustomizerComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(component.presets.length).toBe(6);
    expect(LIFE_STAGE_AVATAR_PRESETS.map(p => p.stageId)).toEqual([
      'kid',
      'teen',
      'student',
      'adult',
      'business',
      'veteran',
    ]);
  });

  it('should switch life stage preset, base model style, theme style, lighting, gear, and emit saved config', () => {
    const fixture = TestBed.createComponent(Avatar3dCustomizerComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();

    const emitted: string[] = [];
    component.lookSaved.subscribe(cfg =>
      emitted.push(`${cfg.stageId}:${cfg.baseModelStyle}:${cfg.themeStyleId}:${cfg.gearId}`)
    );

    const teenPreset = LIFE_STAGE_AVATAR_PRESETS.find(p => p.stageId === 'teen')!;
    component.applyStagePreset(teenPreset);
    expect(component.config().stageId).toBe('teen');

    const crystalModel = AVATAR_BASE_MODEL_OPTIONS.find(
      m => m.id === 'crystal-sprite'
    )!;
    component.applyBaseModelStyle(crystalModel);
    expect(component.config().baseModelStyle).toBe('crystal-sprite');

    const synthTheme = AVATAR_VISUAL_THEME_PRESETS.find(
      t => t.id === 'synthwave-sunset'
    )!;
    component.applyVisualTheme(synthTheme);
    expect(component.config().themeStyleId).toBe('synthwave-sunset');

    const brightLighting = AVATAR_LIGHTING_PRESETS.find(
      l => l.id === 'bright-fill'
    )!;
    component.applyLightingPreset(brightLighting);
    expect(component.config().ambientIntensity).toBe(1.35);
    expect(component.config().directionalIntensity).toBe(2.1);

    component.setGear('crown');
    component.updateConfig({ wireframe: true });
    component.saveLook();

    expect(component.config().gearId).toBe('crown');
    expect(component.config().wireframe).toBeTrue();
    expect(emitted).toContain('teen:crystal-sprite:synthwave-sunset:crown');
  });
});
