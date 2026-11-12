// src/app/features/auth/login/login-page.component.ts
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <section class="login">
      <div class="card">
        <h1>Welcome back</h1>

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
        background: linear-gradient(135deg, #0f172a, #1e293b);
        color: #e2e8f0;
      }

      .card {
        width: min(100%, 420px);
        background: rgba(15, 23, 42, 0.9);
        border: 1px solid #334155;
        border-radius: 16px;
        padding: 24px;
        box-shadow: 0 20px 40px rgba(0, 0, 0, 0.24);
      }

      h1 {
        margin: 0 0 20px;
        font-size: 2rem;
      }

      form {
        display: grid;
        gap: 16px;
      }

      label {
        display: grid;
        gap: 8px;
        font-size: 0.95rem;
      }

      input {
        background: #0b1120;
        border: 1px solid #475569;
        color: white;
        border-radius: 8px;
        padding: 12px 14px;
        font-size: 1rem;
      }

      button {
        background: #2563eb;
        color: white;
        border: none;
        border-radius: 10px;
        padding: 12px 16px;
        font-weight: 600;
        cursor: pointer;
      }
    `,
  ],
})
export class LoginPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

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
