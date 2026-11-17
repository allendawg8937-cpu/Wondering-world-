import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import {
  AVATAR_BASE_MODEL_OPTIONS,
  AVATAR_VISUAL_THEME_PRESETS,
  LIFE_STAGE_AVATAR_PRESETS,
} from '../../data-access/models/avatar-3d.model';

@Component({
  selector: 'app-download-center',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="download-center" data-testid="download-center-page">
      <header class="hero">
        <div>
          <p class="eyebrow">Android App &amp; Developer SDK</p>
          <h1>Download Stages Of Life APK &amp; SDK</h1>
          <p class="subtitle">
            Install the native Android APK on any Android 7.0+ (API 24–36) phone or
            grab the typed 3D Avatar SDK for Android and Three.js web apps.
          </p>
        </div>
        <span class="version-badge">Android API 24–36 • SDK v8.0</span>
      </header>

      <div class="cards-grid">
        <article class="card">
          <h2>📱 Downloadable Android APK</h2>
          <p>
            Signed debug APK built automatically from <code>:app:assembleDebug</code>
            with Jetpack Compose, Material 3, Firebase Auth/Firestore, and 6-stage
            interactive persona switching.
          </p>
          <ul class="specs">
            <li>Package: <code>com.aistudio.wonderingworld.xqrvkl</code> (v8.0 / Build 8)</li>
            <li>Target SDK: Android 16 (API 36) • Min SDK: 24</li>
            <li>Local Output: <code>app/build/outputs/apk/debug/app-debug.apk</code></li>
          </ul>
          <div class="actions">
            <a
              class="btn btn-primary"
              [href]="latestApkReleaseUrl"
              target="_blank"
              rel="noopener noreferrer"
              data-testid="download-apk-release-link"
            >
              Download APK (GitHub Release)
            </a>
            <a
              class="btn btn-secondary"
              [href]="actionsWorkflowUrl"
              target="_blank"
              rel="noopener noreferrer"
              data-testid="download-apk-ci-link"
            >
              View CI Build Artifacts
            </a>
          </div>
        </article>

        <article class="card">
          <h2>🧰 Downloadable 3D Avatar SDK</h2>
          <p>
            Export the typed TypeScript &amp; Three.js preset SDK directly from your
            browser or download the combined Android + Angular SDK archive.
          </p>
          <ul class="specs">
            <li>Includes: 6 Life-Stage Presets, 4 Base Model Rigs &amp; 5 Animation States</li>
            <li>Runtime: Three.js WebGL + Kotlin Jetpack Compose Catalog</li>
            <li>Format: Self-contained <code>.ts</code> SDK module or <code>.zip</code> bundle</li>
          </ul>
          <div class="actions">
            <button
              type="button"
              class="btn btn-primary"
              (click)="downloadTypeScriptSdk()"
              data-testid="download-ts-sdk-button"
            >
              Download TypeScript SDK (.ts)
            </button>
            <a
              class="btn btn-secondary"
              [href]="latestSdkReleaseUrl"
              target="_blank"
              rel="noopener noreferrer"
              data-testid="download-sdk-zip-link"
            >
              Download Full SDK (.zip)
            </a>
          </div>
          @if (statusMessage()) {
            <p class="status-msg" data-testid="sdk-download-status">
              {{ statusMessage() }}
            </p>
          }
        </article>
      </div>

      <article class="card cli-card">
        <h2>⚙️ Build All Downloadable Artifacts Locally</h2>
        <pre><code>bash scripts/package-downloads.sh
adb install -r dist-release/stages-of-life-debug.apk</code></pre>
      </article>
    </section>
  `,
  styles: [
    `
      .download-center {
        display: grid;
        gap: 20px;
      }

      .hero {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        align-items: center;
        gap: 16px;
        background: var(--bg-card);
        border: 1px solid var(--border-subtle);
        border-radius: 18px;
        padding: 22px;
      }

      .eyebrow {
        margin: 0 0 6px;
        font-size: 0.72rem;
        text-transform: uppercase;
        letter-spacing: 0.08em;
        color: var(--accent-link);
      }

      h1 {
        margin: 0 0 8px;
        font-size: clamp(1.5rem, 2.2vw, 2rem);
        color: var(--text-primary);
      }

      .subtitle {
        margin: 0;
        color: var(--text-secondary);
        max-width: 68ch;
      }

      .version-badge {
        padding: 7px 12px;
        border-radius: 999px;
        border: 1px solid var(--status-border);
        background: var(--status-bg);
        color: var(--status-text);
        font-size: 0.8rem;
        font-weight: 700;
      }

      .cards-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(290px, 1fr));
        gap: 18px;
      }

      .card {
        background: var(--bg-card);
        border: 1px solid var(--border-subtle);
        border-radius: 16px;
        padding: 20px;
        display: grid;
        gap: 12px;
        align-content: start;
      }

      h2 {
        margin: 0;
        font-size: 1.15rem;
        color: var(--text-primary);
      }

      p {
        margin: 0;
        color: var(--text-secondary);
        line-height: 1.5;
      }

      .specs {
        margin: 0;
        padding-left: 18px;
        color: var(--text-muted);
        display: grid;
        gap: 5px;
        font-size: 0.88rem;
      }

      code {
        background: var(--bg-input);
        padding: 2px 6px;
        border-radius: 6px;
        color: var(--accent-link);
      }

      .actions {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
        margin-top: 4px;
      }

      .btn {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        padding: 10px 14px;
        border-radius: 10px;
        font-weight: 700;
        font-size: 0.9rem;
        text-decoration: none;
        cursor: pointer;
        border: 1px solid transparent;
      }

      .btn-primary {
        background: var(--accent-primary);
        color: #ffffff;
      }

      .btn-secondary {
        background: var(--bg-surface);
        color: var(--text-primary);
        border-color: var(--border-strong);
      }

      .status-msg {
        color: var(--status-text);
        font-size: 0.85rem;
        font-weight: 600;
      }

      pre {
        margin: 0;
        padding: 12px;
        border-radius: 10px;
        background: var(--bg-input);
        border: 1px solid var(--border-subtle);
        overflow-x: auto;
      }
    `,
  ],
})
export class DownloadCenterComponent {
  readonly latestApkReleaseUrl =
    'https://github.com/allendawg8937-cpu/Wondering-world-/releases/download/latest-apk/stages-of-life-debug.apk';
  readonly latestSdkReleaseUrl =
    'https://github.com/allendawg8937-cpu/Wondering-world-/releases/download/latest-apk/wondering-world-avatar-3d-sdk.zip';
  readonly actionsWorkflowUrl =
    'https://github.com/allendawg8937-cpu/Wondering-world-/actions/workflows/android-ci.yml';

  readonly statusMessage = signal<string | null>(null);

  buildSdkSource(): string {
    const payload = {
      sdkName: 'wondering-world-avatar-sdk',
      version: '8.0.0',
      androidPackage: 'com.aistudio.wonderingworld.xqrvkl',
      lifeStagePresets: LIFE_STAGE_AVATAR_PRESETS,
      baseModelOptions: AVATAR_BASE_MODEL_OPTIONS,
      visualThemes: AVATAR_VISUAL_THEME_PRESETS,
    };

    return [
      '// Wondering World 3D Avatar & Life-Stage SDK',
      `export const WONDERING_WORLD_SDK = ${JSON.stringify(payload, null, 2)} as const;`,
      '',
      'export function getStagePreset(stageId: string) {',
      '  return WONDERING_WORLD_SDK.lifeStagePresets.find((s) => s.stageId === stageId) ?? WONDERING_WORLD_SDK.lifeStagePresets[3];',
      '}',
      '',
    ].join('\n');
  }

  downloadTypeScriptSdk(): void {
    const source = this.buildSdkSource();
    if (typeof Blob === 'undefined' || typeof URL === 'undefined' || typeof document === 'undefined') {
      this.statusMessage.set('SDK module prepared for export.');
      return;
    }
    const blob = new Blob([source], { type: 'text/typescript;charset=utf-8' });
    const objectUrl = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = objectUrl;
    link.download = 'wondering-world-avatar-sdk.ts';
    link.click();
    URL.revokeObjectURL(objectUrl);
    this.statusMessage.set('Downloaded wondering-world-avatar-sdk.ts!');
  }
}
