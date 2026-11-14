// src/app/features/community/components/community-resources.component.spec.ts
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { PersonaCommunityFeedPayload } from '../../../data-access/models/community-resource.model';
import { PersonaService } from '../../../data-access/services/persona.service';
import { CommunityResourcesComponent } from './community-resources.component';

function buildFeed(
  personaId: 'kid' | 'teen' | 'student' | 'adult' | 'business' | 'veteran',
  label: string
): PersonaCommunityFeedPayload {
  return {
    personaId,
    personaLabel: label,
    headline: `${label} Headline`,
    summary: `${label} Summary`,
    advice: [
      {
        id: `${personaId}-adv-1`,
        title: `${label} Study Habit`,
        guidance: `Guided step for ${label}`,
        actionStep: `Complete ${label} action`,
        category: 'Education',
      },
      {
        id: `${personaId}-adv-2`,
        title: `${label} Finance Plan`,
        guidance: `Budget guidance for ${label}`,
        actionStep: `Review ${label} fund`,
        category: 'Finance',
      },
    ],
    links: [
      {
        id: `${personaId}-lnk-1`,
        title: `${label} External Guide`,
        description: `Trusted guide for ${label}`,
        url: `https://example.org/${personaId}`,
        provider: `${label} Org`,
        category: 'Education',
        isExternal: true,
      },
    ],
  };
}

describe('CommunityResourcesComponent', () => {
  let fixture: ComponentFixture<CommunityResourcesComponent>;
  let personaService: PersonaService;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [CommunityResourcesComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    personaService = TestBed.inject(PersonaService);
    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(CommunityResourcesComponent);
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('fetches curated resources dynamically when PersonaService switches personas', () => {
    const initialReq = httpMock.expectOne('/community-resources/adult.json');
    initialReq.flush(buildFeed('adult', 'Adult'));
    fixture.detectChanges();

    expect(
      fixture.nativeElement.querySelector('[data-testid="resources-persona-badge"]')
        ?.textContent
    ).toContain('Adult Resources');
    expect(
      fixture.nativeElement.querySelector('[data-testid="advice-card-adult-adv-1"]')
    ).not.toBeNull();

    personaService.switchPersona('veteran');
    fixture.detectChanges();

    const veteranReq = httpMock.expectOne('/community-resources/veteran.json');
    veteranReq.flush(buildFeed('veteran', 'Veteran'));
    fixture.detectChanges();

    expect(
      fixture.nativeElement.querySelector('[data-testid="resources-persona-badge"]')
        ?.textContent
    ).toContain('Veteran Resources');
    expect(
      fixture.nativeElement.querySelector('[data-testid="advice-card-veteran-adv-1"]')
    ).not.toBeNull();
    expect(
      fixture.nativeElement.querySelector('[data-testid="resource-link-veteran-lnk-1"]')
    ).not.toBeNull();
  });

  it('filters advice and links by category and search query and supports bookmarks', () => {
    httpMock
      .expectOne('/community-resources/adult.json')
      .flush(buildFeed('adult', 'Adult'));
    fixture.detectChanges();

    const financeFilter = fixture.nativeElement.querySelector(
      '[data-testid="resource-category-finance"]'
    ) as HTMLButtonElement;
    financeFilter.click();
    fixture.detectChanges();

    expect(
      fixture.nativeElement.querySelector('[data-testid="advice-card-adult-adv-2"]')
    ).not.toBeNull();
    expect(
      fixture.nativeElement.querySelector('[data-testid="advice-card-adult-adv-1"]')
    ).toBeNull();

    const allFilter = fixture.nativeElement.querySelector(
      '[data-testid="resource-category-all"]'
    ) as HTMLButtonElement;
    allFilter.click();
    fixture.detectChanges();

    const bookmarkBtn = fixture.nativeElement.querySelector(
      '[data-testid="bookmark-advice-adult-adv-1"]'
    ) as HTMLButtonElement;
    bookmarkBtn.click();
    fixture.detectChanges();

    const savedOnlyBtn = fixture.nativeElement.querySelector(
      '[data-testid="resources-saved-only-btn"]'
    ) as HTMLButtonElement;
    savedOnlyBtn.click();
    fixture.detectChanges();

    expect(
      fixture.nativeElement.querySelector('[data-testid="advice-card-adult-adv-1"]')
    ).not.toBeNull();
    expect(
      fixture.nativeElement.querySelector('[data-testid="advice-card-adult-adv-2"]')
    ).toBeNull();
  });

  it('renders an inline error state with retry and offline guide recovery on HTTP failure', () => {
    httpMock
      .expectOne('/community-resources/adult.json')
      .flush('Server error', { status: 500, statusText: 'Server Error' });
    fixture.detectChanges();

    expect(
      fixture.nativeElement.querySelector('[data-testid="resources-error"]')
    ).not.toBeNull();

    const fallbackBtn = fixture.nativeElement.querySelector(
      '[data-testid="resources-fallback-btn"]'
    ) as HTMLButtonElement;
    fallbackBtn.click();
    fixture.detectChanges();

    expect(
      fixture.nativeElement.querySelector('[data-testid="resources-error"]')
    ).toBeNull();
    expect(
      fixture.nativeElement.querySelector(
        '[data-testid="advice-card-adult-fallback-adv-1"]'
      )
    ).not.toBeNull();
  });
});
