// src/app/features/profile/profile-page.component.ts
import { AsyncPipe, NgIf } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ProfileService } from '../../data-access/services/profile.service';
import { Avatar3dCustomizerComponent } from './avatar-3d-customizer/avatar-3d-customizer.component';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [AsyncPipe, NgIf, RouterLink, Avatar3dCustomizerComponent],
  template: `
    <div class="profile-page-stack">
      <section class="profile" *ngIf="profile$ | async as profile">
        <div class="avatar-wrap">
          <img [src]="profile.avatarUrl" alt="Profile picture" />
        </div>

        <div class="details">
          <h1>{{ profile.displayName }}</h1>
          <p>{{ profile.email }}</p>
          <p>{{ profile.bio }}</p>
          <a class="studio-link" routerLink="/avatar-3d">
            Open Fullscreen 3D Avatar Studio →
          </a>
        </div>
      </section>

      @defer (on viewport) {
        <app-avatar-3d-customizer />
      } @placeholder {
        <div class="studio-placeholder">
          Loading interactive Three.js 3D Avatar Customizer...
        </div>
      }
    </div>
  `,
  styles: [
    `
      .profile-page-stack {
        display: grid;
        gap: 24px;
      }

      .profile {
        display: grid;
        grid-template-columns: 160px 1fr;
        gap: 24px;
        background: #111827;
        border: 1px solid #334155;
        border-radius: 16px;
        padding: 24px;
      }

      .avatar-wrap img {
        width: 160px;
        height: 160px;
        border-radius: 50%;
        object-fit: cover;
      }

      h1 {
        margin: 0 0 8px;
        font-size: 2rem;
      }

      p {
        margin: 0 0 10px;
        color: #cbd5e1;
      }

      .studio-link {
        display: inline-block;
        margin-top: 6px;
        color: #38bdf8;
        font-weight: 600;
        text-decoration: none;
      }

      .studio-placeholder {
        background: #111827;
        border: 1px solid #334155;
        border-radius: 16px;
        padding: 24px;
        color: #94a3b8;
      }
    `,
  ],
})
export class ProfilePageComponent {
  private readonly profileService = inject(ProfileService);

  readonly profile$ = this.profileService.getCurrentUser();
}
