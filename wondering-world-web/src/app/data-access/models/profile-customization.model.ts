// src/app/data-access/models/profile-customization.model.ts

export type ProfileThemeId =
  | 'midnight-nebula'
  | 'sunset-arcade'
  | 'emerald-zen'
  | 'retro-myspace'
  | 'cyber-grid'
  | 'solar-gold';

export interface ProfileThemePreset {
  id: ProfileThemeId;
  name: string;
  tagline: string;
  surfaceBackground: string;
  cardBackground: string;
  borderColor: string;
  accentColor: string;
  textColor: string;
  fontFamily: string;
  swatchColors: readonly [string, string, string];
}

export type ProfileWidgetType = 'clock' | 'weather' | 'game' | 'quote';

export type ProfileGameMode = 'reaction-canvas' | 'memory-grid' | 'custom-embed';

export interface ProfileClockConfig {
  format24Hour: boolean;
  showSeconds: boolean;
  timeZone: string;
  label: string;
}

export interface ProfileWeatherConfig {
  city: string;
  latitude: number;
  longitude: number;
  unit: 'F' | 'C';
}

export interface ProfileGameConfig {
  mode: ProfileGameMode;
  embedUrl: string;
}

export interface ProfileQuoteConfig {
  showAuthor: boolean;
  showEncouragementTip: boolean;
}

export interface ProfileWidgetInstance {
  id: string;
  type: ProfileWidgetType;
  title: string;
  clockConfig?: ProfileClockConfig;
  weatherConfig?: ProfileWeatherConfig;
  gameConfig?: ProfileGameConfig;
  quoteConfig?: ProfileQuoteConfig;
}

export interface WeatherSnapshot {
  city: string;
  temperature: number;
  unit: 'F' | 'C';
  condition: string;
  windSpeed: number;
  source: 'live-api' | 'offline-estimate';
  updatedAt: string;
}

export interface CssSanitizationReport {
  sanitizedCss: string;
  blockedPatterns: readonly string[];
  ruleCount: number;
  wasModified: boolean;
}

export interface ProfileCustomizationState {
  themeId: ProfileThemeId;
  customCssRaw: string;
  sanitizedCss: string;
  widgets: readonly ProfileWidgetInstance[];
}
