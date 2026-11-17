# Stages Of Life — Create Your Life

An Android (Kotlin + Jetpack Compose + Material Design 3) life-stage community, creativity, and personal growth platform backed by Firebase Auth (Google Sign-In via Credential Manager), Cloud Firestore, and Gemini AI.

## Key Features

- **State-Based Interactive Persona Dashboard (`PersonaDashboardComponent`)**: Toggle seamlessly between **Kid**, **Teen**, **Student**, **Adult**, **Business**, and **Veteran** views with tailored stage modules, checklists, metrics, and Cloud Firestore progress sync (`users/{uid}/persona_stage_data/{stageId}`).
- **Dynamic Stage-Tailored Community Hub (`PersonaCommunityHubView`)**: Dynamically surfaces stage-specific mentors, circles, guides, and quick bookmarks tailored to the active persona.
- **Lifeline & Resource Hub + Profile `'My Saved Resources'`**: Bookmark verified lifelines and support guides (`users/{uid}/saved_resources/{resourceId}`) with instant Open, Share, and Remove controls in Profile.
- **Sealed-Class Module Navigation Host (`PlatformModuleNavHost`)**: Type-safe Navigation Compose routing across `MainDashboard`, `PhotoStudio`, `FinancialLiteracy`, and `AgeGatedDatingSanctuary`.
- **Isolated 18+ Dating Sanctuary (`IsolatedAgeGatedDatingScreen`)**: Strictly separated data model (`IsolatedDatingModels`), alias-only ViewModel, calendar DOB `>= 18` calculation, third-party age-assurance handshake (Google Play Age Signals, Yoti Age Estimation, Persona 18+ Assurance), PG-13 conduct ceiling, and instant Lock & Wipe purge with zero overlap with main platform data.
- **3D Avatar Canvas & Theme Customizer**: Real-time 3D avatar previews, pose animations, color themes, and custom profile photo filters saved to Firestore (`users/{uid}/profile_3d_configs`).
- **Unified Top Header & Settings Tab**: Universal platform search, Dark/Light mode, Late-Night Eye Comfort mode, 1-tap Solid Color Swatches, glass transparency presets, Download Data (JSON) export, user blocking, and permanent account deletion.

## Repository Structure

```text
.
├── app/                     # Android app (Kotlin, Jetpack Compose, Material 3, Firebase, Navigation Compose)
├── angular-web/             # Companion Angular web platform + Three.js 3D Avatar Customizer & Download Center
├── database/                # PostgreSQL (schema.sql) and SQLite/Room (sqlite_schema.sql) platform schemas
├── scripts/
│   └── package-downloads.sh # Runs tests, builds APK + Angular bundle, packages dist-release/ + SHA-256 checksums
├── .github/workflows/
│   ├── android-ci.yml       # Automated test, lint, APK + SDK build & GitHub Release publishing
│   └── deploy-pages.yml     # Automated Angular web deployment to GitHub Pages
├── firestore.rules          # Zero-trust Firestore security rules (owner-scoped CRUD + schema validation)
├── firestore.test.js        # Firebase emulator Node.js security rules test suite
└── firebase-blueprint.json  # Firestore collection & schema blueprint
```

## Build Artifacts & Downloadable APK / SDK

### Option 1: Build All Release Artifacts Locally
```bash
# 1. Configure API keys / secrets (optional for local AI/Firebase overrides)
cp .env.example .env

# 2. Run Android verification tests, build APK, build Angular bundle & package dist-release/
bash scripts/package-downloads.sh
```
Generated artifacts in `dist-release/`:
- `stages-of-life-debug.apk` & `stages-of-life-v8.0-debug.apk` (Android APK, `app/build/outputs/apk/debug/app-debug.apk`)
- `wondering-world-avatar-3d-sdk.zip` (Android + Angular 3D & Persona SDK + SQL schemas)
- `wondering-world-web-pages.tar.gz` (Production Angular Web Pages bundle)
- `wondering-world-full-source.tar.gz` (Clean repository source archive)
- `checksums.sha256` (SHA-256 integrity manifest)

Install directly onto an Android device or emulator:
```bash
adb install -r dist-release/stages-of-life-debug.apk
```

### Option 2: Automated GitHub Releases & Actions Downloadable APK / SDK
Every push to `main` / `master` (and any `v*` release tag) triggers `.github/workflows/android-ci.yml` to run unit/Compose UI tests, Android Lint, build `stages-of-life-debug.apk` + `wondering-world-avatar-3d-sdk.zip`, and publish both to GitHub Actions artifacts & GitHub Releases (`latest-apk`), while `.github/workflows/deploy-pages.yml` deploys `angular-web` to GitHub Pages.

### Option 3: AI Studio Push to GitHub / Export
Click **Push to GitHub** or **Export APK / AAB** inside Google AI Studio to push this repository directly to GitHub or download APK/AAB packages.

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
