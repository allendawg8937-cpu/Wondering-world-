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

  it('should render current 3D avatar view to a PNG data URL and trigger a browser download on Download Snapshot', () => {
    const fixture = TestBed.createComponent(Avatar3dCustomizerComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();

    const realCreateElement = document.createElement.bind(document);
    const clickSpy = jasmine.createSpy('click');
    const createdLinks: HTMLAnchorElement[] = [];

    spyOn(document, 'createElement').and.callFake((tagName: string) => {
      const element = realCreateElement(tagName);
      if (tagName.toLowerCase() === 'a') {
        const anchor = element as HTMLAnchorElement;
        anchor.click = clickSpy;
        createdLinks.push(anchor);
      }
      return element;
    });

    const snapshots: Array<{ dataUrl: string; filename: string }> = [];
    component.snapshotDownloaded.subscribe(event => snapshots.push(event));

    const btn = fixture.nativeElement.querySelector(
      '[data-testid="avatar-3d-snapshot"]'
    ) as HTMLButtonElement;
    expect(btn.textContent?.trim()).toBe('Download Snapshot');

    btn.click();
    fixture.detectChanges();

    expect(clickSpy).toHaveBeenCalled();
    expect(createdLinks.length).toBe(1);
    expect(createdLinks[0].download).toBe(
      `wondering-world-${component.config().stageId}-${component.config().baseModelStyle}.png`
    );
    expect(createdLinks[0].href.startsWith('data:image/png')).toBeTrue();
    expect(component.lastSnapshotDataUrl()?.startsWith('data:image/png')).toBeTrue();
    expect(snapshots.length).toBe(1);
    expect(component.statusMessage()).toContain('Downloaded 3D avatar snapshot');
  });

  it('should trigger interactive animation states including idle, wave, and dance', () => {
    const fixture = TestBed.createComponent(Avatar3dCustomizerComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();

    for (const poseId of ['wave', 'dance', 'levitate', 'victory', 'idle'] as const) {
      const btn = fixture.nativeElement.querySelector(
        `[data-testid="pose-option-${poseId}"]`
      ) as HTMLButtonElement;
      expect(btn).toBeTruthy();
      btn.click();
      fixture.detectChanges();
      expect(component.config().pose).toBe(poseId);
      expect(btn.getAttribute('aria-pressed')).toBe('true');
    }
  });
});
