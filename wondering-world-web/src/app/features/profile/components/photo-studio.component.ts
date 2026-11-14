// src/app/features/profile/components/photo-studio.component.ts
import { NgFor, NgIf } from '@angular/common';
import {
  Component,
  computed,
  ElementRef,
  EventEmitter,
  inject,
  OnDestroy,
  Output,
  signal,
  ViewChild,
} from '@angular/core';
import {
  AvatarStudioSavedEvent,
  CameraPermissionState,
  StudioFilterId,
  StudioFilterPreset,
  StudioFrameId,
  StudioFramePreset,
} from '../../../data-access/models/photo-studio.model';
import { ProfileService } from '../../../data-access/services/profile.service';

export const STUDIO_FILTERS: readonly StudioFilterPreset[] = [
  {
    id: 'original',
    label: 'Original',
    cssFilter: 'none',
    description: 'True-to-life natural tone',
  },
  {
    id: 'vibrant',
    label: 'Vibrant',
    cssFilter: 'saturate(1.45) contrast(1.12)',
    description: 'Boosted color depth and clarity',
  },
  {
    id: 'golden',
    label: 'Golden Hour',
    cssFilter: 'sepia(0.38) saturate(1.35) brightness(1.06)',
    description: 'Warm sunlit studio portrait',
  },
  {
    id: 'noir',
    label: 'Noir Classic',
    cssFilter: 'grayscale(1) contrast(1.28)',
    description: 'Crisp high-contrast monochrome',
  },
  {
    id: 'neon',
    label: 'Neon Pop',
    cssFilter: 'hue-rotate(285deg) saturate(1.6) contrast(1.12)',
    description: 'Electric cyber color shift',
  },
  {
    id: 'soft-glow',
    label: 'Soft Glow',
    cssFilter: 'brightness(1.08) contrast(0.94) saturate(1.18)',
    description: 'Gentle airy portrait bloom',
  },
];

export const STUDIO_FRAMES: readonly StudioFramePreset[] = [
  {
    id: 'none',
    label: 'Clean',
    ringColor: '#334155',
    borderStyle: '2px solid #334155',
    glowShadow: 'none',
    badgeLabel: '',
  },
  {
    id: 'aurora',
    label: 'Aurora Ring',
    ringColor: '#38bdf8',
    borderStyle: '4px solid #38bdf8',
    glowShadow: '0 0 24px rgba(56, 189, 248, 0.45)',
    badgeLabel: 'AURORA',
  },
  {
    id: 'gold-ring',
    label: 'Gold Crest',
    ringColor: '#f59e0b',
    borderStyle: '4px solid #fbbf24',
    glowShadow: '0 0 24px rgba(245, 158, 11, 0.45)',
    badgeLabel: 'GOLD',
  },
  {
    id: 'polaroid',
    label: 'Polaroid',
    ringColor: '#f8fafc',
    borderStyle: '6px solid #f8fafc',
    glowShadow: '0 10px 28px rgba(15, 23, 42, 0.65)',
    badgeLabel: 'SNAP',
  },
  {
    id: 'cyber',
    label: 'Cyber Grid',
    ringColor: '#a855f7',
    borderStyle: '4px dashed #c084fc',
    glowShadow: '0 0 24px rgba(168, 85, 247, 0.5)',
    badgeLabel: 'CYBER',
  },
  {
    id: 'valor-crest',
    label: 'Valor Emblem',
    ringColor: '#ef4444',
    borderStyle: '4px double #f87171',
    glowShadow: '0 0 24px rgba(239, 68, 68, 0.45)',
    badgeLabel: 'VALOR',
  },
];

@Component({
  selector: 'app-photo-studio',
  standalone: true,
  imports: [NgFor, NgIf],
  template: `
    <section class="studio-card" aria-label="Avatar Photo Studio">
      <header class="studio-header">
        <div>
          <p class="eyebrow">Avatar Photo Studio</p>
          <h3>Customize Your Profile Portrait</h3>
          <p class="subtitle">
            Apply creative filters and frames, upload a photo, or capture a
            fresh portrait using your device camera.
          </p>
        </div>

        <div class="header-actions">
          <button
            type="button"
            class="secondary-btn"
            data-testid="studio-reset-btn"
            (click)="resetStudio()"
          >
            Reset
          </button>
          <button
            type="button"
            class="primary-btn"
            data-testid="studio-save-btn"
            (click)="saveToProfile()"
          >
            Save to Profile
          </button>
        </div>
      </header>

      <div class="studio-workspace">
        <!-- Live Preview & Camera Column -->
        <div class="preview-column">
          <div
            class="avatar-stage"
            [class.frame-polaroid]="selectedFrameId() === 'polaroid'"
            [style.border]="activeFrame().borderStyle"
            [style.box-shadow]="activeFrame().glowShadow"
            data-testid="studio-preview-stage"
          >
            <video
              *ngIf="cameraState() === 'granted'"
              #cameraVideo
              autoplay
              playsinline
              muted
              class="stage-media"
              [class.mirrored]="mirrorCamera()"
              [style.filter]="computedCssFilter()"
              data-testid="studio-camera-video"
            ></video>

            <img
              *ngIf="cameraState() !== 'granted'"
              [src]="previewAvatarUrl()"
              alt="Edited profile avatar preview"
              class="stage-media"
              [style.filter]="computedCssFilter()"
              data-testid="studio-preview-image"
            />

            <span
              *ngIf="activeFrame().badgeLabel"
              class="frame-badge"
              [style.background]="activeFrame().ringColor"
              data-testid="studio-frame-badge"
            >
              {{ activeFrame().badgeLabel }}
            </span>
          </div>

          <div class="camera-controls">
            <button
              *ngIf="cameraState() !== 'granted'"
              type="button"
              class="camera-btn"
              data-testid="studio-enable-camera-btn"
              [disabled]="cameraState() === 'requesting'"
              (click)="enableCamera()"
            >
              {{
                cameraState() === 'requesting'
                  ? 'Requesting Camera...'
                  : 'Use Camera'
              }}
            </button>

            <ng-container *ngIf="cameraState() === 'granted'">
              <button
                type="button"
                class="capture-btn"
                data-testid="studio-capture-btn"
                (click)="capturePhoto()"
              >
                Capture Photo
              </button>
              <button
                type="button"
                class="secondary-btn"
                data-testid="studio-mirror-btn"
                (click)="toggleMirror()"
              >
                {{ mirrorCamera() ? 'Mirror: On' : 'Mirror: Off' }}
              </button>
              <button
                type="button"
                class="danger-btn"
                data-testid="studio-stop-camera-btn"
                (click)="stopCamera()"
              >
                Stop Camera
              </button>
            </ng-container>

            <label class="upload-btn">
              Upload Photo
              <input
                type="file"
                accept="image/*"
                data-testid="studio-file-input"
                (change)="onFileSelected($event)"
              />
            </label>
          </div>

          <p
            *ngIf="cameraStatusMessage()"
            class="status-banner"
            [class.warning]="
              cameraState() === 'denied' || cameraState() === 'unsupported'
            "
            data-testid="studio-camera-status"
            role="status"
          >
            {{ cameraStatusMessage() }}
          </p>

          <p
            *ngIf="saveConfirmation()"
            class="save-banner"
            data-testid="studio-save-confirmation"
            role="status"
          >
            {{ saveConfirmation() }}
          </p>
        </div>

        <!-- Filters, Fine-Tuning & Frames Column -->
        <div class="controls-column">
          <div class="control-group">
            <h4>Creative Filters</h4>
            <div class="preset-grid">
              <button
                *ngFor="let filter of filters"
                type="button"
                class="preset-chip"
                [class.active]="selectedFilterId() === filter.id"
                [attr.data-testid]="'studio-filter-' + filter.id"
                [attr.aria-pressed]="selectedFilterId() === filter.id"
                (click)="selectFilter(filter.id)"
              >
                <strong>{{ filter.label }}</strong>
                <span>{{ filter.description }}</span>
              </button>
            </div>
          </div>

          <div class="control-group sliders-row">
            <label class="slider-control">
              <span>Brightness ({{ brightness() }}%)</span>
              <input
                type="range"
                min="70"
                max="140"
                [value]="brightness()"
                data-testid="studio-brightness-slider"
                (input)="onBrightnessChange($event)"
              />
            </label>

            <label class="slider-control">
              <span>Contrast ({{ contrast() }}%)</span>
              <input
                type="range"
                min="75"
                max="150"
                [value]="contrast()"
                data-testid="studio-contrast-slider"
                (input)="onContrastChange($event)"
              />
            </label>
          </div>

          <div class="control-group">
            <h4>Avatar Frames</h4>
            <div class="frame-grid">
              <button
                *ngFor="let frame of frames"
                type="button"
                class="frame-chip"
                [class.active]="selectedFrameId() === frame.id"
                [attr.data-testid]="'studio-frame-' + frame.id"
                [attr.aria-pressed]="selectedFrameId() === frame.id"
                (click)="selectFrame(frame.id)"
              >
                <span
                  class="frame-swatch"
                  [style.border-color]="frame.ringColor"
                ></span>
                <span>{{ frame.label }}</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>
  `,
  styles: [
    `
      .studio-card {
        margin-top: 24px;
        background: #1e293b;
        border: 1px solid #334155;
        border-radius: 18px;
        padding: 24px;
        display: grid;
        gap: 22px;
      }

      .studio-header {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        align-items: flex-start;
        gap: 16px;
      }

      .eyebrow {
        margin: 0 0 4px;
        text-transform: uppercase;
        letter-spacing: 0.08em;
        font-size: 0.72rem;
        color: #38bdf8;
      }

      .studio-header h3 {
        margin: 0 0 6px;
        font-size: 1.3rem;
        color: #f8fafc;
      }

      .subtitle {
        margin: 0;
        color: #cbd5e1;
        max-width: 60ch;
        font-size: 0.92rem;
      }

      .header-actions {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
      }

      .studio-workspace {
        display: grid;
        grid-template-columns: minmax(240px, 310px) 1fr;
        gap: 24px;
        align-items: start;
      }

      @media (max-width: 820px) {
        .studio-workspace {
          grid-template-columns: 1fr;
        }
      }

      .preview-column {
        display: grid;
        gap: 14px;
        justify-items: center;
        background: #0f172a;
        border: 1px solid #334155;
        border-radius: 16px;
        padding: 20px;
      }

      .avatar-stage {
        position: relative;
        width: 210px;
        height: 210px;
        border-radius: 50%;
        overflow: hidden;
        background: #020817;
        transition: border 0.25s ease, box-shadow 0.25s ease,
          border-radius 0.25s ease;
      }

      .avatar-stage.frame-polaroid {
        border-radius: 18px;
      }

      .stage-media {
        width: 100%;
        height: 100%;
        object-fit: cover;
        display: block;
      }

      .stage-media.mirrored {
        transform: scaleX(-1);
      }

      .frame-badge {
        position: absolute;
        bottom: 10px;
        left: 50%;
        transform: translateX(-50%);
        padding: 3px 10px;
        border-radius: 999px;
        color: #020817;
        font-size: 0.68rem;
        font-weight: 800;
        letter-spacing: 0.08em;
      }

      .camera-controls {
        width: 100%;
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        gap: 8px;
      }

      button,
      .upload-btn {
        min-height: 40px;
        padding: 8px 14px;
        border-radius: 10px;
        border: 1px solid #475569;
        background: #1e293b;
        color: #f8fafc;
        font-size: 0.86rem;
        font-weight: 600;
        cursor: pointer;
        display: inline-flex;
        align-items: center;
        justify-content: center;
      }

      .primary-btn {
        background: #2563eb;
        border-color: #3b82f6;
      }

      .camera-btn {
        background: #0ea5e9;
        border-color: #38bdf8;
        color: #082f49;
      }

      .capture-btn {
        background: #10b981;
        border-color: #34d399;
        color: #022c22;
      }

      .danger-btn {
        background: rgba(239, 68, 68, 0.16);
        border-color: #ef4444;
        color: #fecaca;
      }

      .upload-btn input {
        display: none;
      }

      .status-banner,
      .save-banner {
        width: 100%;
        margin: 0;
        padding: 10px 12px;
        border-radius: 10px;
        font-size: 0.84rem;
        line-height: 1.4;
      }

      .status-banner {
        background: rgba(56, 189, 248, 0.14);
        border: 1px solid #0284c7;
        color: #bae6fd;
      }

      .status-banner.warning {
        background: rgba(245, 158, 11, 0.14);
        border-color: #f59e0b;
        color: #fde68a;
      }

      .save-banner {
        background: rgba(16, 185, 129, 0.14);
        border: 1px solid #10b981;
        color: #a7f3d0;
      }

      .controls-column {
        display: grid;
        gap: 18px;
      }

      .control-group {
        background: #0f172a;
        border: 1px solid #334155;
        border-radius: 14px;
        padding: 16px;
        display: grid;
        gap: 12px;
      }

      .control-group h4 {
        margin: 0;
        font-size: 0.96rem;
        color: #f8fafc;
      }

      .preset-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(145px, 1fr));
        gap: 10px;
      }

      .preset-chip {
        min-height: 62px;
        padding: 10px 12px;
        border-radius: 12px;
        border: 1px solid #334155;
        background: #111827;
        color: #e2e8f0;
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        justify-content: center;
        gap: 3px;
        text-align: left;
      }

      .preset-chip span {
        font-size: 0.74rem;
        color: #94a3b8;
        font-weight: 400;
      }

      .preset-chip.active,
      .frame-chip.active {
        border: 2px solid #38bdf8;
        background: rgba(56, 189, 248, 0.14);
      }

      .sliders-row {
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      }

      .slider-control {
        display: grid;
        gap: 6px;
        font-size: 0.85rem;
        color: #cbd5e1;
      }

      .frame-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(130px, 1fr));
        gap: 10px;
      }

      .frame-chip {
        min-height: 46px;
        padding: 8px 12px;
        border-radius: 12px;
        border: 1px solid #334155;
        background: #111827;
        color: #f8fafc;
        justify-content: flex-start;
        gap: 8px;
      }

      .frame-swatch {
        width: 18px;
        height: 18px;
        border-radius: 50%;
        border: 3px solid #334155;
        flex-shrink: 0;
      }
    `,
  ],
})
export class PhotoStudioComponent implements OnDestroy {
  private readonly profileService = inject(ProfileService);

  @Output() readonly avatarSaved = new EventEmitter<AvatarStudioSavedEvent>();

  private videoElementRef?: ElementRef<HTMLVideoElement>;
  private activeMediaStream: MediaStream | null = null;

  @ViewChild('cameraVideo') set cameraVideoElement(
    ref: ElementRef<HTMLVideoElement> | undefined
  ) {
    this.videoElementRef = ref;
    if (ref && this.activeMediaStream) {
      ref.nativeElement.srcObject = this.activeMediaStream;
      ref.nativeElement.play().catch(() => undefined);
    }
  }

  readonly filters = STUDIO_FILTERS;
  readonly frames = STUDIO_FRAMES;

  private readonly initialProfile = this.profileService.currentProfile();

  readonly previewAvatarUrl = signal<string>(
    this.initialProfile.avatarUrl ??
      'https://images.unsplash.com/photo-1500648767791-00dcc994a43e'
  );
  readonly selectedFilterId = signal<StudioFilterId>(
    this.initialProfile.avatarFilterId ?? 'original'
  );
  readonly selectedFrameId = signal<StudioFrameId>(
    this.initialProfile.avatarFrameId ?? 'none'
  );
  readonly brightness = signal<number>(100);
  readonly contrast = signal<number>(100);
  readonly mirrorCamera = signal<boolean>(true);
  readonly cameraState = signal<CameraPermissionState>('idle');
  readonly cameraStatusMessage = signal<string>('');
  readonly saveConfirmation = signal<string>('');

  readonly activeFilter = computed<StudioFilterPreset>(
    () =>
      this.filters.find(f => f.id === this.selectedFilterId()) ??
      this.filters[0]
  );

  readonly activeFrame = computed<StudioFramePreset>(
    () =>
      this.frames.find(f => f.id === this.selectedFrameId()) ?? this.frames[0]
  );

  readonly computedCssFilter = computed<string>(() => {
    const preset = this.activeFilter().cssFilter;
    const base = preset === 'none' ? '' : `${preset} `;
    return `${base}brightness(${this.brightness()}%) contrast(${this.contrast()}%)`;
  });

  async enableCamera(): Promise<void> {
    this.saveConfirmation.set('');
    if (
      typeof navigator === 'undefined' ||
      !navigator.mediaDevices ||
      typeof navigator.mediaDevices.getUserMedia !== 'function'
    ) {
      this.cameraState.set('unsupported');
      this.cameraStatusMessage.set(
        'Camera capture is unavailable in this browser. Upload a photo or style your current avatar.'
      );
      return;
    }

    this.cameraState.set('requesting');
    this.cameraStatusMessage.set('Requesting camera permission...');

    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: {
          facingMode: 'user',
          width: { ideal: 720 },
          height: { ideal: 720 },
        },
        audio: false,
      });

      this.stopActiveTracks();
      this.activeMediaStream = stream;
      this.cameraState.set('granted');
      this.cameraStatusMessage.set(
        'Camera ready. Preview filters live and click Capture Photo when ready.'
      );

      if (this.videoElementRef) {
        this.videoElementRef.nativeElement.srcObject = stream;
        this.videoElementRef.nativeElement.play().catch(() => undefined);
      }
    } catch (error) {
      const message =
        error instanceof Error ? error.message.toLowerCase() : '';
      const name =
        error instanceof Error ? error.name.toLowerCase() : '';
      if (
        name.includes('notallowed') ||
        name.includes('permission') ||
        message.includes('denied') ||
        message.includes('permission')
      ) {
        this.cameraState.set('denied');
        this.cameraStatusMessage.set(
          'Camera permission was denied. Enable camera access in browser settings or upload a photo instead.'
        );
      } else {
        this.cameraState.set('denied');
        this.cameraStatusMessage.set(
          'Unable to access camera hardware. You can upload a local image or customize your current avatar.'
        );
      }
    }
  }

  capturePhoto(): void {
    const videoEl = this.videoElementRef?.nativeElement;
    if (!videoEl) {
      return;
    }

    const width = videoEl.videoWidth || 480;
    const height = videoEl.videoHeight || 480;
    const canvas = document.createElement('canvas');
    canvas.width = width;
    canvas.height = height;

    const ctx = canvas.getContext('2d');
    if (ctx) {
      if ('filter' in ctx) {
        ctx.filter = this.computedCssFilter();
      }
      if (this.mirrorCamera()) {
        ctx.translate(width, 0);
        ctx.scale(-1, 1);
      }
      ctx.drawImage(videoEl, 0, 0, width, height);
      try {
        const capturedDataUrl = canvas.toDataURL('image/png');
        this.previewAvatarUrl.set(capturedDataUrl);
        this.cameraStatusMessage.set(
          'Photo captured! Fine-tune your filter or frame and click Save to Profile.'
        );
      } catch {
        this.cameraStatusMessage.set(
          'Unable to export camera frame. Please upload an image file instead.'
        );
      }
    }

    this.stopCamera(false);
  }

  stopCamera(clearMessage = true): void {
    this.stopActiveTracks();
    this.cameraState.set('idle');
    if (clearMessage) {
      this.cameraStatusMessage.set('');
    }
  }

  toggleMirror(): void {
    this.mirrorCamera.update(v => !v);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement | null;
    const file = input?.files?.[0];
    if (!file) {
      return;
    }
    const reader = new FileReader();
    reader.onload = () => {
      if (typeof reader.result === 'string') {
        this.stopCamera();
        this.previewAvatarUrl.set(reader.result);
        this.saveConfirmation.set('');
      }
    };
    reader.readAsDataURL(file);
  }

  selectFilter(filterId: StudioFilterId): void {
    this.selectedFilterId.set(filterId);
    this.saveConfirmation.set('');
  }

  selectFrame(frameId: StudioFrameId): void {
    this.selectedFrameId.set(frameId);
    this.saveConfirmation.set('');
  }

  onBrightnessChange(event: Event): void {
    const input = event.target as HTMLInputElement | null;
    this.brightness.set(Number(input?.value ?? 100));
  }

  onContrastChange(event: Event): void {
    const input = event.target as HTMLInputElement | null;
    this.contrast.set(Number(input?.value ?? 100));
  }

  resetStudio(): void {
    this.stopCamera();
    this.selectedFilterId.set('original');
    this.selectedFrameId.set('none');
    this.brightness.set(100);
    this.contrast.set(100);
    this.previewAvatarUrl.set(
      this.profileService.currentProfile().avatarUrl ??
        'https://images.unsplash.com/photo-1500648767791-00dcc994a43e'
    );
    this.saveConfirmation.set('');
  }

  saveToProfile(): void {
    const avatarUrl = this.previewAvatarUrl();
    const filterId = this.selectedFilterId();
    const frameId = this.selectedFrameId();

    this.profileService.updateAvatarCustomization(avatarUrl, filterId, frameId);
    this.saveConfirmation.set(
      `Saved ${this.activeFilter().label} filter and ${this.activeFrame().label} frame to your profile!`
    );
    this.avatarSaved.emit({ avatarUrl, filterId, frameId });
  }

  ngOnDestroy(): void {
    this.stopActiveTracks();
  }

  private stopActiveTracks(): void {
    if (this.activeMediaStream) {
      for (const track of this.activeMediaStream.getTracks()) {
        track.stop();
      }
      this.activeMediaStream = null;
    }
    if (this.videoElementRef) {
      this.videoElementRef.nativeElement.srcObject = null;
    }
  }
}
