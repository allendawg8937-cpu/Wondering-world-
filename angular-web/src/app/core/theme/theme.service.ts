import { Injectable, computed, effect, signal } from '@angular/core';

export type AppThemeMode = 'dark' | 'light-hc';

const THEME_STORAGE_KEY = 'wondering_world_theme_mode';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  readonly themeMode = signal<AppThemeMode>(this.loadInitialTheme());
  readonly isHighContrastLight = computed(
    () => this.themeMode() === 'light-hc'
  );

  constructor() {
    effect(() => {
      const mode = this.themeMode();
      this.applyThemeToDocument(mode);
    });
    this.applyThemeToDocument(this.themeMode());
  }

  toggleTheme(): AppThemeMode {
    const next: AppThemeMode =
      this.themeMode() === 'dark' ? 'light-hc' : 'dark';
    this.setTheme(next);
    return next;
  }

  setTheme(mode: AppThemeMode): void {
    this.themeMode.set(mode);
    this.applyThemeToDocument(mode);
    try {
      globalThis.localStorage?.setItem(THEME_STORAGE_KEY, mode);
    } catch {
      // Ignore storage quota / privacy mode errors
    }
  }

  private loadInitialTheme(): AppThemeMode {
    try {
      const stored = globalThis.localStorage?.getItem(THEME_STORAGE_KEY);
      if (stored === 'light-hc' || stored === 'dark') {
        return stored;
      }
    } catch {
      // Ignore storage read errors
    }
    return 'dark';
  }

  private applyThemeToDocument(mode: AppThemeMode): void {
    if (typeof document !== 'undefined' && document.documentElement) {
      document.documentElement.setAttribute('data-theme', mode);
    }
  }
}
