-- ============================================================================
-- Wondering World / Stages Of Life — Portable SQLite / Android Room DDL (v8.0)
-- Aligned 1:1 with Android Room `PlanetDatabase` & PostgreSQL `database/schema.sql`
-- ============================================================================

PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS user_profiles (
    profileId TEXT NOT NULL PRIMARY KEY,
    displayName TEXT NOT NULL,
    handle TEXT NOT NULL,
    roleBadge TEXT NOT NULL,
    bio TEXT NOT NULL,
    statusText TEXT NOT NULL,
    avatarColorIndex INTEGER NOT NULL DEFAULT 0,
    personaName TEXT NOT NULL DEFAULT 'VETERAN',
    isDarkMode INTEGER NOT NULL DEFAULT 1,
    useDynamicColor INTEGER NOT NULL DEFAULT 0,
    highContrast INTEGER NOT NULL DEFAULT 0,
    largeText INTEGER NOT NULL DEFAULT 0,
    dyslexiaSpacing INTEGER NOT NULL DEFAULT 0,
    screenReaderHints INTEGER NOT NULL DEFAULT 1,
    offlineModeManual INTEGER NOT NULL DEFAULT 0,
    preferredLanguage TEXT NOT NULL DEFAULT 'English (US)',
    notificationsEnabled INTEGER NOT NULL DEFAULT 1,
    autoSyncWhenOnline INTEGER NOT NULL DEFAULT 1,
    updatedAtMillis INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS financial_tracker_entries (
    entryId TEXT NOT NULL PRIMARY KEY,
    title TEXT NOT NULL,
    category TEXT NOT NULL,
    entryType TEXT NOT NULL CHECK (entryType IN ('INCOME', 'EXPENSE', 'SAVINGS_GOAL', 'LOAN_ESTIMATE')),
    amountCents INTEGER NOT NULL CHECK (amountCents >= 0),
    targetCents INTEGER NOT NULL DEFAULT 0 CHECK (targetCents >= 0),
    personaStage TEXT NOT NULL DEFAULT 'ADULT',
    notes TEXT NOT NULL DEFAULT '',
    createdAtMillis INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS persona_stage_data (
    stageId TEXT NOT NULL PRIMARY KEY,
    userId TEXT NOT NULL DEFAULT 'primary_user',
    stageLabel TEXT NOT NULL,
    focusGoal TEXT NOT NULL DEFAULT '',
    primaryMetricLabel TEXT NOT NULL DEFAULT 'Weekly Milestones',
    primaryMetricValue REAL NOT NULL DEFAULT 0.0,
    primaryMetricTarget REAL NOT NULL DEFAULT 10.0,
    pinnedTopicsCsv TEXT NOT NULL DEFAULT '',
    completedTaskIdsCsv TEXT NOT NULL DEFAULT '',
    customNote TEXT NOT NULL DEFAULT '',
    stageScoreOrXp INTEGER NOT NULL DEFAULT 100,
    updatedAtMillis INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS profile_3d_configs (
    configId TEXT NOT NULL PRIMARY KEY,
    userId TEXT NOT NULL DEFAULT 'primary_user',
    configName TEXT NOT NULL DEFAULT 'Cosmic Explorer',
    baseModelId TEXT NOT NULL DEFAULT 'humanoid-rig',
    primaryColorHex TEXT NOT NULL DEFAULT '#00E5FF',
    secondaryColorHex TEXT NOT NULL DEFAULT '#0F172A',
    auraColorHex TEXT NOT NULL DEFAULT '#00E676',
    headgearId TEXT NOT NULL DEFAULT 'head_crown',
    eyewearId TEXT NOT NULL DEFAULT 'eye_visor',
    auraId TEXT NOT NULL DEFAULT 'aura_drones',
    rigId TEXT NOT NULL DEFAULT 'rig_pauldrons',
    animationPose TEXT NOT NULL DEFAULT 'idle',
    backdropId TEXT NOT NULL DEFAULT 'cyber-grid',
    metallicFinish REAL NOT NULL DEFAULT 0.72,
    autoRotate INTEGER NOT NULL DEFAULT 1,
    updatedAtMillis INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS career_milestones (
    milestoneId TEXT NOT NULL PRIMARY KEY,
    userId TEXT NOT NULL DEFAULT 'primary_user',
    title TEXT NOT NULL,
    stage TEXT NOT NULL DEFAULT 'Foundation',
    description TEXT NOT NULL DEFAULT '',
    targetTerm TEXT NOT NULL DEFAULT 'Step 1 • Foundation',
    status TEXT NOT NULL DEFAULT 'Upcoming' CHECK (status IN ('Upcoming', 'In Progress', 'Completed')),
    xpReward INTEGER NOT NULL DEFAULT 100,
    stepOrder INTEGER NOT NULL DEFAULT 1,
    portalUrl TEXT NOT NULL DEFAULT '' CHECK (portalUrl = '' OR portalUrl LIKE 'https://%'),
    updatedAtMillis INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS messenger_threads (
    threadId TEXT NOT NULL PRIMARY KEY,
    participantName TEXT NOT NULL,
    participantRole TEXT NOT NULL,
    topicBadge TEXT NOT NULL DEFAULT 'Community',
    accentHex TEXT NOT NULL DEFAULT '#00E5FF',
    affinityScore INTEGER NOT NULL DEFAULT 80,
    updatedAtMillis INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS user_affinity_signals (
    signalCategory TEXT NOT NULL,
    signalKey TEXT NOT NULL,
    affinityWeight INTEGER NOT NULL DEFAULT 1,
    lastLearnedAtMillis INTEGER NOT NULL,
    PRIMARY KEY (signalCategory, signalKey)
);

CREATE INDEX IF NOT EXISTS idx_financial_entries_created ON financial_tracker_entries(createdAtMillis DESC);
CREATE INDEX IF NOT EXISTS idx_career_milestones_order ON career_milestones(stepOrder ASC);
CREATE INDEX IF NOT EXISTS idx_affinity_weight ON user_affinity_signals(affinityWeight DESC);

CREATE TABLE IF NOT EXISTS saved_resources (
    resourceId TEXT NOT NULL PRIMARY KEY,
    userId TEXT NOT NULL DEFAULT 'primary_user',
    title TEXT NOT NULL,
    subtitle TEXT NOT NULL DEFAULT '',
    badge TEXT NOT NULL DEFAULT 'VERIFIED RESOURCE',
    url TEXT NOT NULL DEFAULT '' CHECK (url = '' OR url LIKE 'https://%'),
    category TEXT NOT NULL DEFAULT 'Lifeline & Resource Hub',
    stageId TEXT NOT NULL DEFAULT 'all',
    createdAtMillis INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_saved_resources_created ON saved_resources(createdAtMillis DESC);
