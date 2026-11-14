# Stages Of Life — Create Your Life

An Android (Kotlin + Jetpack Compose + Material Design 3) life-stage community, creativity, and personal growth platform backed by Firebase Auth (Google Sign-In via Credential Manager), Cloud Firestore, and Gemini AI.

## Key Features

- **State-Based Interactive Persona Dashboard (`PersonaDashboardComponent`)**: Toggle seamlessly between **Kid**, **Teen**, **Student**, **Adult**, **Business**, and **Veteran** views with tailored stage modules, checklists, metrics, and Cloud Firestore progress sync (`users/{uid}/persona_stage_data/{stageId}`).
- **Unified Top Header (`UnifiedSearchAndThemeHeaderBar`)**: Combined Universal Platform Search (Users, Posts, Resources, 3D & Apps + Web Search), System-Wide Dark/Light Mode toggle, Late-Night Eye Comfort mode, 1-tap Solid Color Swatches, and Background Transparency presets across every screen.
- **3D Avatar & Theme Customizer**: Real-time 3D avatar previews, color themes, and custom profile photo filters saved to Firestore (`users/{uid}/profile_3d_configs`).
- **Studio, Video Reels & AI Tools**: Short video reels, AI study tutor & flashcards, text-to-video & photo studio, and EqualCoins rewards.
- **Dedicated Settings Tab (`⚙️ Settings`)**:
  - Platform switcher (Search engine, layout density, notifications, 18+ lounge visibility)
  - All colors, custom color wheel, dark/light mode, and glass transparency controls
  - Phone & Digital Wallet setup
  - Code of Conduct & **Download Data (JSON)** export aggregating goals and preferences
  - Creator Contact & About Platform info
  - Community User Blocking (`users/{uid}/blocked_users`) and Permanent Account Deletion

## Download & Install the Android App (APK)

### Option 1: Build Locally
```bash
# 1. Clone the repository
git clone <your-github-repo-url>
cd stages-of-life

# 2. Configure API keys / secrets (optional for local AI/Firebase overrides)
cp .env.example .env

# 3. Build the installable Android APK
gradle :app:assembleDebug
```
The installable APK is generated at:
`app/build/outputs/apk/debug/app-debug.apk`

Install directly onto an Android device or emulator:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Option 2: Automated GitHub Actions Downloadable APK
Every push to `main` (and any `v*` release tag) triggers `.github/workflows/android-ci.yml` to run unit/Compose UI tests, Android Lint, and upload `stages-of-life-debug-apk` (`app-debug.apk`) as a downloadable build artifact (and GitHub Release asset on tagged versions).

### Option 3: AI Studio Export
Click **Publish to GitHub** or **Export APK / AAB** inside Google AI Studio to push this repository directly to GitHub or download signed APK/AAB packages.

## Running Verification Tests

```bash
# Start Firebase Auth & Firestore emulators for security rule tests
firebase emulators:start --only auth,firestore --project demo-no-project

# Run Firestore JS security rule tests
FIRESTORE_EMULATOR_HOST="127.0.0.1:8085" node --test firestore.test.js

# Run Android Kotlin unit, Compose UI & Firestore Rule tests
FIRESTORE_EMULATOR_HOST="127.0.0.1:8085" gradle :app:testDebugUnitTest
```

## Creator & Platform Contact
- **Email**: allengrogan33@gmail.com
- **Phone**: 509-552-3217
- **Address**: 820 N Summit Blvd Apt 205
