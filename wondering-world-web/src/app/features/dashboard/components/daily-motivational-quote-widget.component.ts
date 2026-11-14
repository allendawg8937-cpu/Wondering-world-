// src/app/features/dashboard/components/daily-motivational-quote-widget.component.ts
import { Component, computed, inject, Input, signal } from '@angular/core';
import { LifePersonaId } from '../../../data-access/models/persona.model';
import { ProfileWidgetInstance } from '../../../data-access/models/profile-customization.model';
import { PersonaService } from '../../../data-access/services/persona.service';

export interface PersonaDailyQuote {
  id: string;
  personaId: LifePersonaId;
  quote: string;
  author: string;
  encouragementTip: string;
  focusBadge: string;
}

const FAVORITE_QUOTES_KEY = 'wondering_world_favorite_quotes_v1';

export const PERSONA_QUOTE_LIBRARY: Record<
  LifePersonaId,
  readonly PersonaDailyQuote[]
> = {
  kid: [
    {
      id: 'kid_q1',
      personaId: 'kid',
      quote:
        'You are braver than you believe, stronger than you seem, and smarter than you think.',
      author: 'A.A. Milne',
      encouragementTip:
        'Try one new puzzle, drawing, or science question today—curiosity is your superpower!',
      focusBadge: 'Curiosity Spark',
    },
    {
      id: 'kid_q2',
      personaId: 'kid',
      quote:
        'Every mistake is proof that you are trying and learning something brand new.',
      author: 'Cosmic Explorer Club',
      encouragementTip:
        'Celebrate effort over perfection—each try levels up your brain!',
      focusBadge: 'Growth Explorer',
    },
    {
      id: 'kid_q3',
      personaId: 'kid',
      quote:
        'A single kind word can brighten someone’s whole day like sunshine after rain.',
      author: 'Wondering World Kids Circle',
      encouragementTip:
        'Share one kind thank-you or high-five with a friend or family member today.',
      focusBadge: 'Kindness Quest',
    },
  ],
  teen: [
    {
      id: 'teen_q1',
      personaId: 'teen',
      quote:
        'Don’t wait for inspiration to strike—create your own momentum by building one small thing today.',
      author: 'Teen Creator Collective',
      encouragementTip:
        'Spend 20 focused minutes on a beat, sketch, or coding experiment.',
      focusBadge: 'Creator Momentum',
    },
    {
      id: 'teen_q2',
      personaId: 'teen',
      quote:
        'Your voice, your creativity, and your perspective are needed in rooms you haven’t even entered yet.',
      author: 'Youth Leadership Forum',
      encouragementTip:
        'Speak up thoughtfully in class, club practice, or a group discussion today.',
      focusBadge: 'Authentic Voice',
    },
    {
      id: 'teen_q3',
      personaId: 'teen',
      quote:
        'Small daily discipline beats once-a-month bursts of motivation every single time.',
      author: 'Hackathon Mentors',
      encouragementTip:
        'Finish your top homework or skill practice block before scrolling feeds tonight.',
      focusBadge: 'Daily Focus',
    },
  ],
  student: [
    {
      id: 'student_q1',
      personaId: 'student',
      quote:
        'Success is the sum of small efforts, repeated day in and day out.',
      author: 'Robert Collier',
      encouragementTip:
        'Complete one 25-minute flashcard sprint and review tomorrow’s hardest notes.',
      focusBadge: 'Study Mastery',
    },
    {
      id: 'student_q2',
      personaId: 'student',
      quote:
        'Education is the passport to the future, for tomorrow belongs to those who prepare for it today.',
      author: 'Malcolm X',
      encouragementTip:
        'Check one FAFSA, grant, or paid internship deadline on your semester calendar.',
      focusBadge: 'Academic Horizon',
    },
    {
      id: 'student_q3',
      personaId: 'student',
      quote:
        'Asking questions isn’t falling behind—it’s how scholars turn confusion into clarity.',
      author: 'Campus Study Mentors',
      encouragementTip:
        'Message a study partner or ask one clarifying question in office hours.',
      focusBadge: 'Curious Scholar',
    },
  ],
  adult: [
    {
      id: 'adult_q1',
      personaId: 'adult',
      quote:
        'Balance is not something you find; it is something you design intentionally each morning.',
      author: 'Jana Kingsford',
      encouragementTip:
        'Protect 20 minutes today for movement, reflection, or unhurried family time.',
      focusBadge: 'Intentional Living',
    },
    {
      id: 'adult_q2',
      personaId: 'adult',
      quote:
        'Peace of mind is built one steady habit at a time—automated savings, honest rest, and clear boundaries.',
      author: 'Household Resilience Circle',
      encouragementTip:
        'Do a quick two-minute review of your emergency reserve and weekly priorities.',
      focusBadge: 'Steady Stability',
    },
    {
      id: 'adult_q3',
      personaId: 'adult',
      quote:
        'You don’t have to carry every responsibility alone; strong communities thrive when neighbors share support.',
      author: 'Wondering World Community',
      encouragementTip:
        'Check in with a trusted friend or explore local community resources today.',
      focusBadge: 'Connected Strength',
    },
  ],
  business: [
    {
      id: 'business_q1',
      personaId: 'business',
      quote:
        'Vision without execution is just a daydream; consistent customer care turns ideas into lasting enterprises.',
      author: 'Founders Roundtable',
      encouragementTip:
        'Follow up personally with one loyal customer, partner, or prospective lead today.',
      focusBadge: 'Founder Execution',
    },
    {
      id: 'business_q2',
      personaId: 'business',
      quote:
        'Protect your cash flow, clarify your metrics, and build systems that serve your team on busy days.',
      author: 'SBA Growth Mentors',
      encouragementTip:
        'Review your operating runway and pick the single highest-impact task on your board.',
      focusBadge: 'Capital Discipline',
    },
    {
      id: 'business_q3',
      personaId: 'business',
      quote:
        'Great leaders build environments where teammates, partners, and communities win together.',
      author: 'Main Street Innovators',
      encouragementTip:
        'Recognize one teammate or local collaborator who moved a project forward this week.',
      focusBadge: 'Servant Leadership',
    },
  ],
  veteran: [
    {
      id: 'veteran_q1',
      personaId: 'veteran',
      quote:
        'Your service forged discipline, adaptability, and teamwork—strengths that anchor every chapter ahead.',
      author: 'Veterans Transition Network',
      encouragementTip:
        'Connect with a fellow veteran peer or review an earned VA or GI Bill benefit today.',
      focusBadge: 'Service Legacy',
    },
    {
      id: 'veteran_q2',
      personaId: 'veteran',
      quote:
        'True strength includes standing shoulder-to-shoulder with peers who understand your path.',
      author: 'DAV & VFW Peer Mentors',
      encouragementTip:
        'Reach out to a battle buddy or mentor today—your experience strengthens someone else.',
      focusBadge: 'Camaraderie',
    },
    {
      id: 'veteran_q3',
      personaId: 'veteran',
      quote:
        'Transitioning missions doesn’t end your purpose; it opens new ways to lead, mentor, and build at home.',
      author: 'Veteran Founders Guild',
      encouragementTip:
        'Log one career milestone, mentorship note, or personal wellness goal this week.',
      focusBadge: 'Next Mission',
    },
  ],
};

export function getDayOfYear(date: Date = new Date()): number {
  const start = new Date(date.getFullYear(), 0, 0);
  const diff = date.getTime() - start.getTime();
  return Math.floor(diff / 86_400_000);
}

export function selectDailyQuoteForPersona(
  personaId: LifePersonaId,
  dayOfYear: number = getDayOfYear(),
  offset: number = 0
): PersonaDailyQuote {
  const pool = PERSONA_QUOTE_LIBRARY[personaId] ?? PERSONA_QUOTE_LIBRARY.adult;
  const rawIndex = (dayOfYear + offset) % pool.length;
  const safeIndex = rawIndex < 0 ? rawIndex + pool.length : rawIndex;
  return pool[safeIndex] ?? pool[0];
}

@Component({
  selector: 'app-daily-motivational-quote-widget',
  standalone: true,
  template: `
    <article
      class="quote-widget"
      aria-label="Daily motivational quote for active persona"
      data-testid="daily-motivational-quote-widget"
      [style.border-color]="activePersona().accentColor"
      [style.--quote-accent]="activePersona().accentColor"
    >
      <header class="quote-header">
        <div class="header-title-row">
          <span class="persona-badge" data-testid="quote-persona-badge">
            {{ activePersona().label }} Daily Motivation
          </span>
          <span class="focus-chip" data-testid="quote-focus-badge">
            {{ activeQuote().focusBadge }}
          </span>
        </div>

        <button
          type="button"
          class="fav-btn"
          [class.saved]="isFavorited()"
          [attr.aria-pressed]="isFavorited()"
          data-testid="quote-favorite-btn"
          (click)="toggleFavorite()"
        >
          {{ isFavorited() ? '★ Saved' : '☆ Save' }}
        </button>
      </header>

      <blockquote class="quote-body" data-testid="quote-text">
        “{{ activeQuote().quote }}”
      </blockquote>

      @if (showAuthor) {
        <p class="quote-author" data-testid="quote-author">
          — {{ activeQuote().author }}
        </p>
      }

      @if (showEncouragementTip) {
        <div class="encouragement-box" data-testid="quote-encouragement-tip">
          <strong>Today’s Encouragement:</strong>
          <span>{{ activeQuote().encouragementTip }}</span>
        </div>
      }

      <footer class="quote-actions">
        <div class="nav-group">
          <button
            type="button"
            class="action-btn"
            data-testid="quote-prev-btn"
            (click)="stepQuote(-1)"
          >
            ← Prev
          </button>
          <button
            type="button"
            class="action-btn primary"
            data-testid="quote-next-btn"
            (click)="stepQuote(1)"
          >
            Next Quote →
          </button>
          @if (currentOffset() !== 0) {
            <button
              type="button"
              class="action-btn"
              data-testid="quote-today-btn"
              (click)="resetToToday()"
            >
              Today’s Pick
            </button>
          }
        </div>

        <div class="share-group">
          <button
            type="button"
            class="action-btn share-image-btn"
            data-testid="quote-export-image-btn"
            (click)="exportQuoteImage()"
          >
            {{ exportStatus() || '🖼️ Share Image' }}
          </button>
          <button
            type="button"
            class="action-btn"
            data-testid="quote-copy-btn"
            (click)="copyQuote()"
          >
            {{ copiedFeedback() ? '✓ Copied' : 'Copy Quote' }}
          </button>
        </div>
      </footer>
    </article>
  `,
  styles: [
    `
      .quote-widget {
        background: linear-gradient(145deg, #0f172a, #111827);
        border: 1px solid var(--quote-accent, #38bdf8);
        border-radius: 16px;
        padding: 18px;
        display: grid;
        gap: 12px;
        color: #f8fafc;
      }

      .quote-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 10px;
        flex-wrap: wrap;
      }

      .header-title-row {
        display: flex;
        align-items: center;
        gap: 8px;
        flex-wrap: wrap;
      }

      .persona-badge {
        background: color-mix(in srgb, var(--quote-accent, #38bdf8) 18%, transparent);
        color: var(--quote-accent, #38bdf8);
        border: 1px solid var(--quote-accent, #38bdf8);
        border-radius: 999px;
        padding: 4px 10px;
        font-size: 0.78rem;
        font-weight: 700;
      }

      .focus-chip {
        background: #1e293b;
        color: #e2e8f0;
        border-radius: 999px;
        padding: 4px 10px;
        font-size: 0.75rem;
        font-weight: 600;
      }

      .quote-body {
        margin: 0;
        font-size: 1.12rem;
        line-height: 1.5;
        font-style: italic;
        font-weight: 600;
      }

      .quote-author {
        margin: 0;
        font-size: 0.88rem;
        color: var(--quote-accent, #38bdf8);
        font-weight: 700;
      }

      .encouragement-box {
        background: rgba(16, 185, 129, 0.12);
        border: 1px solid rgba(16, 185, 129, 0.45);
        border-radius: 12px;
        padding: 10px 12px;
        display: flex;
        gap: 6px;
        flex-wrap: wrap;
        font-size: 0.88rem;
        line-height: 1.4;
      }

      .quote-actions {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 8px;
        flex-wrap: wrap;
      }

      .nav-group,
      .share-group {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
      }

      .action-btn,
      .fav-btn {
        background: #1e293b;
        color: #f8fafc;
        border: 1px solid #334155;
        border-radius: 10px;
        padding: 6px 12px;
        font-size: 0.82rem;
        font-weight: 600;
        cursor: pointer;
      }

      .action-btn.primary {
        background: var(--quote-accent, #38bdf8);
        color: #020617;
        border-color: var(--quote-accent, #38bdf8);
      }

      .action-btn.share-image-btn {
        border-color: var(--quote-accent, #38bdf8);
        color: var(--quote-accent, #38bdf8);
      }

      .fav-btn.saved {
        border-color: #f59e0b;
        color: #fbbf24;
      }
    `,
  ],
})
export class DailyMotivationalQuoteWidgetComponent {
  private readonly personaService = inject(PersonaService);

  @Input() widget?: ProfileWidgetInstance;

  private readonly offsetsSignal = signal<
    Partial<Record<LifePersonaId, number>>
  >({});
  private readonly favoritesSignal = signal<readonly string[]>(
    this.loadFavorites()
  );
  readonly copiedFeedback = signal(false);
  readonly exportStatus = signal<string | null>(null);

  readonly activePersona = this.personaService.activePersona;

  readonly currentOffset = computed<number>(() => {
    const id = this.personaService.activePersonaId();
    return this.offsetsSignal()[id] ?? 0;
  });

  readonly activeQuote = computed<PersonaDailyQuote>(() => {
    const id = this.personaService.activePersonaId();
    return selectDailyQuoteForPersona(id, getDayOfYear(), this.currentOffset());
  });

  readonly isFavorited = computed<boolean>(() =>
    this.favoritesSignal().includes(this.activeQuote().id)
  );

  get showAuthor(): boolean {
    return this.widget?.quoteConfig?.showAuthor ?? true;
  }

  get showEncouragementTip(): boolean {
    return this.widget?.quoteConfig?.showEncouragementTip ?? true;
  }

  stepQuote(delta: number): void {
    const id = this.personaService.activePersonaId();
    this.offsetsSignal.update(current => ({
      ...current,
      [id]: (current[id] ?? 0) + delta,
    }));
  }

  resetToToday(): void {
    const id = this.personaService.activePersonaId();
    this.offsetsSignal.update(current => ({
      ...current,
      [id]: 0,
    }));
  }

  toggleFavorite(): void {
    const quoteId = this.activeQuote().id;
    this.favoritesSignal.update(current => {
      const next = current.includes(quoteId)
        ? current.filter(item => item !== quoteId)
        : [...current, quoteId];
      this.saveFavorites(next);
      return next;
    });
  }

  copyQuote(): void {
    const q = this.activeQuote();
    const formatted = `"${q.quote}" — ${q.author}`;
    if (typeof navigator !== 'undefined' && navigator.clipboard?.writeText) {
      navigator.clipboard.writeText(formatted).catch(() => {});
    }
    this.copiedFeedback.set(true);
    setTimeout(() => this.copiedFeedback.set(false), 1800);
  }

  async exportQuoteImage(): Promise<void> {
    const persona = this.activePersona();
    const quote = this.activeQuote();
    const canvas = this.renderPersonaQuoteCanvas(persona, quote);
    if (!canvas) {
      this.exportStatus.set('✓ Ready');
      setTimeout(() => this.exportStatus.set(null), 2000);
      return;
    }

    const blob = await new Promise<Blob | null>(resolve =>
      canvas.toBlob(result => resolve(result), 'image/png')
    );
    if (!blob) {
      this.exportStatus.set('Export unavailable');
      setTimeout(() => this.exportStatus.set(null), 2000);
      return;
    }

    const fileName = `stages-of-life-${persona.id}-${quote.id}.png`;
    if (
      typeof navigator !== 'undefined' &&
      typeof navigator.share === 'function' &&
      typeof File !== 'undefined'
    ) {
      const file = new File([blob], fileName, { type: 'image/png' });
      const shareData: ShareData = {
        title: `${persona.label} Daily Motivation`,
        text: `"${quote.quote}" — ${quote.author}`,
        files: [file],
      };
      if (!navigator.canShare || navigator.canShare(shareData)) {
        try {
          await navigator.share(shareData);
          this.exportStatus.set('✓ Shared Image');
          setTimeout(() => this.exportStatus.set(null), 2200);
          return;
        } catch {
          // Fall through to PNG download.
        }
      }
    }

    if (typeof URL !== 'undefined' && typeof document !== 'undefined') {
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = fileName;
      link.click();
      URL.revokeObjectURL(url);
      this.exportStatus.set('✓ Downloaded PNG');
      setTimeout(() => this.exportStatus.set(null), 2200);
    }
  }

  renderPersonaQuoteCanvas(
    persona = this.activePersona(),
    quote = this.activeQuote()
  ): HTMLCanvasElement | null {
    if (typeof document === 'undefined') {
      return null;
    }
    const canvas = document.createElement('canvas');
    canvas.width = 1080;
    canvas.height = 1080;
    const ctx = canvas.getContext('2d');
    if (!ctx) {
      return null;
    }

    const grad = ctx.createLinearGradient(0, 0, 1080, 1080);
    grad.addColorStop(0, '#090e1a');
    grad.addColorStop(0.72, '#131c31');
    grad.addColorStop(1, persona.accentColor);
    ctx.fillStyle = grad;
    ctx.fillRect(0, 0, 1080, 1080);

    ctx.fillStyle = 'rgba(15, 23, 42, 0.88)';
    ctx.strokeStyle = persona.accentColor;
    ctx.lineWidth = 4;
    ctx.beginPath();
    ctx.roundRect(56, 56, 968, 968, 36);
    ctx.fill();
    ctx.stroke();

    ctx.fillStyle = persona.accentColor;
    ctx.font = 'bold 30px sans-serif';
    ctx.fillText(
      `${persona.label.toUpperCase()} MODE • ${quote.focusBadge.toUpperCase()}`,
      108,
      152
    );

    ctx.fillStyle = '#f8fafc';
    ctx.font = 'italic bold 44px serif';
    this.drawWrappedText(ctx, `"${quote.quote}"`, 108, 290, 864, 58);

    ctx.fillStyle = persona.accentColor;
    ctx.font = 'bold 32px sans-serif';
    ctx.fillText(`— ${quote.author}`, 108, 690);

    ctx.fillStyle = '#d1fae5';
    ctx.font = '600 26px sans-serif';
    this.drawWrappedText(
      ctx,
      `Daily Boost: ${quote.encouragementTip}`,
      108,
      780,
      864,
      36
    );

    ctx.fillStyle = '#94a3b8';
    ctx.font = 'bold 24px sans-serif';
    ctx.fillText(
      'STAGES OF LIFE • WE ARE ALL EQUAL • wereallequals.world',
      108,
      970
    );
    return canvas;
  }

  private drawWrappedText(
    ctx: CanvasRenderingContext2D,
    text: string,
    x: number,
    y: number,
    maxWidth: number,
    lineHeight: number
  ): void {
    const words = text.split(' ');
    let line = '';
    let cursorY = y;
    for (const word of words) {
      const candidate = line ? `${line} ${word}` : word;
      if (ctx.measureText(candidate).width > maxWidth && line) {
        ctx.fillText(line, x, cursorY);
        line = word;
        cursorY += lineHeight;
      } else {
        line = candidate;
      }
    }
    if (line) {
      ctx.fillText(line, x, cursorY);
    }
  }

  private loadFavorites(): readonly string[] {
    try {
      if (typeof localStorage === 'undefined') {
        return [];
      }
      const raw = localStorage.getItem(FAVORITE_QUOTES_KEY);
      const parsed = raw ? JSON.parse(raw) : [];
      return Array.isArray(parsed)
        ? parsed.filter((x): x is string => typeof x === 'string')
        : [];
    } catch {
      return [];
    }
  }

  private saveFavorites(ids: readonly string[]): void {
    try {
      if (typeof localStorage !== 'undefined') {
        localStorage.setItem(FAVORITE_QUOTES_KEY, JSON.stringify(ids));
      }
    } catch {
      // Ignore storage write errors.
    }
  }
}
