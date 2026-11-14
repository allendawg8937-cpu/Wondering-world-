// src/app/data-access/services/community-resources.service.spec.ts
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { PersonaCommunityFeedPayload } from '../models/community-resource.model';
import { LifePersonaId } from '../models/persona.model';
import { CommunityResourcesService } from './community-resources.service';

describe('CommunityResourcesService', () => {
  let service: CommunityResourcesService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(CommunityResourcesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('fetches curated resources dynamically from the persona JSON endpoint', () => {
    const personas: LifePersonaId[] = [
      'kid',
      'teen',
      'student',
      'adult',
      'business',
      'veteran',
    ];

    for (const id of personas) {
      let receivedId: LifePersonaId | undefined;
      let receivedSource: string | undefined;

      service.getResourcesForPersona(id).subscribe(bundle => {
        receivedId = bundle.personaId;
        receivedSource = bundle.source;
      });

      const req = httpMock.expectOne(`/community-resources/${id}.json`);
      expect(req.request.method).toBe('GET');

      const mockPayload: PersonaCommunityFeedPayload = {
        personaId: id,
        personaLabel: id.toUpperCase(),
        headline: `${id} guidance`,
        summary: `Curated feed for ${id}`,
        advice: [],
        links: [],
      };
      req.flush(mockPayload);

      expect(receivedId).toBe(id);
      expect(receivedSource).toBe('dynamic-feed');
    }
  });

  it('provides an offline fallback bundle tailored to the requested persona', () => {
    let fallbackPersonaId: LifePersonaId | undefined;
    let fallbackSource: string | undefined;
    let adviceCount = 0;

    service.getOfflineFallback('veteran').subscribe(bundle => {
      fallbackPersonaId = bundle.personaId;
      fallbackSource = bundle.source;
      adviceCount = bundle.advice.length;
    });

    expect(fallbackPersonaId).toBe('veteran');
    expect(fallbackSource).toBe('offline-fallback');
    expect(adviceCount).toBeGreaterThan(0);
  });
});
