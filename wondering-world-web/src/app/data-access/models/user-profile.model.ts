// src/app/data-access/models/user-profile.model.ts
import { StudioFilterId, StudioFrameId } from './photo-studio.model';

export interface UserProfile {
  id: string;
  displayName: string;
  email: string;
  avatarUrl?: string;
  avatarFilterId?: StudioFilterId;
  avatarFrameId?: StudioFrameId;
  bio?: string;
}
