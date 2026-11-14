// src/app/features/dashboard/components/daily-motivational-quote-widget.component.spec.ts
import { TestBed } from '@angular/core/testing';
import { PersonaService } from '../../../data-access/services/persona.service';
import {
  DailyMotivationalQuoteWidgetComponent,
  selectDailyQuoteForPersona,
} from './daily-motivational-quote-widget.component';

describe('DailyMotivationalQuoteWidgetComponent', () => {
  beforeEach(async () => {
    if (typeof localStorage !== 'undefined') {
      localStorage.clear();
    }
    await TestBed.configureTestingModule({
      imports: [DailyMotivationalQuoteWidgetComponent],
    }).compileComponents();
  });

  it('provides distinct daily motivational quotes across all six personas', () => {
    const ids = ['kid', 'teen', 'student', 'adult', 'business', 'veteran'] as const;
    const quotes = ids.map(id => selectDailyQuoteForPersona(id, 100));
    expect(new Set(quotes.map(q => q.id)).size).toBe(6);
    expect(new Set(quotes.map(q => q.quote)).size).toBe(6);
  });

  it('updates rendered quote and encouragement immediately when active persona changes', () => {
    const personaService = TestBed.inject(PersonaService);
    const fixture = TestBed.createComponent(
      DailyMotivationalQuoteWidgetComponent
    );
    personaService.switchPersona('kid');
    fixture.detectChanges();

    const host = fixture.nativeElement as HTMLElement;
    expect(host.querySelector('[data-testid="quote-persona-badge"]')?.textContent).toContain(
      'Kid Daily Motivation'
    );
    const kidQuoteText = host.querySelector('[data-testid="quote-text"]')?.textContent;

    personaService.switchPersona('veteran');
    fixture.detectChanges();

    expect(host.querySelector('[data-testid="quote-persona-badge"]')?.textContent).toContain(
      'Veteran Daily Motivation'
    );
    const veteranQuoteText = host.querySelector('[data-testid="quote-text"]')?.textContent;
    expect(veteranQuoteText).not.toEqual(kidQuoteText);
  });

  it('cycles to next quote and saves favorites', () => {
    const personaService = TestBed.inject(PersonaService);
    personaService.switchPersona('student');
    const fixture = TestBed.createComponent(
      DailyMotivationalQuoteWidgetComponent
    );
    fixture.detectChanges();

    const host = fixture.nativeElement as HTMLElement;
    const initialQuote = host.querySelector('[data-testid="quote-text"]')?.textContent;

    (host.querySelector('[data-testid="quote-next-btn"]') as HTMLButtonElement).click();
    fixture.detectChanges();
    const nextQuote = host.querySelector('[data-testid="quote-text"]')?.textContent;
    expect(nextQuote).not.toEqual(initialQuote);

    (host.querySelector('[data-testid="quote-favorite-btn"]') as HTMLButtonElement).click();
    fixture.detectChanges();
    expect(host.querySelector('[data-testid="quote-favorite-btn"]')?.textContent).toContain(
      'Saved'
    );
  });

  it('renders a persona-branded share image canvas and exposes export button', () => {
    const personaService = TestBed.inject(PersonaService);
    personaService.switchPersona('business');
    const fixture = TestBed.createComponent(
      DailyMotivationalQuoteWidgetComponent
    );
    fixture.detectChanges();

    const host = fixture.nativeElement as HTMLElement;
    const exportBtn = host.querySelector('[data-testid="quote-export-image-btn"]');
    expect(exportBtn).toBeTruthy();

    const canvas = fixture.componentInstance.renderPersonaQuoteCanvas();
    expect(canvas).toBeTruthy();
    expect(canvas?.width).toBe(1080);
    expect(canvas?.height).toBe(1080);
  });
});
