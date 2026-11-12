// src/app/data-access/models/user-profile.model.ts
export interface UserProfile {
  id: string;
  displayName: string;
  email: string;
  avatarUrl?: string;
  bio?: string;
}
