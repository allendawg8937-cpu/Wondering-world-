// src/app/data-access/models/persona.model.ts

export type LifePersonaId =
  | 'kid'
  | 'teen'
  | 'student'
  | 'adult'
  | 'business'
  | 'veteran';

export interface PersonaMetric {
  label: string;
  value: string;
}

export interface PersonaModuleShortcut {
  title: string;
  subtitle: string;
  route: string;
}

export interface PersonaDefinition {
  id: LifePersonaId;
  label: string;
  shortCode: string;
  ageRange: string;
  tagline: string;
  summary: string;
  accentColor: string;
  badgeBackground: string;
  primaryCta: string;
  focusAreas: readonly string[];
  metrics: readonly PersonaMetric[];
  recommendedModules: readonly PersonaModuleShortcut[];
}

export type PersonaChecklistMap = Record<LifePersonaId, readonly string[]>;
