// src/app/features/auth/login/login-page.component.ts
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { ThemeService } from '../../../core/theme/theme.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <section class="login">
      <div class="card">
        <div class="login-top">
          <h1>Welcome back</h1>
          <button
            type="button"
            class="theme-btn"
            data-testid="login-theme-toggle"
            [attr.aria-pressed]="themeService.isHighContrastLight()"
            (click)="themeService.toggleTheme()"
          >
            {{ themeService.isHighContrastLight() ? '🌙 Dark' : '☀️ Light HC' }}
          </button>
        </div>

        <form [formGroup]="form" (ngSubmit)="submit()">
          <label>
            Email
            <input type="email" formControlName="email" />
          </label>

          <label>
            Password
            <input type="password" formControlName="password" />
          </label>

          <button type="submit" [disabled]="form.invalid">
            Sign in
          </button>
        </form>
      </div>
    </section>
  `,
  styles: [
    `
      .login {
        min-height: 100vh;
        display: grid;
        place-items: center;
        padding: 16px;
        background: var(--bg-app);
        color: var(--text-primary);
      }

      .card {
        width: min(100%, 420px);
        background: var(--bg-surface);
        border: 2px solid var(--border-subtle);
        border-radius: 16px;
        padding: 24px;
        box-shadow: 0 20px 40px rgba(0, 0, 0, 0.24);
      }

      .login-top {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 12px;
        margin-bottom: 20px;
      }

      h1 {
        margin: 0;
        font-size: 1.85rem;
      }

      .theme-btn {
        background: var(--bg-card);
        color: var(--text-primary);
        border: 2px solid var(--border-strong);
        border-radius: 8px;
        padding: 7px 10px;
        font-size: 0.8rem;
        font-weight: 700;
        cursor: pointer;
      }

      form {
        display: grid;
        gap: 16px;
      }

      label {
        display: grid;
        gap: 8px;
        font-size: 0.95rem;
        font-weight: 600;
      }

      input {
        background: var(--bg-input);
        border: 2px solid var(--border-strong);
        color: var(--text-primary);
        border-radius: 8px;
        padding: 12px 14px;
        font-size: 1rem;
      }

      button[type='submit'] {
        background: var(--accent-primary);
        color: #ffffff;
        border: none;
        border-radius: 10px;
        padding: 12px 16px;
        font-weight: 700;
        cursor: pointer;
      }
    `,
  ],
})
export class LoginPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly themeService = inject(ThemeService);

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  async submit(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const loggedIn = await this.auth.login(
      this.form.value.email ?? '',
      this.form.value.password ?? ''
    );

    if (loggedIn) {
      this.router.navigate(['/dashboard']);
    }
  }
}
