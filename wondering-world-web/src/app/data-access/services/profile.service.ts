// src/app/data-access/services/profile.service.ts
import { inject, Injectable, signal } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { LoadingService } from '../../core/loading/loading.service';
import { StudioFilterId, StudioFrameId } from '../models/photo-studio.model';
import { UserProfile } from '../models/user-profile.model';

const AVATAR_STUDIO_STORAGE_KEY = 'wondering_world_profile_avatar_studio';

const DEFAULT_PROFILE: UserProfile = {
  id: 'u_001',
  displayName: 'Alex Morgan',
  email: 'alex@example.com',
  avatarUrl: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e',
  avatarFilterId: 'original',
  avatarFrameId: 'none',
  bio: 'Designer and growth enthusiast',
};

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  private readonly loadingService = inject(LoadingService);
  private readonly profileSignal = signal<UserProfile>(this.loadInitialProfile());
  private readonly profileSubject = new BehaviorSubject<UserProfile>(
    this.profileSignal()
  );

  readonly currentProfile = this.profileSignal.asReadonly();

  getCurrentUser(): Observable<UserProfile> {
    if (this.profileSubject.value.avatarUrl !== DEFAULT_PROFILE.avatarUrl) {
      return this.profileSubject.asObservable();
    }
    return this.loadingService.trackObservable(
      of(this.profileSubject.value).pipe(delay(250)),
      'Fetching user profile from Firebase...'
    );
  }

  observeCurrentUser(): Observable<UserProfile> {
    return this.profileSubject.asObservable();
  }

  updateAvatarCustomization(
    avatarUrl: string,
    avatarFilterId: StudioFilterId,
    avatarFrameId: StudioFrameId
  ): UserProfile {
    const updated: UserProfile = {
      ...this.profileSignal(),
      avatarUrl,
      avatarFilterId,
      avatarFrameId,
    };
    this.profileSignal.set(updated);
    this.profileSubject.next(updated);
    this.persistAvatarState(updated);
    return updated;
  }

  private loadInitialProfile(): UserProfile {
    try {
      if (typeof localStorage === 'undefined') {
        return DEFAULT_PROFILE;
      }
      const raw = localStorage.getItem(AVATAR_STUDIO_STORAGE_KEY);
      if (!raw) {
        return DEFAULT_PROFILE;
      }
      const parsed = JSON.parse(raw) as Partial<UserProfile>;
      return {
        ...DEFAULT_PROFILE,
        avatarUrl: parsed.avatarUrl ?? DEFAULT_PROFILE.avatarUrl,
        avatarFilterId: parsed.avatarFilterId ?? DEFAULT_PROFILE.avatarFilterId,
        avatarFrameId: parsed.avatarFrameId ?? DEFAULT_PROFILE.avatarFrameId,
      };
    } catch {
      return DEFAULT_PROFILE;
    }
  }

  private persistAvatarState(profile: UserProfile): void {
    try {
      if (typeof localStorage !== 'undefined') {
        localStorage.setItem(
          AVATAR_STUDIO_STORAGE_KEY,
          JSON.stringify({
            avatarUrl: profile.avatarUrl,
            avatarFilterId: profile.avatarFilterId,
            avatarFrameId: profile.avatarFrameId,
          })
        );
      }
    } catch {
      // Ignore storage quota or restricted-mode errors.
    }
  }
}
