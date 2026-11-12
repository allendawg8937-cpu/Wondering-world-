// src/app/data-access/services/profile.service.ts
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { UserProfile } from '../models/user-profile.model';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  getCurrentUser(): Observable<UserProfile> {
    return of({
      id: 'u_001',
      displayName: 'Alex Morgan',
      email: 'alex@example.com',
      avatarUrl: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e',
      bio: 'Designer and growth enthusiast',
    }).pipe(delay(250));
  }
}
