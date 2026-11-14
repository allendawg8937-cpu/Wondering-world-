// src/app/features/profile/components/profile-weather-widget.component.ts
import {
  Component,
  DestroyRef,
  inject,
  Input,
  OnInit,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  ProfileWeatherConfig,
  ProfileWidgetInstance,
  WeatherSnapshot,
} from '../../../data-access/models/profile-customization.model';
import { ProfileCustomizationService } from '../../../data-access/services/profile-customization.service';

@Component({
  selector: 'app-profile-weather-widget',
  standalone: true,
  template: `
    <div class="weather-widget" data-testid="widget-weather-body">
      @if (forecast(); as data) {
        <div class="weather-main">
          <strong class="temp-value" data-testid="widget-weather-temp">
            {{ data.temperature }}°{{ data.unit }}
          </strong>
          <div class="weather-meta">
            <span class="condition">{{ data.condition }}</span>
            <span class="wind">Wind {{ data.windSpeed }} km/h · {{ data.city }}</span>
          </div>
        </div>
      } @else {
        <p class="loading-text">Loading forecast...</p>
      }

      <div class="weather-controls">
        <select
          class="mini-select"
          aria-label="Select forecast city"
          [value]="config().city"
          (change)="onCityChange($event)"
        >
          @for (city of cities; track city.city) {
            <option [value]="city.city">{{ city.city }}</option>
          }
        </select>

        <button
          type="button"
          class="mini-btn"
          data-testid="widget-weather-unit-btn"
          (click)="toggleUnit()"
        >
          °{{ config().unit }}
        </button>

        <button
          type="button"
          class="mini-btn"
          data-testid="widget-weather-refresh-btn"
          (click)="loadForecast()"
        >
          Refresh
        </button>
      </div>
    </div>
  `,
  styles: [
    `
      .weather-widget {
        display: grid;
        gap: 12px;
      }

      .weather-main {
        display: flex;
        align-items: center;
        gap: 14px;
      }

      .temp-value {
        font-size: 2rem;
        font-weight: 800;
        color: var(--profile-accent, #38bdf8);
      }

      .weather-meta {
        display: grid;
        gap: 2px;
      }

      .condition {
        font-weight: 600;
        color: #f8fafc;
      }

      .wind,
      .loading-text {
        margin: 0;
        font-size: 0.82rem;
        color: #94a3b8;
      }

      .weather-controls {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
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
export class ProfileWeatherWidgetComponent implements OnInit {
  private readonly customizationService = inject(ProfileCustomizationService);
  private readonly destroyRef = inject(DestroyRef);

  @Input({ required: true }) widget!: ProfileWidgetInstance;

  readonly cities = this.customizationService.weatherCities;
  readonly forecast = signal<WeatherSnapshot | null>(null);

  ngOnInit(): void {
    this.loadForecast();
  }

  config(): ProfileWeatherConfig {
    return (
      this.widget.weatherConfig ?? {
        city: this.cities[0].city,
        latitude: this.cities[0].latitude,
        longitude: this.cities[0].longitude,
        unit: 'F',
      }
    );
  }

  loadForecast(): void {
    this.customizationService
      .fetchWeatherForecast(this.config())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(snapshot => {
        this.forecast.set(snapshot);
      });
  }

  toggleUnit(): void {
    const nextUnit = this.config().unit === 'F' ? 'C' : 'F';
    this.customizationService.updateWeatherConfig(this.widget.id, {
      unit: nextUnit,
    });
    this.widget = {
      ...this.widget,
      weatherConfig: { ...this.config(), unit: nextUnit },
    };
    this.loadForecast();
  }

  onCityChange(event: Event): void {
    const select = event.target as HTMLSelectElement | null;
    const found = this.cities.find(c => c.city === select?.value);
    if (!found) {
      return;
    }
    const patch: ProfileWeatherConfig = {
      city: found.city,
      latitude: found.latitude,
      longitude: found.longitude,
      unit: this.config().unit,
    };
    this.customizationService.updateWeatherConfig(this.widget.id, patch);
    this.widget = { ...this.widget, weatherConfig: patch };
    this.loadForecast();
  }
}
