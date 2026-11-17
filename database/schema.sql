-- ============================================================================
-- Wondering World / Stages Of Life — Unified Platform PostgreSQL Schema (v8.0)
-- Covers: Users & Accessibility, 6 Life Stages, Real-Time 3D Avatar PBR Studio,
--         Photo Studio Snapshots, Finance & Goals, Career Milestones,
--         Community Feed / Comments / Likes, Social Graph, Video Reels,
--         Moderation & Safety, and Telemetry Analytics.
-- Target: PostgreSQL 15+ / Google Cloud SQL
-- ============================================================================

BEGIN;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ----------------------------------------------------------------------------
-- 0. Shared Timestamp Trigger
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ----------------------------------------------------------------------------
-- 1. Life Stage Reference Catalog
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS life_stages (
    stage_id                VARCHAR(32) PRIMARY KEY
        CHECK (stage_id IN ('kid', 'teen', 'student', 'adult', 'business', 'veteran')),
    stage_label             VARCHAR(64) NOT NULL,
    default_age             INTEGER NOT NULL CHECK (default_age BETWEEN 5 AND 120),
    emoji                   VARCHAR(16) NOT NULL,
    accent_hex              VARCHAR(16) NOT NULL,
    guardrail_badge         VARCHAR(96) NOT NULL,
    headline                VARCHAR(200) NOT NULL,
    summary                 TEXT NOT NULL,
    default_metric_label    VARCHAR(96) NOT NULL,
    default_metric_target   DOUBLE PRECISION NOT NULL CHECK (default_metric_target > 0),
    default_gear_id         VARCHAR(48) NOT NULL DEFAULT 'halo',
    sort_order              INTEGER NOT NULL UNIQUE
);

-- ----------------------------------------------------------------------------
-- 2. User Profiles, Preferences & Economy Wallet
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_profiles (
    user_id                  VARCHAR(128) PRIMARY KEY,
    display_name             VARCHAR(120) NOT NULL,
    handle                   VARCHAR(80) UNIQUE,
    email                    VARCHAR(254) UNIQUE,
    role                     VARCHAR(32) NOT NULL DEFAULT 'adult'
        REFERENCES life_stages(stage_id),
    age                      INTEGER NOT NULL DEFAULT 26 CHECK (age BETWEEN 5 AND 120),
    location                 VARCHAR(120) NOT NULL DEFAULT '',
    bio                      TEXT NOT NULL DEFAULT '',
    profile_picture_url      TEXT NOT NULL DEFAULT '',
    phone_number             VARCHAR(32) NOT NULL DEFAULT '',
    theme_color_hex          VARCHAR(16) NOT NULL DEFAULT '#00E5FF',
    theme_visual_style       VARCHAR(48) NOT NULL DEFAULT 'HYPERCUBE_6D',
    theme_mode               VARCHAR(24) NOT NULL DEFAULT 'dark'
        CHECK (theme_mode IN ('dark', 'light', 'light-hc')),
    is_dark_mode             BOOLEAN NOT NULL DEFAULT TRUE,
    high_contrast            BOOLEAN NOT NULL DEFAULT FALSE,
    large_text               BOOLEAN NOT NULL DEFAULT FALSE,
    dyslexia_spacing         BOOLEAN NOT NULL DEFAULT FALSE,
    screen_reader_hints      BOOLEAN NOT NULL DEFAULT TRUE,
    use_dynamic_color        BOOLEAN NOT NULL DEFAULT FALSE,
    offline_mode_manual      BOOLEAN NOT NULL DEFAULT FALSE,
    preferred_language       VARCHAR(40) NOT NULL DEFAULT 'English (US)',
    notifications_enabled    BOOLEAN NOT NULL DEFAULT TRUE,
    auto_sync_when_online    BOOLEAN NOT NULL DEFAULT TRUE,
    music_track_title        VARCHAR(180) NOT NULL DEFAULT 'Comfortably Numb - Pink Floyd',
    music_platform           VARCHAR(48) NOT NULL DEFAULT 'Spotify',
    spotify_connected        BOOLEAN NOT NULL DEFAULT FALSE,
    spotify_user_id          VARCHAR(128) NOT NULL DEFAULT '',
    apple_music_connected    BOOLEAN NOT NULL DEFAULT FALSE,
    apple_music_storefront   VARCHAR(32) NOT NULL DEFAULT '',
    savings_balance_cents    BIGINT NOT NULL DEFAULT 0 CHECK (savings_balance_cents >= 0),
    savings_goal_cents       BIGINT NOT NULL DEFAULT 500000 CHECK (savings_goal_cents >= 0),
    virtual_currency_balance BIGINT NOT NULL DEFAULT 250 CHECK (virtual_currency_balance >= 0),
    arcade_high_score        BIGINT NOT NULL DEFAULT 0 CHECK (arcade_high_score >= 0),
    pledge_accepted          BOOLEAN NOT NULL DEFAULT TRUE,
    interests                JSONB NOT NULL DEFAULT '[]'::jsonb,
    earned_badges            JSONB NOT NULL DEFAULT '["welcome_pioneer"]'::jsonb,
    owned_virtual_items      JSONB NOT NULL DEFAULT '[]'::jsonb,
    created_at               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TRIGGER trg_user_profiles_updated_at
    BEFORE UPDATE ON user_profiles
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE INDEX IF NOT EXISTS idx_user_profiles_role ON user_profiles(role);
CREATE INDEX IF NOT EXISTS idx_user_profiles_interests_gin ON user_profiles USING GIN (interests);

-- ----------------------------------------------------------------------------
-- 3. Per-User Life-Stage Progress & Goals (`persona_stage_data`)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS persona_stage_data (
    user_id               VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    stage_id              VARCHAR(32) NOT NULL REFERENCES life_stages(stage_id),
    stage_label           VARCHAR(64) NOT NULL,
    focus_goal            TEXT NOT NULL DEFAULT '',
    primary_metric_label  VARCHAR(96) NOT NULL DEFAULT 'Weekly Milestones',
    primary_metric_value  DOUBLE PRECISION NOT NULL DEFAULT 0 CHECK (primary_metric_value >= 0),
    primary_metric_target DOUBLE PRECISION NOT NULL DEFAULT 10 CHECK (primary_metric_target > 0),
    pinned_topics         JSONB NOT NULL DEFAULT '[]'::jsonb,
    completed_task_ids    JSONB NOT NULL DEFAULT '[]'::jsonb,
    custom_note           TEXT NOT NULL DEFAULT '',
    stage_score_or_xp     BIGINT NOT NULL DEFAULT 100 CHECK (stage_score_or_xp >= 0),
    created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, stage_id)
);

CREATE TRIGGER trg_persona_stage_data_updated_at
    BEFORE UPDATE ON persona_stage_data
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ----------------------------------------------------------------------------
-- 4. 3D Avatar PBR & Three.js Studio Configurations (`profile_3d_configs`)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS profile_3d_configs (
    config_id              VARCHAR(128) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id                VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    config_name            VARCHAR(120) NOT NULL DEFAULT 'Cosmic Explorer',
    base_model_id          VARCHAR(48) NOT NULL DEFAULT 'humanoid-rig'
        CHECK (base_model_id IN ('humanoid-rig', 'mech-guardian', 'voxel-bot', 'crystal-sprite', 'cyber_astronaut')),
    base_model_name        VARCHAR(80) NOT NULL DEFAULT 'Humanoid Prime',
    theme_style_id         VARCHAR(48) NOT NULL DEFAULT 'cyber-neon',
    primary_color_hex      VARCHAR(16) NOT NULL DEFAULT '#00E5FF',
    secondary_color_hex    VARCHAR(16) NOT NULL DEFAULT '#0F172A',
    aura_color_hex         VARCHAR(16) NOT NULL DEFAULT '#00E676',
    skin_tone_hex          VARCHAR(16) NOT NULL DEFAULT '#C68B67',
    headgear_id            VARCHAR(48) NOT NULL DEFAULT 'head_crown',
    eyewear_id             VARCHAR(48) NOT NULL DEFAULT 'eye_visor',
    aura_id                VARCHAR(48) NOT NULL DEFAULT 'aura_drones',
    rig_id                 VARCHAR(48) NOT NULL DEFAULT 'rig_pauldrons',
    animation_pose         VARCHAR(24) NOT NULL DEFAULT 'idle'
        CHECK (animation_pose IN ('idle', 'wave', 'dance', 'levitate', 'victory')),
    backdrop_id            VARCHAR(32) NOT NULL DEFAULT 'cyber-grid'
        CHECK (backdrop_id IN ('cyber-grid', 'nebula-orbit', 'solar-halo')),
    metallic_finish        NUMERIC(4,3) NOT NULL DEFAULT 0.400 CHECK (metallic_finish BETWEEN 0 AND 1),
    roughness              NUMERIC(4,3) NOT NULL DEFAULT 0.350 CHECK (roughness BETWEEN 0.05 AND 1),
    wireframe_overlay      BOOLEAN NOT NULL DEFAULT FALSE,
    ambient_intensity      NUMERIC(4,2) NOT NULL DEFAULT 0.90 CHECK (ambient_intensity BETWEEN 0.1 AND 3.0),
    directional_intensity  NUMERIC(4,2) NOT NULL DEFAULT 1.65 CHECK (directional_intensity BETWEEN 0.1 AND 4.0),
    scale_factor           NUMERIC(4,2) NOT NULL DEFAULT 1.00 CHECK (scale_factor BETWEEN 0.5 AND 2.0),
    auto_rotate            BOOLEAN NOT NULL DEFAULT TRUE,
    turntable_yaw_deg      DOUBLE PRECISION NOT NULL DEFAULT 14.0,
    is_active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TRIGGER trg_profile_3d_configs_updated_at
    BEFORE UPDATE ON profile_3d_configs
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE INDEX IF NOT EXISTS idx_profile_3d_configs_user ON profile_3d_configs(user_id, is_active);

-- ----------------------------------------------------------------------------
-- 5. Photo & 3D Studio PNG Snapshots
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS photo_studio_snapshots (
    snapshot_id      VARCHAR(128) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id          VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    config_id        VARCHAR(128) REFERENCES profile_3d_configs(config_id) ON DELETE SET NULL,
    filename         VARCHAR(180) NOT NULL,
    filter_id        VARCHAR(32) NOT NULL DEFAULT 'original',
    frame_id         VARCHAR(32) NOT NULL DEFAULT 'none',
    brightness_pct   INTEGER NOT NULL DEFAULT 100 CHECK (brightness_pct BETWEEN 50 AND 200),
    contrast_pct     INTEGER NOT NULL DEFAULT 100 CHECK (contrast_pct BETWEEN 50 AND 200),
    snapshot_url     TEXT NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ----------------------------------------------------------------------------
-- 6. Financial Literacy, Savings Goals & Budget Tracker
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS financial_tracker_entries (
    entry_id        VARCHAR(128) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id         VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    persona_stage   VARCHAR(32) NOT NULL DEFAULT 'adult' REFERENCES life_stages(stage_id),
    title           VARCHAR(160) NOT NULL,
    category        VARCHAR(64) NOT NULL DEFAULT 'Save',
    entry_type      VARCHAR(32) NOT NULL DEFAULT 'SAVINGS_GOAL'
        CHECK (entry_type IN ('INCOME', 'EXPENSE', 'SAVINGS_GOAL', 'LOAN_ESTIMATE')),
    amount_cents    BIGINT NOT NULL CHECK (amount_cents >= 0),
    target_cents    BIGINT NOT NULL DEFAULT 0 CHECK (target_cents >= 0),
    notes           TEXT NOT NULL DEFAULT '',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TRIGGER trg_financial_tracker_updated_at
    BEFORE UPDATE ON financial_tracker_entries
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE INDEX IF NOT EXISTS idx_financial_entries_user_stage
    ON financial_tracker_entries(user_id, persona_stage, created_at DESC);

-- ----------------------------------------------------------------------------
-- 7. Student Career, FAFSA & Apprenticeship Milestones
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS career_milestones (
    milestone_id    VARCHAR(128) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id         VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    title           VARCHAR(180) NOT NULL,
    stage           VARCHAR(64) NOT NULL DEFAULT 'Foundation',
    description     TEXT NOT NULL DEFAULT '',
    target_term     VARCHAR(96) NOT NULL DEFAULT 'Step 1 • Foundation',
    status          VARCHAR(32) NOT NULL DEFAULT 'Upcoming'
        CHECK (status IN ('Upcoming', 'In Progress', 'Completed')),
    xp_reward       BIGINT NOT NULL DEFAULT 100 CHECK (xp_reward >= 0),
    step_order      INTEGER NOT NULL DEFAULT 1,
    portal_url      TEXT NOT NULL DEFAULT '',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TRIGGER trg_career_milestones_updated_at
    BEFORE UPDATE ON career_milestones
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE INDEX IF NOT EXISTS idx_career_milestones_user_order
    ON career_milestones(user_id, step_order);

-- ----------------------------------------------------------------------------
-- 8. Community Feed: Wall Posts, Likes, Comments & Video Reels
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS wall_posts (
    post_id       VARCHAR(128) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id       VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    author_name   VARCHAR(120) NOT NULL,
    author_role   VARCHAR(32) NOT NULL REFERENCES life_stages(stage_id),
    category      VARCHAR(64) NOT NULL DEFAULT 'Update',
    title         VARCHAR(200) NOT NULL,
    content       TEXT NOT NULL,
    likes_count   BIGINT NOT NULL DEFAULT 0 CHECK (likes_count >= 0),
    accent_hex    VARCHAR(16) NOT NULL DEFAULT '#6366F1',
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TRIGGER trg_wall_posts_updated_at
    BEFORE UPDATE ON wall_posts
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE INDEX IF NOT EXISTS idx_wall_posts_recent ON wall_posts(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_wall_posts_role ON wall_posts(author_role);

CREATE TABLE IF NOT EXISTS post_likes (
    liker_id      VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    post_id       VARCHAR(128) NOT NULL REFERENCES wall_posts(post_id) ON DELETE CASCADE,
    post_owner_id VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (liker_id, post_id)
);

CREATE TABLE IF NOT EXISTS post_comments (
    comment_id    VARCHAR(128) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    post_id       VARCHAR(128) NOT NULL REFERENCES wall_posts(post_id) ON DELETE CASCADE,
    post_owner_id VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    author_id     VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    author_name   VARCHAR(120) NOT NULL,
    author_role   VARCHAR(32) NOT NULL REFERENCES life_stages(stage_id),
    body_text     TEXT NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_post_comments_post ON post_comments(post_id, created_at ASC);

CREATE TABLE IF NOT EXISTS video_reel_comments (
    comment_id    VARCHAR(128) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    reel_id       VARCHAR(128) NOT NULL,
    video_id      VARCHAR(128) NOT NULL,
    user_id       VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    author_name   VARCHAR(120) NOT NULL,
    author_role   VARCHAR(32) NOT NULL REFERENCES life_stages(stage_id),
    body_text     TEXT NOT NULL,
    likes_count   BIGINT NOT NULL DEFAULT 0 CHECK (likes_count >= 0),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_video_reel_comments_reel ON video_reel_comments(reel_id, created_at DESC);

-- ----------------------------------------------------------------------------
-- 9. Social Graph: Friends, Friend Requests & Follows
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS friends (
    user_id         VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    friend_user_id  VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    display_name    VARCHAR(120) NOT NULL,
    avatar_emoji    VARCHAR(16) NOT NULL DEFAULT '👤',
    role            VARCHAR(32) NOT NULL REFERENCES life_stages(stage_id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, friend_user_id),
    CHECK (user_id <> friend_user_id)
);

CREATE TABLE IF NOT EXISTS friend_requests (
    request_id        VARCHAR(128) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    from_user_id      VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    from_name         VARCHAR(120) NOT NULL,
    from_avatar_emoji VARCHAR(16) NOT NULL DEFAULT '👤',
    to_user_id        VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    status            VARCHAR(24) NOT NULL DEFAULT 'pending'
        CHECK (status IN ('pending', 'accepted', 'declined')),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CHECK (from_user_id <> to_user_id)
);

CREATE INDEX IF NOT EXISTS idx_friend_requests_recipient ON friend_requests(to_user_id, status);

CREATE TABLE IF NOT EXISTS social_follows (
    follower_id            VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    following_id           VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    following_display_name VARCHAR(120) NOT NULL,
    following_role         VARCHAR(32) NOT NULL REFERENCES life_stages(stage_id),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (follower_id, following_id),
    CHECK (follower_id <> following_id)
);

-- ----------------------------------------------------------------------------
-- 10. Trust, Safety, Blocking & Telemetry Analytics
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS blocked_users (
    user_id              VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    blocked_user_id      VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    blocked_display_name VARCHAR(120) NOT NULL,
    reason               VARCHAR(160) NOT NULL DEFAULT 'Boundary Preference',
    created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, blocked_user_id),
    CHECK (user_id <> blocked_user_id)
);

CREATE TABLE IF NOT EXISTS moderation_flags (
    flag_id          VARCHAR(128) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    reporter_user_id VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    target_id        VARCHAR(128) NOT NULL,
    target_type      VARCHAR(24) NOT NULL CHECK (target_type IN ('post', 'profile', 'comment', 'reel_comment')),
    reason           VARCHAR(120) NOT NULL,
    detail           TEXT NOT NULL DEFAULT '',
    status           VARCHAR(24) NOT NULL DEFAULT 'pending'
        CHECK (status IN ('pending', 'reviewed', 'resolved', 'dismissed')),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_moderation_flags_status ON moderation_flags(status, created_at DESC);

CREATE TABLE IF NOT EXISTS user_interactions (
    interaction_id VARCHAR(128) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id        VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    action_type    VARCHAR(64) NOT NULL,
    detail         TEXT NOT NULL DEFAULT '',
    category       VARCHAR(64) NOT NULL DEFAULT 'general',
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_user_interactions_user_time ON user_interactions(user_id, created_at DESC);

-- ----------------------------------------------------------------------------
-- 10B. Adaptive Messenger & Continuous User-Learning Engine
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS messenger_threads (
    thread_id        VARCHAR(128) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    owner_user_id    VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    participant_name VARCHAR(120) NOT NULL,
    participant_role VARCHAR(32) NOT NULL REFERENCES life_stages(stage_id),
    topic_badge      VARCHAR(80) NOT NULL DEFAULT 'Community Chat',
    accent_hex       VARCHAR(16) NOT NULL DEFAULT '#00E5FF',
    is_online        BOOLEAN NOT NULL DEFAULT TRUE,
    affinity_score   INTEGER NOT NULL DEFAULT 80 CHECK (affinity_score BETWEEN 0 AND 100),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TRIGGER trg_messenger_threads_updated_at
    BEFORE UPDATE ON messenger_threads
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE INDEX IF NOT EXISTS idx_messenger_threads_owner ON messenger_threads(owner_user_id, updated_at DESC);

CREATE TABLE IF NOT EXISTS messenger_messages (
    message_id        VARCHAR(128) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    thread_id         VARCHAR(128) NOT NULL REFERENCES messenger_threads(thread_id) ON DELETE CASCADE,
    sender_user_id    VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    sender_name       VARCHAR(120) NOT NULL,
    sender_role       VARCHAR(32) NOT NULL REFERENCES life_stages(stage_id),
    body_text         TEXT NOT NULL,
    delivery_status   VARCHAR(24) NOT NULL DEFAULT 'Delivered'
        CHECK (delivery_status IN ('Queued', 'Delivered', 'Received', 'Read')),
    reactions_count   INTEGER NOT NULL DEFAULT 0 CHECK (reactions_count >= 0),
    learned_topic_tag VARCHAR(64) NOT NULL DEFAULT 'Community',
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_messenger_messages_thread ON messenger_messages(thread_id, created_at ASC);

CREATE TABLE IF NOT EXISTS user_affinity_signals (
    user_id         VARCHAR(128) NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    signal_category VARCHAR(48) NOT NULL
        CHECK (signal_category IN ('interest', 'lifestyle', 'schedule', 'research', 'messenger_topic')),
    signal_key      VARCHAR(96) NOT NULL,
    affinity_weight INTEGER NOT NULL DEFAULT 1 CHECK (affinity_weight >= 1),
    last_learned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, signal_category, signal_key)
);

CREATE INDEX IF NOT EXISTS idx_user_affinity_rank ON user_affinity_signals(user_id, affinity_weight DESC);

-- ----------------------------------------------------------------------------
-- 11. Platform Analytics & Reporting SQL Views
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW vw_platform_user_overview AS
SELECT
    u.user_id,
    u.display_name,
    u.role AS active_stage_id,
    ls.stage_label,
    u.virtual_currency_balance AS equal_coins,
    u.savings_balance_cents,
    u.savings_goal_cents,
    ROUND(
        CASE WHEN u.savings_goal_cents > 0
             THEN LEAST(100.0, (u.savings_balance_cents::numeric / u.savings_goal_cents) * 100.0)
             ELSE 0.0
        END, 2
    ) AS savings_goal_pct,
    COALESCE(SUM(psd.stage_score_or_xp), 0) AS total_stage_xp,
    COUNT(DISTINCT cm.milestone_id) FILTER (WHERE cm.status = 'Completed') AS completed_milestones,
    COUNT(DISTINCT f.friend_user_id) AS friend_count,
    COUNT(DISTINCT p.post_id) AS post_count
FROM user_profiles u
JOIN life_stages ls ON ls.stage_id = u.role
LEFT JOIN persona_stage_data psd ON psd.user_id = u.user_id
LEFT JOIN career_milestones cm ON cm.user_id = u.user_id
LEFT JOIN friends f ON f.user_id = u.user_id
LEFT JOIN wall_posts p ON p.user_id = u.user_id
GROUP BY u.user_id, ls.stage_label;

CREATE OR REPLACE VIEW vw_financial_stage_rollups AS
SELECT
    persona_stage,
    entry_type,
    COUNT(*) AS entry_count,
    SUM(amount_cents) AS total_amount_cents,
    SUM(target_cents) AS total_target_cents
FROM financial_tracker_entries
GROUP BY persona_stage, entry_type;

CREATE OR REPLACE VIEW vw_avatar_3d_studio_analytics AS
SELECT
    base_model_id,
    animation_pose,
    backdrop_id,
    COUNT(*) AS config_count,
    AVG(metallic_finish)::numeric(4,3) AS avg_metalness,
    AVG(roughness)::numeric(4,3) AS avg_roughness
FROM profile_3d_configs
GROUP BY base_model_id, animation_pose, backdrop_id;

CREATE OR REPLACE VIEW vw_adaptive_messenger_engagement AS
SELECT
    u.user_id,
    u.display_name,
    COUNT(DISTINCT mt.thread_id) AS active_threads,
    COUNT(DISTINCT mm.message_id) AS total_messages,
    COALESCE(SUM(uas.affinity_weight), 0) AS cumulative_learning_score
FROM user_profiles u
LEFT JOIN messenger_threads mt ON mt.owner_user_id = u.user_id
LEFT JOIN messenger_messages mm ON mm.thread_id = mt.thread_id
LEFT JOIN user_affinity_signals uas ON uas.user_id = u.user_id
GROUP BY u.user_id, u.display_name;

-- ----------------------------------------------------------------------------
-- 12. Canonical Seed Data (6 Life Stages + Demo Pioneer Account)
-- ----------------------------------------------------------------------------
INSERT INTO life_stages (
    stage_id, stage_label, default_age, emoji, accent_hex, guardrail_badge,
    headline, summary, default_metric_label, default_metric_target, default_gear_id, sort_order
) VALUES
    ('kid', 'Kid', 10, '🚀', '#10B981', 'COPPA Kid-Safe Space',
     'STEM Curiosity & Cosmic Explorer Hub',
     'Playful science missions, math blitz puzzles, and moderated discovery badges designed for young learners.',
     'STEM Missions Completed', 5, 'visor', 1),
    ('teen', 'Teen', 15, '⚡', '#EC4899', 'Youth Creator Guardrails',
     'Creator Lab, Hackathons & Volunteer Impact',
     'Track community service hours, build Kotlin & TypeScript projects, and map out early scholarships.',
     'Service Hours Logged', 20, 'headphones', 2),
    ('student', 'Student', 20, '🎓', '#6366F1', 'Higher Ed & Trade Track',
     'FAFSA Aid, Exam Prep & Paid Internships',
     'Organize grant deadlines, AI flashcard study blocks, and paid internship or apprenticeship applications.',
     'Study Focus Hours', 25, 'glasses', 3),
    ('adult', 'Adult', 28, '🏡', '#06B6D4', 'Community Stability Suite',
     'Financial Resilience, 211 Support & Career Balance',
     'Manage emergency fund checkpoints, access verified 211 housing/health directories, and track weekly wellness.',
     'Stability Check-Ins', 10, 'halo', 4),
    ('business', 'Business', 34, '💼', '#F59E0B', 'Founder & Enterprise Suite',
     'SBA Capital Readiness, LLC Compliance & Storefront',
     'Track LLC/EIN milestones, prepare SBA 7(a) micro-grant packets, and grow local community commerce.',
     'Capital Readiness Score', 100, 'crown', 5),
    ('veteran', 'Veteran', 38, '🎖️', '#8B5CF6', '24/7 988 Veterans Support',
     'VA PACT Act Health, GI Bill & DAV Peer Network',
     'Coordinate VA medical visits, confirm Post-9/11 GI Bill stipends, and connect with peer mentors.',
     'Transition Steps Done', 6, 'jetpack', 6)
ON CONFLICT (stage_id) DO NOTHING;

INSERT INTO user_profiles (
    user_id, display_name, handle, email, role, age, location, bio,
    savings_balance_cents, savings_goal_cents, virtual_currency_balance
) VALUES (
    'primary_user',
    'Jordan Vance',
    '@jordan_vance',
    'jordan@wonderingworld.app',
    'veteran',
    28,
    'Spokane, WA',
    'We are all equal. Building community, creativity, and stability across every stage of life.',
    245075,
    500000,
    420
) ON CONFLICT (user_id) DO NOTHING;

INSERT INTO profile_3d_configs (
    config_id, user_id, config_name, base_model_id, base_model_name,
    primary_color_hex, secondary_color_hex, aura_color_hex,
    headgear_id, eyewear_id, aura_id, rig_id, animation_pose, backdrop_id, metallic_finish
) VALUES (
    'active_primary_3d',
    'primary_user',
    'Cosmic Explorer',
    'humanoid-rig',
    'Humanoid Prime',
    '#00E5FF',
    '#0F172A',
    '#00E676',
    'head_crown',
    'eye_visor',
    'aura_drones',
    'rig_pauldrons',
    'idle',
    'cyber-grid',
    0.720
) ON CONFLICT (config_id) DO NOTHING;

COMMIT;
