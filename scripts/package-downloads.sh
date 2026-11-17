#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "==> 1. Running Android unit & UI verification tests..."
gradle :app:testDebugUnitTest \
  --tests "com.example.ExampleUnitTest" \
  --tests "com.example.ExampleRobolectricTest" \
  --tests "com.example.ui.components.PersonaAndUnifiedHeaderTest" \
  --tests "com.example.ui.components.PersonaCommunityHubAndSavedResourcesTest" \
  --tests "com.example.ui.screens.LifeStage3DAvatarPreviewScreenTest" \
  --tests "com.example.navigation.PlatformModuleNavHostAndDatingGateTest" \
  --tests "com.example.network.SecureUrlAndSqlTest" \
  --tests "com.example.data.export.UserDataExportBuilderTest" \
  :app:assembleDebug --console=plain

echo "==> 2. Building Angular Web Pages bundle..."
if [ ! -d angular-web/node_modules/typescript ]; then
  npm --prefix angular-web ci
fi
node angular-web/node_modules/typescript/bin/tsc -p angular-web/tsconfig.spec.json --noEmit
npm --prefix angular-web run build:pages

echo "==> 3. Packaging downloadable artifacts into dist-release/..."
rm -rf dist-release
mkdir -p dist-release/sdk/angular dist-release/sdk/android dist-release/sdk/database

cp app/build/outputs/apk/debug/app-debug.apk dist-release/stages-of-life-debug.apk
cp app/build/outputs/apk/debug/app-debug.apk dist-release/stages-of-life-v8.0-debug.apk

cp angular-web/src/app/data-access/models/avatar-3d.model.ts dist-release/sdk/angular/
cp angular-web/src/app/features/profile/avatar-3d-customizer/avatar-theme-selector.component.ts dist-release/sdk/angular/
cp angular-web/src/app/features/profile/avatar-3d-customizer/avatar-3d-customizer.component.ts dist-release/sdk/angular/
cp app/src/main/java/com/example/ui/components/ThreeDAvatarCatalog.kt dist-release/sdk/android/
cp app/src/main/java/com/example/ui/components/PersonaCommunityHubView.kt dist-release/sdk/android/
cp app/src/main/java/com/example/ui/components/SavedResourcesProfileSection.kt dist-release/sdk/android/
cp app/src/main/java/com/example/ui/screens/LifeStage3DAvatarPreviewScreen.kt dist-release/sdk/android/
cp app/src/main/java/com/example/ui/navigation/PlatformModuleNavHost.kt dist-release/sdk/android/
cp app/src/main/java/com/example/dating/IsolatedAgeGatedDatingScreen.kt dist-release/sdk/android/
cp app/src/main/java/com/example/network/SecureUrlHelper.kt dist-release/sdk/android/
cp database/schema.sql dist-release/sdk/database/
cp database/sqlite_schema.sql dist-release/sdk/database/
cp .env.example dist-release/sdk/.env.example

cat << 'EOF' > dist-release/sdk/README.md
# Wondering World / Stages Of Life — 3D Avatar, SQL & Persona SDK (v8.0)
Included files:
- `angular/` — Angular 22 + Three.js WebGL 3D avatar customizer (`idle`, `wave`, `dance`, `levitate`, `victory` animations, orbital particles, PBR pedestal, theme selector, PNG snapshot export).
- `android/` — Jetpack Compose `ThreeDAvatarCatalog.kt` presets (`kid`, `teen`, `student`, `adult`, `business`, `veteran`).
- `database/` — Complete PostgreSQL (`schema.sql`) and portable SQLite/Room (`sqlite_schema.sql`) schemas for the entire platform.
- Android target config: `compileSdk 36`, `minSdk 24`, `targetSdk 36`, `versionCode 8`, `versionName "8.0"`.
EOF

(cd dist-release && zip -qr wondering-world-avatar-3d-sdk.zip sdk)
tar -czf dist-release/wondering-world-web-pages.tar.gz -C angular-web/dist/wondering-world-web browser
tar --exclude='.git' \
    --exclude='.gradle' \
    --exclude='build' \
    --exclude='app/build' \
    --exclude='angular-web/node_modules' \
    --exclude='angular-web/dist' \
    --exclude='dist-release' \
    -czf dist-release/wondering-world-full-source.tar.gz .

(
  cd dist-release
  sha256sum \
    stages-of-life-debug.apk \
    stages-of-life-v8.0-debug.apk \
    wondering-world-avatar-3d-sdk.zip \
    wondering-world-web-pages.tar.gz \
    wondering-world-full-source.tar.gz > checksums.sha256
  cp checksums.sha256 stages-of-life-debug.apk.sha256
  cp checksums.sha256 wondering-world-avatar-3d-sdk.zip.sha256
)

echo "==> Downloadable artifacts ready in dist-release/:"
ls -lh dist-release/
