// src/app/data-access/services/community-resources.service.ts
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  PersonaCommunityBundle,
  PersonaCommunityFeedPayload,
} from '../models/community-resource.model';
import { LifePersonaId } from '../models/persona.model';
import { PersonaService } from './persona.service';

@Injectable({
  providedIn: 'root',
})
export class CommunityResourcesService {
  private readonly http = inject(HttpClient);
  private readonly personaService = inject(PersonaService);

  getResourcesForPersona(
    personaId: LifePersonaId
  ): Observable<PersonaCommunityBundle> {
    const endpoint = `/community-resources/${encodeURIComponent(personaId)}.json`;
    return this.http.get<PersonaCommunityFeedPayload>(endpoint).pipe(
      map(payload => ({
        ...payload,
        fetchedAt: new Date().toISOString(),
        source: 'dynamic-feed' as const,
      }))
    );
  }

  getOfflineFallback(personaId: LifePersonaId): Observable<PersonaCommunityBundle> {
    const persona = this.personaService.getPersonaById(personaId);
    return of({
      personaId: persona.id,
      personaLabel: persona.label,
      headline: `${persona.label} Stage Curated Starter Guide`,
      summary: persona.summary,
      fetchedAt: new Date().toISOString(),
      source: 'offline-fallback',
      advice: persona.focusAreas.map((focus, idx) => ({
        id: `${persona.id}-fallback-adv-${idx + 1}`,
        title: `${persona.label} Priority #${idx + 1}`,
        guidance: focus,
        actionStep: persona.primaryCta,
        category: 'Community' as const,
      })),
      links: persona.recommendedModules.map((mod, idx) => ({
        id: `${persona.id}-fallback-lnk-${idx + 1}`,
        title: mod.title,
        description: mod.subtitle,
        url: mod.route,
        provider: 'Wondering World',
        category: 'Support' as const,
        isExternal: false,
      })),
    });
  }
}
