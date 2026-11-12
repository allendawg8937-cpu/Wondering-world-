// src/app/data-access/services/profile.service.ts
import { inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { LoadingService } from '../../core/loading/loading.service';
import { UserProfile } from '../models/user-profile.model';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  private readonly loadingService = inject(LoadingService);

  getCurrentUser(): Observable<UserProfile> {
    return this.loadingService.trackObservable(
      of({
        id: 'u_001',
        displayName: 'Alex Morgan',
        email: 'alex@example.com',
        avatarUrl: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e',
        bio: 'Designer and growth enthusiast',
      }).pipe(delay(250)),
      'Fetching user profile from Firebase...'
    );
  }
}
