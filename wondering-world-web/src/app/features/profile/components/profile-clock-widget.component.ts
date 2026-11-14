// src/app/features/profile/components/profile-clock-widget.component.ts
import {
  Component,
  DestroyRef,
  inject,
  Input,
  OnInit,
  signal,
} from '@angular/core';
import {
  ProfileClockConfig,
  ProfileWidgetInstance,
} from '../../../data-access/models/profile-customization.model';
import { ProfileCustomizationService } from '../../../data-access/services/profile-customization.service';

const TIMEZONE_OPTIONS: readonly { label: string; value: string }[] = [
  { label: 'Local Device', value: 'Local' },
  { label: 'New York (ET)', value: 'America/New_York' },
  { label: 'Los Angeles (PT)', value: 'America/Los_Angeles' },
  { label: 'London (GMT)', value: 'Europe/London' },
  { label: 'Tokyo (JST)', value: 'Asia/Tokyo' },
];

@Component({
  selector: 'app-profile-clock-widget',
  standalone: true,
  template: `
    <div class="clock-widget" data-testid="widget-clock-body">
      <div class="time-display" data-testid="widget-clock-time">
        {{ formattedTime() }}
      </div>
      <div class="date-display">{{ formattedDate() }} · {{ config().timeZone }}</div>

      <div class="clock-controls">
        <button
          type="button"
          class="mini-btn"
          data-testid="widget-clock-format-btn"
          (click)="toggle24Hour()"
        >
          {{ config().format24Hour ? '24h' : '12h' }}
        </button>
        <button
          type="button"
          class="mini-btn"
          data-testid="widget-clock-seconds-btn"
          (click)="toggleSeconds()"
        >
          {{ config().showSeconds ? 'Sec: On' : 'Sec: Off' }}
        </button>
        <select
          class="mini-select"
          aria-label="Clock timezone"
          [value]="config().timeZone"
          (change)="onTimezoneChange($event)"
        >
          @for (tz of timezones; track tz.value) {
            <option [value]="tz.value">{{ tz.label }}</option>
          }
        </select>
      </div>
    </div>
  `,
  styles: [
    `
      .clock-widget {
        display: grid;
        gap: 10px;
      }

      .time-display {
        font-size: 1.9rem;
        font-weight: 800;
        letter-spacing: 0.04em;
        color: var(--profile-accent, #38bdf8);
        font-variant-numeric: tabular-nums;
      }

      .date-display {
        font-size: 0.84rem;
        color: #94a3b8;
      }

      .clock-controls {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        align-items: center;
      }

      .mini-btn,
      .mini-select {
        min-height: 34px;
        padding: 4px 10px;
        border-radius: 8px;
        border: 1px solid #334155;
        background: #0f172a;
        color: #f8fafc;
        font-size: 0.8rem;
        font-weight: 600;
        cursor: pointer;
      }
    `,
  ],
})
export class ProfileClockWidgetComponent implements OnInit {
  private readonly customizationService = inject(ProfileCustomizationService);
  private readonly destroyRef = inject(DestroyRef);

  @Input({ required: true }) widget!: ProfileWidgetInstance;

  readonly timezones = TIMEZONE_OPTIONS;
  readonly now = signal<Date>(new Date());

  ngOnInit(): void {
    const timer = setInterval(() => {
      this.now.set(new Date());
    }, 1000);
    this.destroyRef.onDestroy(() => clearInterval(timer));
  }

  config(): ProfileClockConfig {
    return (
      this.widget.clockConfig ?? {
        format24Hour: false,
        showSeconds: true,
        timeZone: 'Local',
        label: 'Local Time',
      }
    );
  }

  formattedTime(): string {
    const cfg = this.config();
    try {
      return new Intl.DateTimeFormat(undefined, {
        hour: '2-digit',
        minute: '2-digit',
        second: cfg.showSeconds ? '2-digit' : undefined,
        hour12: !cfg.format24Hour,
        timeZone: cfg.timeZone === 'Local' ? undefined : cfg.timeZone,
      }).format(this.now());
    } catch {
      return this.now().toLocaleTimeString();
    }
  }

  formattedDate(): string {
    const cfg = this.config();
    try {
      return new Intl.DateTimeFormat(undefined, {
        weekday: 'short',
        month: 'short',
        day: 'numeric',
        timeZone: cfg.timeZone === 'Local' ? undefined : cfg.timeZone,
      }).format(this.now());
    } catch {
      return this.now().toDateString();
    }
  }

  toggle24Hour(): void {
    this.customizationService.updateClockConfig(this.widget.id, {
      format24Hour: !this.config().format24Hour,
    });
  }

  toggleSeconds(): void {
    this.customizationService.updateClockConfig(this.widget.id, {
      showSeconds: !this.config().showSeconds,
    });
  }

  onTimezoneChange(event: Event): void {
    const select = event.target as HTMLSelectElement | null;
    if (select?.value) {
      this.customizationService.updateClockConfig(this.widget.id, {
        timeZone: select.value,
      });
    }
  }
}
