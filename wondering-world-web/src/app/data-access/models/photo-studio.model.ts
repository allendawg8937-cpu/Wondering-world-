// src/app/data-access/models/photo-studio.model.ts

export type StudioFilterId =
  | 'original'
  | 'vibrant'
  | 'golden'
  | 'noir'
  | 'neon'
  | 'soft-glow';

export type StudioFrameId =
  | 'none'
  | 'aurora'
  | 'gold-ring'
  | 'polaroid'
  | 'cyber'
  | 'valor-crest';

export type CameraPermissionState =
  | 'idle'
  | 'requesting'
  | 'granted'
  | 'denied'
  | 'unsupported';

export interface StudioFilterPreset {
  id: StudioFilterId;
  label: string;
  cssFilter: string;
  description: string;
}

export interface StudioFramePreset {
  id: StudioFrameId;
  label: string;
  ringColor: string;
  borderStyle: string;
  glowShadow: string;
  badgeLabel: string;
}

export interface AvatarStudioSavedEvent {
  avatarUrl: string;
  filterId: StudioFilterId;
  frameId: StudioFrameId;
}
