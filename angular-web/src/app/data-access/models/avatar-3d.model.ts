export type LifeStageId =
  | 'kid'
  | 'teen'
  | 'student'
  | 'adult'
  | 'business'
  | 'veteran';

export type AvatarGearId =
  | 'visor'
  | 'headphones'
  | 'glasses'
  | 'halo'
  | 'crown'
  | 'jetpack';

export type AvatarPoseId = 'idle' | 'levitate' | 'victory';

export type AvatarBackdropId = 'cyber-grid' | 'nebula-orbit' | 'solar-halo';

export type AvatarBaseModelStyle =
  | 'humanoid-rig'
  | 'mech-guardian'
  | 'voxel-bot'
  | 'crystal-sprite';

export interface AvatarBaseModelOption {
  id: AvatarBaseModelStyle;
  label: string;
  subtitle: string;
  defaultBackdrop: AvatarBackdropId;
  accentHex: string;
}

export interface AvatarVisualThemePreset {
  id: string;
  label: string;
  accentHex: string;
  suitHex: string;
  rimLightHex: string;
  backdrop: AvatarBackdropId;
  metalness: number;
}

export interface LifeStageAvatarPreset {
  stageId: LifeStageId;
  stageLabel: string;
  characterName: string;
  tagline: string;
  accentHex: string;
  suitHex: string;
  skinToneHex: string;
  defaultGear: AvatarGearId;
  headScale: number;
  torsoScale: number;
  heightScale: number;
}

export type AvatarLightingPresetId = 'studio' | 'bright-fill' | 'dramatic';

export interface AvatarLightingPreset {
  id: AvatarLightingPresetId;
  label: string;
  ambientIntensity: number;
  directionalIntensity: number;
}

export interface Avatar3dConfig {
  stageId: LifeStageId;
  baseModelStyle: AvatarBaseModelStyle;
  themeStyleId: string;
  accentHex: string;
  suitHex: string;
  skinToneHex: string;
  gearId: AvatarGearId;
  pose: AvatarPoseId;
  backdrop: AvatarBackdropId;
  metalness: number;
  wireframe: boolean;
  autoRotate: boolean;
  ambientIntensity: number;
  directionalIntensity: number;
}

export const AVATAR_LIGHTING_PRESETS: AvatarLightingPreset[] = [
  {
    id: 'studio',
    label: 'Studio Balanced',
    ambientIntensity: 0.9,
    directionalIntensity: 1.65,
  },
  {
    id: 'bright-fill',
    label: 'Bright Showcase',
    ambientIntensity: 1.35,
    directionalIntensity: 2.1,
  },
  {
    id: 'dramatic',
    label: 'Dramatic Rim',
    ambientIntensity: 0.45,
    directionalIntensity: 2.4,
  },
];

export const AVATAR_BASE_MODEL_OPTIONS: AvatarBaseModelOption[] = [
  {
    id: 'humanoid-rig',
    label: 'Humanoid Prime',
    subtitle: 'Smooth stylized Pixar-inspired rig',
    defaultBackdrop: 'cyber-grid',
    accentHex: '#38bdf8',
  },
  {
    id: 'mech-guardian',
    label: 'Mech Guardian',
    subtitle: 'Armored shoulder pauldrons & visor core',
    defaultBackdrop: 'solar-halo',
    accentHex: '#f59e0b',
  },
  {
    id: 'voxel-bot',
    label: 'Voxel Byte',
    subtitle: 'Geometric cube-crafted pixel rig',
    defaultBackdrop: 'cyber-grid',
    accentHex: '#10b981',
  },
  {
    id: 'crystal-sprite',
    label: 'Crystal Sprite',
    subtitle: 'Faceted octahedral astral prism rig',
    defaultBackdrop: 'nebula-orbit',
    accentHex: '#a855f7',
  },
];

export const AVATAR_VISUAL_THEME_PRESETS: AvatarVisualThemePreset[] = [
  {
    id: 'cyber-neon',
    label: 'Cyber Neon',
    accentHex: '#00e5ff',
    suitHex: '#0f172a',
    rimLightHex: '#38bdf8',
    backdrop: 'cyber-grid',
    metalness: 0.4,
  },
  {
    id: 'synthwave-sunset',
    label: 'Synthwave Sunset',
    accentHex: '#ec4899',
    suitHex: '#3b0764',
    rimLightHex: '#f472b6',
    backdrop: 'nebula-orbit',
    metalness: 0.55,
  },
  {
    id: 'emerald-matrix',
    label: 'Emerald Matrix',
    accentHex: '#10b981',
    suitHex: '#064e3b',
    rimLightHex: '#34d399',
    backdrop: 'cyber-grid',
    metalness: 0.45,
  },
  {
    id: 'solar-gold',
    label: 'Solar Gold',
    accentHex: '#f59e0b',
    suitHex: '#1f2937',
    rimLightHex: '#fbbf24',
    backdrop: 'solar-halo',
    metalness: 0.75,
  },
];

export const LIFE_STAGE_AVATAR_PRESETS: LifeStageAvatarPreset[] = [
  {
    stageId: 'kid',
    stageLabel: 'Kid',
    characterName: 'Starlight Explorer',
    tagline: 'Playful chibi proportions with emerald cosmic visor',
    accentHex: '#10b981',
    suitHex: '#065f46',
    skinToneHex: '#f4c7a1',
    defaultGear: 'visor',
    headScale: 1.2,
    torsoScale: 0.86,
    heightScale: 0.84,
  },
  {
    stageId: 'teen',
    stageLabel: 'Teen',
    characterName: 'Neon Creator',
    tagline: 'Dynamic street rig with magenta sonic headphones',
    accentHex: '#ec4899',
    suitHex: '#4c1d95',
    skinToneHex: '#d99b78',
    defaultGear: 'headphones',
    headScale: 1.05,
    torsoScale: 0.94,
    heightScale: 0.94,
  },
  {
    stageId: 'student',
    stageLabel: 'Student',
    characterName: 'Quantum Scholar',
    tagline: 'Focused scholar rig with indigo AR smart lenses',
    accentHex: '#6366f1',
    suitHex: '#1e3a8a',
    skinToneHex: '#e8be9e',
    defaultGear: 'glasses',
    headScale: 1.0,
    torsoScale: 0.98,
    heightScale: 0.98,
  },
  {
    stageId: 'adult',
    stageLabel: 'Adult',
    characterName: 'Horizon Voyager',
    tagline: 'Balanced modern prime rig with cyan aurora halo',
    accentHex: '#00e5ff',
    suitHex: '#0f172a',
    skinToneHex: '#c68b67',
    defaultGear: 'halo',
    headScale: 0.96,
    torsoScale: 1.05,
    heightScale: 1.02,
  },
  {
    stageId: 'business',
    stageLabel: 'Business',
    characterName: 'Apex Founder',
    tagline: 'Tailored executive rig with golden telemetry crown',
    accentHex: '#f59e0b',
    suitHex: '#1f2937',
    skinToneHex: '#8d5534',
    defaultGear: 'crown',
    headScale: 0.95,
    torsoScale: 1.1,
    heightScale: 1.04,
  },
  {
    stageId: 'veteran',
    stageLabel: 'Veteran',
    characterName: 'Solar Mentor',
    tagline: 'Heroic commander rig with twilight thrusters',
    accentHex: '#a855f7',
    suitHex: '#312e81',
    skinToneHex: '#f1c9a8',
    defaultGear: 'jetpack',
    headScale: 0.95,
    torsoScale: 1.14,
    heightScale: 1.06,
  },
];

export const SKIN_TONE_SWATCHES: Array<{ label: string; hex: string }> = [
  { label: 'Porcelain', hex: '#f4c7a1' },
  { label: 'Warm Sand', hex: '#e8be9e' },
  { label: 'Golden Honey', hex: '#d99b78' },
  { label: 'Chestnut', hex: '#c68b67' },
  { label: 'Deep Espresso', hex: '#8d5534' },
];

export const AVATAR_GEAR_OPTIONS: Array<{ id: AvatarGearId; label: string }> = [
  { id: 'visor', label: 'Holo Visor' },
  { id: 'headphones', label: 'Sonic Phones' },
  { id: 'glasses', label: 'AR Glasses' },
  { id: 'halo', label: 'Aurora Halo' },
  { id: 'crown', label: 'Founder Crest' },
  { id: 'jetpack', label: 'Jet Thrusters' },
];
