// src/app/data-access/services/persona.service.spec.ts
import { TestBed } from '@angular/core/testing';
import { LifePersonaId } from '../models/persona.model';
import { PersonaService } from './persona.service';

describe('PersonaService', () => {
  let service: PersonaService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
    service = TestBed.inject(PersonaService);
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('exposes all six life personas in order', () => {
    const ids = service.personas.map(p => p.id);
    expect(ids).toEqual(['kid', 'teen', 'student', 'adult', 'business', 'veteran']);
  });

  it('switches across all six personas and updates activePersona signal and stream', () => {
    const expectedIds: LifePersonaId[] = [
      'kid',
      'teen',
      'student',
      'adult',
      'business',
      'veteran',
    ];
    let latestStreamId: LifePersonaId | undefined;
    const sub = service.activePersona$.subscribe(persona => {
      latestStreamId = persona.id;
    });

    for (const id of expectedIds) {
      const selected = service.switchPersona(id);
      expect(selected.id).toBe(id);
      expect(service.activePersonaId()).toBe(id);
      expect(service.activePersona().id).toBe(id);
      expect(latestStreamId).toBe(id);
    }

    sub.unsubscribe();
  });

  it('cycles through personas sequentially wrapping from veteran back to kid and backwards', () => {
    service.switchPersona('kid');
    expect(service.cycleNextPersona().id).toBe('teen');
    expect(service.cycleNextPersona().id).toBe('student');
    expect(service.cycleNextPersona().id).toBe('adult');
    expect(service.cycleNextPersona().id).toBe('business');
    expect(service.cycleNextPersona().id).toBe('veteran');
    expect(service.cycleNextPersona().id).toBe('kid');

    expect(service.cyclePreviousPersona().id).toBe('veteran');
    expect(service.cyclePreviousPersona().id).toBe('business');
  });

  it('tracks checklist task completion per persona independently', () => {
    service.switchPersona('student');
    const studentTask = service.activePersona().focusAreas[0];
    expect(service.activeCompletionProgress()).toBe(0);

    service.toggleFocusTask('student', studentTask);
    expect(service.isTaskCompleted('student', studentTask)).toBeTrue();
    expect(service.activeCompletionProgress()).toBeGreaterThan(0);

    service.switchPersona('business');
    expect(service.activeCompletionProgress()).toBe(0);
  });
});
