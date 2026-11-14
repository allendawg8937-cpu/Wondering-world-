// src/app/data-access/services/profile-customization.service.ts
import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import {
  CssSanitizationReport,
  ProfileClockConfig,
  ProfileCustomizationState,
  ProfileGameConfig,
  ProfileQuoteConfig,
  ProfileThemeId,
  ProfileThemePreset,
  ProfileWeatherConfig,
  ProfileWidgetInstance,
  ProfileWidgetType,
  WeatherSnapshot,
} from '../models/profile-customization.model';
import { CssSanitizerService } from './css-sanitizer.service';

const CUSTOMIZATION_STORAGE_KEY = 'wondering_world_profile_customization_v1';

export const PROFILE_THEMES: readonly ProfileThemePreset[] = [
  {
    id: 'midnight-nebula',
    name: 'Midnight Nebula',
    tagline: 'Deep slate canvas with electric sky highlights',
    surfaceBackground: 'linear-gradient(145deg, #0f172a, #1e1b4b)',
    cardBackground: '#111827',
    borderColor: '#334155',
    accentColor: '#38bdf8',
    textColor: '#f8fafc',
    fontFamily: 'Arial, sans-serif',
    swatchColors: ['#0f172a', '#1e1b4b', '#38bdf8'],
  },
  {
    id: 'sunset-arcade',
    name: 'Sunset Arcade',
    tagline: 'Warm dusk magenta paired with amber neon glow',
    surfaceBackground: 'linear-gradient(145deg, #2e1065, #7c2d12)',
    cardBackground: '#1f1235',
    borderColor: '#f97316',
    accentColor: '#fb923c',
    textColor: '#fff7ed',
    fontFamily: "'Trebuchet MS', sans-serif",
    swatchColors: ['#2e1065', '#7c2d12', '#fb923c'],
  },
  {
    id: 'emerald-zen',
    name: 'Emerald Zen',
    tagline: 'Calm forest slate with crisp mint accents',
    surfaceBackground: 'linear-gradient(145deg, #022c22, #0f172a)',
    cardBackground: '#064e3b',
    borderColor: '#10b981',
    accentColor: '#34d399',
    textColor: '#ecfdf5',
    fontFamily: 'Georgia, serif',
    swatchColors: ['#022c22', '#064e3b', '#34d399'],
  },
  {
    id: 'retro-myspace',
    name: 'Retro Zine 2006',
    tagline: 'Classic high-contrast Web 2.0 bulletin board vibe',
    surfaceBackground: 'linear-gradient(135deg, #172554, #1e293b)',
    cardBackground: '#0f172a',
    borderColor: '#f43f5e',
    accentColor: '#fb7185',
    textColor: '#ffe4e6',
    fontFamily: "'Courier New', monospace",
    swatchColors: ['#172554', '#0f172a', '#fb7185'],
  },
  {
    id: 'cyber-grid',
    name: 'Cyber Violet',
    tagline: 'Ultraviolet grid contrast with razor borders',
    surfaceBackground: 'linear-gradient(145deg, #090d16, #3b0764)',
    cardBackground: '#130924',
    borderColor: '#a855f7',
    accentColor: '#c084fc',
    textColor: '#faf5ff',
    fontFamily: "'Lucida Console', monospace",
    swatchColors: ['#090d16', '#3b0764', '#c084fc'],
  },
  {
    id: 'solar-gold',
    name: 'Solar Prestige',
    tagline: 'Obsidian studio slate with gilded brass trim',
    surfaceBackground: 'linear-gradient(145deg, #18181b, #451a03)',
    cardBackground: '#1c1917',
    borderColor: '#f59e0b',
    accentColor: '#fbbf24',
    textColor: '#fef3c7',
    fontFamily: 'Verdana, sans-serif',
    swatchColors: ['#18181b', '#451a03', '#fbbf24'],
  },
];

export interface WeatherCityPreset {
  city: string;
  latitude: number;
  longitude: number;
}

export const WEATHER_CITY_PRESETS: readonly WeatherCityPreset[] = [
  { city: 'Seattle, WA', latitude: 47.61, longitude: -122.33 },
  { city: 'New York, NY', latitude: 40.71, longitude: -74.01 },
  { city: 'Austin, TX', latitude: 30.27, longitude: -97.74 },
  { city: 'London, UK', latitude: 51.51, longitude: -0.13 },
  { city: 'Tokyo, JP', latitude: 35.68, longitude: 139.69 },
];

interface OpenMeteoResponse {
  current_weather?: {
    temperature?: number;
    windspeed?: number;
    weathercode?: number;
  };
}

function createDefaultWidgets(): ProfileWidgetInstance[] {
  return [
    {
      id: 'w_clock_default',
      type: 'clock',
      title: 'Live World Clock',
      clockConfig: {
        format24Hour: false,
        showSeconds: true,
        timeZone: 'Local',
        label: 'Local Time',
      },
    },
  ];
}

@Injectable({
  providedIn: 'root',
})
export class ProfileCustomizationService {
  private readonly http = inject(HttpClient);
  private readonly sanitizer = inject(CssSanitizerService);

  readonly themes = PROFILE_THEMES;
  readonly weatherCities = WEATHER_CITY_PRESETS;

  private readonly stateSignal = signal<ProfileCustomizationState>(
    this.loadState()
  );
  private readonly lastSanitizationSignal = signal<CssSanitizationReport>({
    sanitizedCss: this.stateSignal().sanitizedCss,
    blockedPatterns: [],
    ruleCount: this.stateSignal().sanitizedCss ? 1 : 0,
    wasModified: false,
  });

  readonly selectedThemeId = computed(() => this.stateSignal().themeId);
  readonly activeTheme = computed<ProfileThemePreset>(
    () =>
      this.themes.find(t => t.id === this.stateSignal().themeId) ??
      this.themes[0]
  );
  readonly customCssRaw = computed(() => this.stateSignal().customCssRaw);
  readonly sanitizedCss = computed(() => this.stateSignal().sanitizedCss);
  readonly widgets = computed(() => this.stateSignal().widgets);
  readonly sanitizationReport = this.lastSanitizationSignal.asReadonly();

  selectTheme(themeId: ProfileThemeId): ProfileThemePreset {
    const preset = this.themes.find(t => t.id === themeId) ?? this.themes[0];
    this.updateState(current => ({
      ...current,
      themeId: preset.id,
    }));
    return preset;
  }

  applyCustomCss(rawCss: string): CssSanitizationReport {
    const report = this.sanitizer.sanitizeProfileCss(rawCss);
    this.lastSanitizationSignal.set(report);
    this.updateState(current => ({
      ...current,
      customCssRaw: rawCss,
      sanitizedCss: report.sanitizedCss,
    }));
    return report;
  }

  clearCustomCss(): void {
    const emptyReport: CssSanitizationReport = {
      sanitizedCss: '',
      blockedPatterns: [],
      ruleCount: 0,
      wasModified: false,
    };
    this.lastSanitizationSignal.set(emptyReport);
    this.updateState(current => ({
      ...current,
      customCssRaw: '',
      sanitizedCss: '',
    }));
  }

  addWidget(type: ProfileWidgetType, customTitle?: string): ProfileWidgetInstance {
    const id = `w_${type}_${Date.now()}_${Math.floor(Math.random() * 1000)}`;
    const widget = this.buildDefaultWidget(id, type, customTitle);
    this.updateState(current => ({
      ...current,
      widgets: [...current.widgets, widget],
    }));
    return widget;
  }

  removeWidget(widgetId: string): void {
    this.updateState(current => ({
      ...current,
      widgets: current.widgets.filter(w => w.id !== widgetId),
    }));
  }

  moveWidget(widgetId: string, direction: 'up' | 'down'): void {
    this.updateState(current => {
      const list = [...current.widgets];
      const idx = list.findIndex(w => w.id === widgetId);
      if (idx < 0) {
        return current;
      }
      const targetIdx = direction === 'up' ? idx - 1 : idx + 1;
      if (targetIdx < 0 || targetIdx >= list.length) {
        return current;
      }
      const [moved] = list.splice(idx, 1);
      list.splice(targetIdx, 0, moved);
      return { ...current, widgets: list };
    });
  }

  updateClockConfig(
    widgetId: string,
    patch: Partial<ProfileClockConfig>
  ): void {
    this.updateState(current => ({
      ...current,
      widgets: current.widgets.map(w =>
        w.id === widgetId && w.clockConfig
          ? { ...w, clockConfig: { ...w.clockConfig, ...patch } }
          : w
      ),
    }));
  }

  updateWeatherConfig(
    widgetId: string,
    patch: Partial<ProfileWeatherConfig>
  ): void {
    this.updateState(current => ({
      ...current,
      widgets: current.widgets.map(w =>
        w.id === widgetId && w.weatherConfig
          ? { ...w, weatherConfig: { ...w.weatherConfig, ...patch } }
          : w
      ),
    }));
  }

  updateGameConfig(widgetId: string, patch: Partial<ProfileGameConfig>): void {
    this.updateState(current => ({
      ...current,
      widgets: current.widgets.map(w =>
        w.id === widgetId && w.gameConfig
          ? { ...w, gameConfig: { ...w.gameConfig, ...patch } }
          : w
      ),
    }));
  }

  updateQuoteConfig(widgetId: string, patch: Partial<ProfileQuoteConfig>): void {
    this.updateState(current => ({
      ...current,
      widgets: current.widgets.map(w =>
        w.id === widgetId && w.quoteConfig
          ? { ...w, quoteConfig: { ...w.quoteConfig, ...patch } }
          : w
      ),
    }));
  }

  fetchWeatherForecast(config: ProfileWeatherConfig): Observable<WeatherSnapshot> {
    const unitParam = config.unit === 'F' ? 'fahrenheit' : 'celsius';
    const url =
      `https://api.open-meteo.com/v1/forecast?latitude=${config.latitude}` +
      `&longitude=${config.longitude}&current_weather=true&temperature_unit=${unitParam}`;

    return this.http.get<OpenMeteoResponse>(url).pipe(
      map(res => {
        const current = res.current_weather;
        if (typeof current?.temperature !== 'number') {
          return this.buildOfflineWeather(config);
        }
        return {
          city: config.city,
          temperature: Math.round(current.temperature),
          unit: config.unit,
          condition: this.describeWeatherCode(current.weathercode ?? 0),
          windSpeed: Math.round(current.windspeed ?? 6),
          source: 'live-api' as const,
          updatedAt: new Date().toLocaleTimeString(),
        };
      }),
      catchError(() => of(this.buildOfflineWeather(config)))
    );
  }

  private buildOfflineWeather(config: ProfileWeatherConfig): WeatherSnapshot {
    const baseCelsius =
      config.city.includes('Austin')
        ? 24
        : config.city.includes('Tokyo')
          ? 18
          : config.city.includes('London')
            ? 13
            : 16;
    const temperature =
      config.unit === 'F'
        ? Math.round((baseCelsius * 9) / 5 + 32)
        : baseCelsius;

    return {
      city: config.city,
      temperature,
      unit: config.unit,
      condition: 'Clear & Mild (Offline Forecast)',
      windSpeed: 8,
      source: 'offline-estimate',
      updatedAt: new Date().toLocaleTimeString(),
    };
  }

  private describeWeatherCode(code: number): string {
    if (code === 0) return 'Clear Sky';
    if (code <= 3) return 'Partly Cloudy';
    if (code <= 48) return 'Coastal Fog';
    if (code <= 67) return 'Light Rain';
    if (code <= 77) return 'Snow Flurries';
    if (code <= 82) return 'Rain Showers';
    return 'Thunderstorms';
  }

  private buildDefaultWidget(
    id: string,
    type: ProfileWidgetType,
    customTitle?: string
  ): ProfileWidgetInstance {
    if (type === 'clock') {
      return {
        id,
        type: 'clock',
        title: customTitle?.trim() || 'Customizable Clock',
        clockConfig: {
          format24Hour: false,
          showSeconds: true,
          timeZone: 'Local',
          label: 'Local Time',
        },
      };
    }
    if (type === 'weather') {
      const preset = this.weatherCities[0];
      return {
        id,
        type: 'weather',
        title: customTitle?.trim() || 'Weather Forecast',
        weatherConfig: {
          city: preset.city,
          latitude: preset.latitude,
          longitude: preset.longitude,
          unit: 'F',
        },
      };
    }
    if (type === 'quote') {
      return {
        id,
        type: 'quote',
        title: customTitle?.trim() || 'Daily Persona Motivation',
        quoteConfig: {
          showAuthor: true,
          showEncouragementTip: true,
        },
      };
    }
    return {
      id,
      type: 'game',
      title: customTitle?.trim() || 'HTML5 Mini-Game',
      gameConfig: {
        mode: 'reaction-canvas',
        embedUrl: '',
      },
    };
  }

  private updateState(
    updater: (prev: ProfileCustomizationState) => ProfileCustomizationState
  ): void {
    const next = updater(this.stateSignal());
    this.stateSignal.set(next);
    this.saveState(next);
  }

  private loadState(): ProfileCustomizationState {
    const fallback: ProfileCustomizationState = {
      themeId: 'midnight-nebula',
      customCssRaw: '',
      sanitizedCss: '',
      widgets: createDefaultWidgets(),
    };
    try {
      if (typeof localStorage === 'undefined') {
        return fallback;
      }
      const raw = localStorage.getItem(CUSTOMIZATION_STORAGE_KEY);
      if (!raw) {
        return fallback;
      }
      const parsed = JSON.parse(raw) as Partial<ProfileCustomizationState>;
      const validTheme = this.themes.some(t => t.id === parsed.themeId)
        ? (parsed.themeId as ProfileThemeId)
        : fallback.themeId;
      const customCssRaw =
        typeof parsed.customCssRaw === 'string' ? parsed.customCssRaw : '';
      const sanitizedCss = customCssRaw
        ? this.sanitizer.sanitizeProfileCss(customCssRaw).sanitizedCss
        : '';
      const widgets =
        Array.isArray(parsed.widgets) && parsed.widgets.length > 0
          ? (parsed.widgets as ProfileWidgetInstance[])
          : fallback.widgets;

      return {
        themeId: validTheme,
        customCssRaw,
        sanitizedCss,
        widgets,
      };
    } catch {
      return fallback;
    }
  }

  private saveState(state: ProfileCustomizationState): void {
    try {
      if (typeof localStorage !== 'undefined') {
        localStorage.setItem(CUSTOMIZATION_STORAGE_KEY, JSON.stringify(state));
      }
    } catch {
      // Ignore storage failures.
    }
  }
}
