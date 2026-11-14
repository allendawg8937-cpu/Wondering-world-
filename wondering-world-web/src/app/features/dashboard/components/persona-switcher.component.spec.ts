// src/app/features/dashboard/components/persona-switcher.component.spec.ts
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { LifePersonaId } from '../../../data-access/models/persona.model';
import { PersonaService } from '../../../data-access/services/persona.service';
import { PersonaSwitcherComponent } from './persona-switcher.component';

describe('PersonaSwitcherComponent', () => {
  let fixture: ComponentFixture<PersonaSwitcherComponent>;
  let component: PersonaSwitcherComponent;
  let personaService: PersonaService;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [PersonaSwitcherComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    personaService = TestBed.inject(PersonaService);
    fixture = TestBed.createComponent(PersonaSwitcherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('renders toggle buttons for Kid, Teen, Student, Adult, Business, and Veteran', () => {
    const ids: LifePersonaId[] = [
      'kid',
      'teen',
      'student',
      'adult',
      'business',
      'veteran',
    ];
    for (const id of ids) {
      const btn = fixture.nativeElement.querySelector(
        `[data-testid="persona-option-${id}"]`
      ) as HTMLButtonElement | null;
      expect(btn).withContext(`Missing button for ${id}`).not.toBeNull();
    }
  });

  it('updates stage view and emits personaChanged when clicking each persona', () => {
    const emitted: LifePersonaId[] = [];
    const sub = component.personaChanged.subscribe(persona =>
      emitted.push(persona.id)
    );

    const targets: LifePersonaId[] = [
      'kid',
      'teen',
      'student',
      'adult',
      'business',
      'veteran',
    ];

    for (const id of targets) {
      const btn = fixture.nativeElement.querySelector(
        `[data-testid="persona-option-${id}"]`
      ) as HTMLButtonElement;
      btn.click();
      fixture.detectChanges();

      expect(personaService.activePersonaId()).toBe(id);
      expect(btn.getAttribute('aria-pressed')).toBe('true');

      const stageView = fixture.nativeElement.querySelector(
        `[data-testid="persona-stage-view-${id}"]`
      );
      expect(stageView).withContext(`Missing stage view for ${id}`).not.toBeNull();

      const activeIcons = fixture.nativeElement.querySelectorAll(
        `[data-testid="persona-icon-${id}"].is-active svg`
      );
      expect(activeIcons.length)
        .withContext(`Expected animated active SVG icon for ${id}`)
        .toBeGreaterThanOrEqual(2);
    }

    expect(emitted).toEqual(targets);
    sub.unsubscribe();
  });

  it('navigates seamlessly with Prev/Next controls and keyboard arrow keys', () => {
    const prevBtn = fixture.nativeElement.querySelector(
      '[data-testid="persona-prev-btn"]'
    ) as HTMLButtonElement;
    const nextBtn = fixture.nativeElement.querySelector(
      '[data-testid="persona-cycle-btn"]'
    ) as HTMLButtonElement;
    const grid = fixture.nativeElement.querySelector(
      '.persona-grid'
    ) as HTMLElement;

    personaService.switchPersona('kid');
    fixture.detectChanges();

    nextBtn.click();
    fixture.detectChanges();
    expect(personaService.activePersonaId()).toBe('teen');

    prevBtn.click();
    fixture.detectChanges();
    expect(personaService.activePersonaId()).toBe('kid');

    grid.dispatchEvent(new KeyboardEvent('keydown', { key: 'End', bubbles: true }));
    fixture.detectChanges();
    expect(personaService.activePersonaId()).toBe('veteran');

    grid.dispatchEvent(new KeyboardEvent('keydown', { key: 'Home', bubbles: true }));
    fixture.detectChanges();
    expect(personaService.activePersonaId()).toBe('kid');
  });
});
