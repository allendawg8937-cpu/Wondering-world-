import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AppShellComponent } from './app-shell.component';

describe('AppShellComponent', () => {
  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [AppShellComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  afterEach(() => {
    localStorage.clear();
    document.documentElement.removeAttribute('data-theme');
  });

  it('should toggle between default dark theme and high-contrast light theme', () => {
    const fixture = TestBed.createComponent(AppShellComponent);
    fixture.detectChanges();

    const btn = fixture.nativeElement.querySelector(
      '[data-testid="global-theme-toggle"]'
    ) as HTMLButtonElement;
    expect(btn).toBeTruthy();
    expect(btn.getAttribute('aria-pressed')).toBe('false');
    expect(document.documentElement.getAttribute('data-theme')).toBe('dark');

    btn.click();
    fixture.detectChanges();

    expect(btn.getAttribute('aria-pressed')).toBe('true');
    expect(document.documentElement.getAttribute('data-theme')).toBe('light-hc');
  });
});
