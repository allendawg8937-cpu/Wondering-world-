// src/app/data-access/services/persona.service.ts
import { computed, Injectable, signal } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import {
  LifePersonaId,
  PersonaChecklistMap,
  PersonaDefinition,
} from '../models/persona.model';

const PERSONA_STORAGE_KEY = 'wondering_world_active_persona';

const DEFAULT_PERSONAS: readonly PersonaDefinition[] = [
  {
    id: 'kid',
    label: 'Kid',
    shortCode: 'KD',
    ageRange: 'Ages 6–12',
    tagline: 'Playful discovery, creative projects & safe family habits',
    summary:
      'Interactive learning quests, avatar customization, pocket-money jars, and guided daily curiosity challenges.',
    accentColor: '#38bdf8',
    badgeBackground: 'rgba(56, 189, 248, 0.16)',
    primaryCta: 'Launch Kid Explorer Quest',
    focusAreas: [
      'Complete 15-minute creative drawing or STEM puzzle',
      'Add weekly allowance coins to savings jar',
      'Share one kind family moment in the journal',
    ],
    metrics: [
      { label: 'Explorer Badges', value: '12 Earned' },
      { label: 'Savings Jar', value: '$45' },
      { label: 'Learning Streak', value: '7 Days' },
    ],
    recommendedModules: [
      { title: 'My Avatar Studio', subtitle: 'Customize colors & playful look', route: '/profile' },
      { title: 'Allowance Jars', subtitle: 'Track kid savings milestones', route: '/finance/goals' },
    ],
  },
  {
    id: 'teen',
    label: 'Teen',
    shortCode: 'TN',
    ageRange: 'Ages 13–17',
    tagline: 'Identity expression, creative studio & early independence',
    summary:
      'Personal theme styling, first savings targets, digital citizenship tools, and creative media expression.',
    accentColor: '#a855f7',
    badgeBackground: 'rgba(168, 85, 247, 0.16)',
    primaryCta: 'Open Teen Creator Hub',
    focusAreas: [
      'Customize personal profile theme colors & bio',
      'Track first tech or gear savings goal',
      'Review community conduct & digital safety check',
    ],
    metrics: [
      { label: 'Studio Creations', value: '9 Drafts' },
      { label: 'Youth Goal Fund', value: '$280' },
      { label: 'Peer Props', value: '34 Given' },
    ],
    recommendedModules: [
      { title: 'Theme & Profile', subtitle: 'Express your personal identity', route: '/profile' },
      { title: 'First Goals Tracker', subtitle: 'Save for hobbies & milestones', route: '/finance/goals' },
    ],
  },
  {
    id: 'student',
    label: 'Student',
    shortCode: 'ST',
    ageRange: 'Ages 18–24',
    tagline: 'Academic mastery, career readiness & campus budgeting',
    summary:
      'Semester study blocks, scholarship & tuition planning, research bookmarks, and early career networking.',
    accentColor: '#10b981',
    badgeBackground: 'rgba(16, 185, 129, 0.16)',
    primaryCta: 'Open Study & Career Planner',
    focusAreas: [
      'Log weekly study milestone and revision block',
      'Update textbook, lab, or tuition budget target',
      'Draft portfolio highlight on public profile',
    ],
    metrics: [
      { label: 'Study Focus', value: '18.5 hrs/wk' },
      { label: 'Semester Budget', value: '82% On Track' },
      { label: 'Career Steps', value: '4 / 6 Ready' },
    ],
    recommendedModules: [
      { title: 'Semester Budget', subtitle: 'Control tuition & living expenses', route: '/finance/goals' },
      { title: 'Academic Bio', subtitle: 'Showcase skills & credentials', route: '/profile' },
    ],
  },
  {
    id: 'adult',
    label: 'Adult',
    shortCode: 'AD',
    ageRange: 'Ages 25–64',
    tagline: 'Household balance, career progression & long-term wealth',
    summary:
      'Unified financial milestones, emergency reserves, personal wellness routines, and community mentorship.',
    accentColor: '#3b82f6',
    badgeBackground: 'rgba(59, 130, 246, 0.16)',
    primaryCta: 'Review Adult Life Balance',
    focusAreas: [
      'Automate monthly emergency reserve contribution',
      'Schedule annual career & skill review checkpoint',
      'Maintain 3x weekly wellness & recovery routine',
    ],
    metrics: [
      { label: 'Reserve Progress', value: '$6,400' },
      { label: 'Active Goals', value: '3 Milestones' },
      { label: 'Community Streak', value: '14 Days' },
    ],
    recommendedModules: [
      { title: 'Wealth & Savings', subtitle: 'Manage emergency & home funds', route: '/finance/goals' },
      { title: 'Profile & Safety', subtitle: 'Manage privacy & moderation tools', route: '/profile' },
    ],
  },
  {
    id: 'business',
    label: 'Business',
    shortCode: 'BZ',
    ageRange: 'Founders & Leaders',
    tagline: 'Venture growth, cash-flow KPIs & client operations',
    summary:
      'Quarterly revenue targets, brand storefront positioning, project pipeline tracking, and team leadership logs.',
    accentColor: '#f59e0b',
    badgeBackground: 'rgba(245, 158, 11, 0.18)',
    primaryCta: 'Inspect Business Growth Console',
    focusAreas: [
      'Audit monthly operating runway & reserve target',
      'Publish client case study or product announcement',
      'Review partner deliverables & Q4 pipeline targets',
    ],
    metrics: [
      { label: 'Quarterly Target', value: '76% Reached' },
      { label: 'Active Clients', value: '14 Accounts' },
      { label: 'Operating Reserve', value: '9 Months' },
    ],
    recommendedModules: [
      { title: 'Capital Reserves', subtitle: 'Track business liquidity targets', route: '/finance/goals' },
      { title: 'Brand Profile', subtitle: 'Manage business identity & trust', route: '/profile' },
    ],
  },
  {
    id: 'veteran',
    label: 'Veteran',
    shortCode: 'VT',
    ageRange: 'Service & Mentorship',
    tagline: 'Transition benefits, peer camaraderie & legacy mentorship',
    summary:
      'Benefit & transition checklists, veteran community circles, mentorship hours, and retirement security planning.',
    accentColor: '#ef4444',
    badgeBackground: 'rgba(239, 68, 68, 0.16)',
    primaryCta: 'Enter Veteran Support Hub',
    focusAreas: [
      'Verify annual benefit & healthcare renewal dates',
      'Connect with regional peer mentor & service chapter',
      'Record personal story or leadership mentoring note',
    ],
    metrics: [
      { label: 'Benefits Checklist', value: '5 / 5 Verified' },
      { label: 'Mentoring Hours', value: '24 hrs' },
      { label: 'Peer Network', value: '19 Connections' },
    ],
    recommendedModules: [
      { title: 'Retirement & Benefits', subtitle: 'Long-term security & reserves', route: '/finance/goals' },
      { title: 'Service Profile', subtitle: 'Share experience & mentor peers', route: '/profile' },
    ],
  },
];

function createEmptyChecklist(): PersonaChecklistMap {
  return {
    kid: [],
    teen: [],
    student: [],
    adult: [],
    business: [],
    veteran: [],
  };
}

@Injectable({
  providedIn: 'root',
})
export class PersonaService {
  readonly personas: readonly PersonaDefinition[] = DEFAULT_PERSONAS;

  private readonly activePersonaIdSignal = signal<LifePersonaId>(
    this.readPersistedPersonaId()
  );
  private readonly completedChecklistSignal = signal<PersonaChecklistMap>(
    createEmptyChecklist()
  );
  private readonly lastSwitchedAtSignal = signal<string>(
    new Date().toISOString()
  );

  readonly activePersonaId = this.activePersonaIdSignal.asReadonly();
  readonly lastSwitchedAt = this.lastSwitchedAtSignal.asReadonly();

  readonly activePersona = computed<PersonaDefinition>(() =>
    this.getPersonaById(this.activePersonaIdSignal())
  );

  readonly activeCompletedTasks = computed<readonly string[]>(() => {
    const id = this.activePersonaIdSignal();
    return this.completedChecklistSignal()[id];
  });

  readonly activeCompletionProgress = computed<number>(() => {
    const persona = this.activePersona();
    const total = persona.focusAreas.length;
    if (total === 0) {
      return 0;
    }
    const done = this.activeCompletedTasks().length;
    return Math.round((done / total) * 100);
  });

  private readonly activePersonaSubject = new BehaviorSubject<PersonaDefinition>(
    this.activePersona()
  );
  readonly activePersona$: Observable<PersonaDefinition> =
    this.activePersonaSubject.asObservable();

  switchPersona(personaId: LifePersonaId): PersonaDefinition {
    const target = this.getPersonaById(personaId);
    this.activePersonaIdSignal.set(target.id);
    this.lastSwitchedAtSignal.set(new Date().toISOString());
    this.persistPersonaId(target.id);
    this.activePersonaSubject.next(target);
    return target;
  }

  cycleNextPersona(): PersonaDefinition {
    const currentId = this.activePersonaIdSignal();
    const currentIndex = this.personas.findIndex(p => p.id === currentId);
    const nextIndex = (currentIndex + 1) % this.personas.length;
    const nextPersona = this.personas[nextIndex] ?? this.personas[0];
    return this.switchPersona(nextPersona.id);
  }

  cyclePreviousPersona(): PersonaDefinition {
    const currentId = this.activePersonaIdSignal();
    const currentIndex = this.personas.findIndex(p => p.id === currentId);
    const prevIndex =
      (currentIndex - 1 + this.personas.length) % this.personas.length;
    const prevPersona = this.personas[prevIndex] ?? this.personas[0];
    return this.switchPersona(prevPersona.id);
  }

  getPersonaById(personaId: LifePersonaId): PersonaDefinition {
    return (
      this.personas.find(persona => persona.id === personaId) ??
      this.personas[3]
    );
  }

  toggleFocusTask(personaId: LifePersonaId, taskTitle: string): void {
    this.completedChecklistSignal.update(current => {
      const existing = current[personaId];
      const nextTasks = existing.includes(taskTitle)
        ? existing.filter(item => item !== taskTitle)
        : [...existing, taskTitle];
      return {
        ...current,
        [personaId]: nextTasks,
      };
    });
  }

  isTaskCompleted(personaId: LifePersonaId, taskTitle: string): boolean {
    return this.completedChecklistSignal()[personaId].includes(taskTitle);
  }

  private readPersistedPersonaId(): LifePersonaId {
    try {
      if (typeof localStorage === 'undefined') {
        return 'adult';
      }
      const saved = localStorage.getItem(PERSONA_STORAGE_KEY);
      if (
        saved === 'kid' ||
        saved === 'teen' ||
        saved === 'student' ||
        saved === 'adult' ||
        saved === 'business' ||
        saved === 'veteran'
      ) {
        return saved;
      }
    } catch {
      // Ignore storage access failures in restricted browser environments.
    }
    return 'adult';
  }

  private persistPersonaId(personaId: LifePersonaId): void {
    try {
      if (typeof localStorage !== 'undefined') {
        localStorage.setItem(PERSONA_STORAGE_KEY, personaId);
      }
    } catch {
      // Ignore storage write failures in restricted browser environments.
    }
  }
}
