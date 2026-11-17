import { TestBed } from '@angular/core/testing';
import { ThemeService } from './theme.service';

describe('ThemeService', () => {
  beforeEach(() => {
    localStorage.clear();
    document.documentElement.removeAttribute('data-theme');
  });

  afterEach(() => {
    localStorage.clear();
    document.documentElement.removeAttribute('data-theme');
  });

  it('should default to dark theme and toggle to high-contrast light theme', () => {
    const service = TestBed.inject(ThemeService);

    expect(service.themeMode()).toBe('dark');
    expect(service.isHighContrastLight()).toBeFalse();
    expect(document.documentElement.getAttribute('data-theme')).toBe('dark');

    const next = service.toggleTheme();
    expect(next).toBe('light-hc');
    expect(service.isHighContrastLight()).toBeTrue();
    expect(document.documentElement.getAttribute('data-theme')).toBe('light-hc');
    expect(localStorage.getItem('wondering_world_theme_mode')).toBe('light-hc');

    service.toggleTheme();
    expect(service.themeMode()).toBe('dark');
    expect(document.documentElement.getAttribute('data-theme')).toBe('dark');
  });
});
