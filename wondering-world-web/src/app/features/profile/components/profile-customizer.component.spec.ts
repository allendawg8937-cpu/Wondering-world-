// src/app/features/profile/components/profile-customizer.component.spec.ts
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProfileCustomizationService } from '../../../data-access/services/profile-customization.service';
import { ProfileCustomizerComponent } from './profile-customizer.component';
import { ProfileGameWidgetComponent } from './profile-game-widget.component';

describe('ProfileCustomizerComponent', () => {
  let fixture: ComponentFixture<ProfileCustomizerComponent>;
  let customizationService: ProfileCustomizationService;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [ProfileCustomizerComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    customizationService = TestBed.inject(ProfileCustomizationService);
    fixture = TestBed.createComponent(ProfileCustomizerComponent);
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('lets users switch between pre-designed profile themes', () => {
    const sunsetBtn = fixture.nativeElement.querySelector(
      '[data-testid="theme-option-sunset-arcade"]'
    ) as HTMLButtonElement;
    sunsetBtn.click();
    fixture.detectChanges();

    expect(customizationService.selectedThemeId()).toBe('sunset-arcade');
    expect(sunsetBtn.getAttribute('aria-pressed')).toBe('true');
  });

  it('sanitizes custom CSS uploads/inputs and surfaces blocked patterns', () => {
    const cssTab = fixture.nativeElement.querySelector(
      '[data-testid="customizer-tab-css"]'
    ) as HTMLButtonElement;
    cssTab.click();
    fixture.detectChanges();

    const textarea = fixture.nativeElement.querySelector(
      '[data-testid="custom-css-textarea"]'
    ) as HTMLTextAreaElement;
    textarea.value =
      '@import url(https://bad.test/a.css); .profile-card { color: #34d399; }';
    textarea.dispatchEvent(new Event('input'));

    const applyBtn = fixture.nativeElement.querySelector(
      '[data-testid="apply-custom-css-btn"]'
    ) as HTMLButtonElement;
    applyBtn.click();
    fixture.detectChanges();

    expect(customizationService.sanitizedCss()).toContain(
      '[data-profile-scope="active"] .profile-card'
    );
    expect(customizationService.sanitizedCss()).not.toContain('@import');
    expect(
      fixture.nativeElement.querySelector('[data-testid="css-blocked-patterns"]')
    ).not.toBeNull();
  });

  it('adds and removes clock, weather, and HTML5 game widgets', () => {
    const widgetsTab = fixture.nativeElement.querySelector(
      '[data-testid="customizer-tab-widgets"]'
    ) as HTMLButtonElement;
    widgetsTab.click();
    fixture.detectChanges();

    const initialCount = customizationService.widgets().length;

    (
      fixture.nativeElement.querySelector(
        '[data-testid="add-widget-weather-btn"]'
      ) as HTMLButtonElement
    ).click();
    (
      fixture.nativeElement.querySelector(
        '[data-testid="add-widget-game-btn"]'
      ) as HTMLButtonElement
    ).click();
    fixture.detectChanges();

    expect(customizationService.widgets().length).toBe(initialCount + 2);
    expect(
      customizationService.widgets().some(w => w.type === 'weather')
    ).toBeTrue();
    expect(
      customizationService.widgets().some(w => w.type === 'game')
    ).toBeTrue();
  });

  it('enforces HTTPS-only validation on custom HTML5 game embeds', () => {
    const gameFixture = TestBed.createComponent(ProfileGameWidgetComponent);
    gameFixture.componentInstance.widget = {
      id: 'w_game_test',
      type: 'game',
      title: 'Test Game',
      gameConfig: { mode: 'custom-embed', embedUrl: '' },
    };
    gameFixture.detectChanges();

    gameFixture.componentInstance.setMode('custom-embed');
    gameFixture.detectChanges();

    gameFixture.componentInstance.embedUrlInput.set('javascript:alert(1)');
    gameFixture.componentInstance.applyEmbedUrl();
    gameFixture.detectChanges();

    expect(gameFixture.componentInstance.safeEmbedUrl()).toBeNull();
    expect(
      gameFixture.nativeElement.querySelector(
        '[data-testid="widget-game-embed-error"]'
      )
    ).not.toBeNull();

    gameFixture.componentInstance.embedUrlInput.set('https://example.com/arcade');
    gameFixture.componentInstance.applyEmbedUrl();
    gameFixture.detectChanges();

    expect(gameFixture.componentInstance.safeEmbedUrl()).not.toBeNull();
    expect(
      gameFixture.nativeElement.querySelector('[data-testid="widget-game-iframe"]')
    ).not.toBeNull();
  });
});
