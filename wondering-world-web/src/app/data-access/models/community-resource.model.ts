// src/app/data-access/models/community-resource.model.ts
import { LifePersonaId } from './persona.model';

export type ResourceCategory =
  | 'Education'
  | 'Wellness'
  | 'Finance'
  | 'Career'
  | 'Community'
  | 'Support';

export interface CommunityAdviceItem {
  id: string;
  title: string;
  guidance: string;
  actionStep: string;
  category: ResourceCategory;
}

export interface CuratedResourceLink {
  id: string;
  title: string;
  description: string;
  url: string;
  provider: string;
  category: ResourceCategory;
  isExternal: boolean;
}

export interface PersonaCommunityFeedPayload {
  personaId: LifePersonaId;
  personaLabel: string;
  headline: string;
  summary: string;
  advice: readonly CommunityAdviceItem[];
  links: readonly CuratedResourceLink[];
}

export interface PersonaCommunityBundle extends PersonaCommunityFeedPayload {
  fetchedAt: string;
  source: 'dynamic-feed' | 'offline-fallback';
}
