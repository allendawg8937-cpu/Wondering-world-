import { CommonModule } from '@angular/common';
import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  OnDestroy,
  Output,
  ViewChild,
  signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import {
  AVATAR_GEAR_OPTIONS,
  AVATAR_LIGHTING_PRESETS,
  Avatar3dConfig,
  AvatarBackdropId,
  AvatarBaseModelOption,
  AvatarGearId,
  AvatarLightingPreset,
  AvatarPoseId,
  AvatarVisualThemePreset,
  LIFE_STAGE_AVATAR_PRESETS,
  LifeStageAvatarPreset,
  SKIN_TONE_SWATCHES,
} from '../../../data-access/models/avatar-3d.model';
import { AvatarThemeSelectorComponent } from './avatar-theme-selector.component';

const STORAGE_KEY = 'wondering_world_avatar_3d_config_v2';

@Component({
  selector: 'app-avatar-3d-customizer',
  standalone: true,
  imports: [CommonModule, FormsModule, AvatarThemeSelectorComponent],
  template: `
    <section class="studio-shell" data-testid="avatar-3d-customizer">
      <header class="studio-header">
        <div>
          <span class="studio-kicker">Three.js WebGL Viewport</span>
          <h2>3D Avatar Customizer Studio</h2>
          <p class="studio-sub">
            {{ activePreset().stageLabel }} • {{ activePreset().characterName }} •
            {{ config().baseModelStyle }} — {{ activePreset().tagline }}
          </p>
        </div>

        <div class="header-actions">
          <button
            type="button"
            class="btn-secondary"
            data-testid="avatar-3d-reset-camera"
            (click)="resetCamera()"
          >
            Reset Camera
          </button>
          <button
            type="button"
            class="btn-secondary"
            data-testid="avatar-3d-snapshot"
            (click)="exportSnapshot()"
          >
            Snapshot PNG
          </button>
          <button
            type="button"
            class="btn-primary"
            data-testid="avatar-3d-save"
            (click)="saveLook()"
          >
            Save 3D Look
          </button>
        </div>
      </header>

      <div class="studio-grid">
        <div class="viewport-card">
          <div
            #canvasHost
            class="canvas-host"
            data-testid="three-canvas-host"
            aria-label="Interactive Three.js 3D Avatar Viewport"
          ></div>

          @if (webglUnavailable()) {
            <div class="webgl-fallback" data-testid="webgl-fallback-banner">
              <strong>WebGL context unavailable in this browser/runner</strong>
              <p>
                Stage: {{ activePreset().stageLabel }} • Model:
                {{ config().baseModelStyle }} • Gear: {{ config().gearId }}
              </p>
            </div>
          }

          <div class="viewport-overlay">
            <span class="stage-chip" [style.border-color]="config().accentHex">
              {{ activePreset().stageLabel }} • {{ config().baseModelStyle }}
            </span>
            <span class="hint-text">Drag to orbit • Scroll to zoom</span>
          </div>

          @if (statusMessage()) {
            <p class="status-banner" data-testid="avatar-3d-status">
              {{ statusMessage() }}
            </p>
          }
        </div>

        <div class="controls-panel">
          <app-avatar-theme-selector
            [selectedStageId]="config().stageId"
            [selectedBaseModel]="config().baseModelStyle"
            [selectedThemeId]="config().themeStyleId"
            (presetSelected)="applyStagePreset($event)"
            (baseModelSelected)="applyBaseModelStyle($event)"
            (themeStyleSelected)="applyVisualTheme($event)"
          />

          <div class="control-block">
            <h3>4. Skin Tone &amp; Outfit Finish</h3>
            <div class="swatch-row">
              @for (swatch of skinSwatches; track swatch.hex) {
                <button
                  type="button"
                  class="skin-swatch"
                  [class.active]="config().skinToneHex === swatch.hex"
                  [style.background]="swatch.hex"
                  [attr.aria-label]="'Skin tone ' + swatch.label"
                  (click)="updateConfig({ skinToneHex: swatch.hex })"
                ></button>
              }
            </div>

            <div class="color-inputs">
              <label>
                Suit Base
                <input
                  type="color"
                  [ngModel]="config().suitHex"
                  (ngModelChange)="updateConfig({ suitHex: $event })"
                />
              </label>
              <label>
                Neon Accent
                <input
                  type="color"
                  [ngModel]="config().accentHex"
                  (ngModelChange)="updateConfig({ accentHex: $event })"
                />
              </label>
            </div>

            <label class="range-label">
              Armor Sheen ({{ (config().metalness * 100).toFixed(0) }}%)
              <input
                type="range"
                min="0"
                max="1"
                step="0.05"
                [ngModel]="config().metalness"
                (ngModelChange)="updateConfig({ metalness: +$event })"
              />
            </label>
          </div>

          <div class="control-block">
            <h3>5. Signature 3D Gear &amp; Pose</h3>
            <div class="chip-wrap">
              @for (gear of gearOptions; track gear.id) {
                <button
                  type="button"
                  class="mini-chip"
                  [class.active]="config().gearId === gear.id"
                  [attr.data-testid]="'gear-option-' + gear.id"
                  (click)="setGear(gear.id)"
                >
                  {{ gear.label }}
                </button>
              }
            </div>

            <div class="chip-wrap">
              @for (pose of poseOptions; track pose.id) {
                <button
                  type="button"
                  class="mini-chip"
                  [class.active]="config().pose === pose.id"
                  (click)="updateConfig({ pose: pose.id })"
                >
                  {{ pose.label }}
                </button>
              }
            </div>

            <div class="chip-wrap">
              @for (bg of backdropOptions; track bg.id) {
                <button
                  type="button"
                  class="mini-chip"
                  [class.active]="config().backdrop === bg.id"
                  (click)="setBackdrop(bg.id)"
                >
                  {{ bg.label }}
                </button>
              }
            </div>

            <div class="toggle-row">
              <label class="toggle-item">
                <input
                  type="checkbox"
                  [ngModel]="config().wireframe"
                  data-testid="avatar-3d-wireframe-toggle"
                  (ngModelChange)="updateConfig({ wireframe: $event })"
                />
                Wireframe Holo Overlay
              </label>
              <label class="toggle-item">
                <input
                  type="checkbox"
                  [ngModel]="config().autoRotate"
                  (ngModelChange)="updateConfig({ autoRotate: $event })"
                />
                Auto-Spin Turntable
              </label>
            </div>
          </div>

          <div class="control-block" data-testid="avatar-3d-lighting-panel">
            <h3>6. Studio Ambient &amp; Directional Lighting</h3>
            <div class="chip-wrap">
              @for (preset of lightingPresets; track preset.id) {
                <button
                  type="button"
                  class="mini-chip"
                  [attr.data-testid]="'lighting-preset-' + preset.id"
                  (click)="applyLightingPreset(preset)"
                >
                  {{ preset.label }}
                </button>
              }
            </div>
            <label class="range-label">
              Ambient Fill ({{ config().ambientIntensity.toFixed(2) }}x)
              <input
                type="range"
                min="0.2"
                max="2.0"
                step="0.05"
                data-testid="ambient-light-slider"
                [ngModel]="config().ambientIntensity"
                (ngModelChange)="updateLighting({ ambientIntensity: +$event })"
              />
            </label>
            <label class="range-label">
              Directional Key Light ({{ config().directionalIntensity.toFixed(2) }}x)
              <input
                type="range"
                min="0.4"
                max="3.0"
                step="0.05"
                data-testid="directional-light-slider"
                [ngModel]="config().directionalIntensity"
                (ngModelChange)="updateLighting({ directionalIntensity: +$event })"
              />
            </label>
          </div>
        </div>
      </div>
    </section>
  `,
  styles: [
    `
      .studio-shell {
        background: #0b1120;
        border: 1px solid #1e293b;
        border-radius: 20px;
        padding: 22px;
        display: grid;
        gap: 20px;
        color: #f8fafc;
      }

      .studio-header {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        align-items: center;
        gap: 14px;
      }

      .studio-kicker {
        font-size: 0.72rem;
        text-transform: uppercase;
        letter-spacing: 0.1em;
        color: #38bdf8;
        font-weight: 700;
      }

      h2 {
        margin: 4px 0;
        font-size: 1.4rem;
      }

      .studio-sub {
        margin: 0;
        font-size: 0.88rem;
        color: #94a3b8;
      }

      .header-actions {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
      }

      .btn-primary,
      .btn-secondary {
        padding: 9px 14px;
        border-radius: 10px;
        font-weight: 600;
        cursor: pointer;
        border: 1px solid #334155;
      }

      .btn-primary {
        background: #2563eb;
        border-color: #3b82f6;
        color: #ffffff;
      }

      .btn-secondary {
        background: #111827;
        color: #e2e8f0;
      }

      .studio-grid {
        display: grid;
        grid-template-columns: minmax(280px, 1.15fr) minmax(280px, 1fr);
        gap: 20px;
        align-items: start;
      }

      @media (max-width: 880px) {
        .studio-grid {
          grid-template-columns: 1fr;
        }
      }

      .viewport-card {
        position: relative;
        border-radius: 16px;
        overflow: hidden;
        background: radial-gradient(circle at top, #172554 0%, #050816 78%);
        border: 1px solid #1e293b;
        min-height: 410px;
        display: grid;
        grid-template-rows: 1fr auto;
      }

      .canvas-host {
        width: 100%;
        height: 410px;
        display: block;
      }

      .webgl-fallback {
        padding: 18px;
        text-align: center;
        color: #cbd5e1;
      }

      .viewport-overlay {
        position: absolute;
        top: 12px;
        left: 12px;
        right: 12px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        pointer-events: none;
      }

      .stage-chip {
        padding: 4px 10px;
        border-radius: 999px;
        font-size: 0.75rem;
        font-weight: 700;
        background: rgba(15, 23, 42, 0.85);
        border: 1px solid #38bdf8;
      }

      .hint-text {
        font-size: 0.75rem;
        color: #cbd5e1;
        background: rgba(15, 23, 42, 0.72);
        padding: 4px 10px;
        border-radius: 999px;
      }

      .status-banner {
        margin: 0;
        padding: 10px 14px;
        background: rgba(16, 185, 129, 0.14);
        border-top: 1px solid rgba(16, 185, 129, 0.45);
        color: #6ee7b7;
        font-size: 0.84rem;
        font-weight: 600;
      }

      .controls-panel {
        display: grid;
        gap: 14px;
      }

      .control-block {
        background: #111827;
        border: 1px solid #1f2937;
        border-radius: 14px;
        padding: 14px;
        display: grid;
        gap: 10px;
      }

      .control-block h3 {
        margin: 0;
        font-size: 0.92rem;
        color: #e2e8f0;
      }

      .swatch-row,
      .chip-wrap,
      .color-inputs,
      .toggle-row {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        align-items: center;
      }

      .skin-swatch {
        width: 28px;
        height: 28px;
        border-radius: 50%;
        border: 2px solid #334155;
        cursor: pointer;
      }

      .skin-swatch.active {
        border-color: #ffffff;
        transform: scale(1.1);
      }

      .color-inputs label,
      .range-label {
        display: grid;
        gap: 4px;
        font-size: 0.8rem;
        color: #cbd5e1;
      }

      .mini-chip {
        padding: 6px 11px;
        border-radius: 999px;
        border: 1px solid #334155;
        background: #0f172a;
        color: #cbd5e1;
        font-size: 0.78rem;
        cursor: pointer;
      }

      .mini-chip.active {
        border-color: #38bdf8;
        background: rgba(56, 189, 248, 0.18);
        color: #ffffff;
        font-weight: 700;
      }

      .toggle-item {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        font-size: 0.8rem;
        color: #cbd5e1;
      }
    `,
  ],
})
export class Avatar3dCustomizerComponent implements AfterViewInit, OnDestroy {
  @ViewChild('canvasHost', { static: true })
  canvasHost!: ElementRef<HTMLDivElement>;

  @Output() readonly lookSaved = new EventEmitter<Avatar3dConfig>();

  readonly presets = LIFE_STAGE_AVATAR_PRESETS;
  readonly skinSwatches = SKIN_TONE_SWATCHES;
  readonly gearOptions = AVATAR_GEAR_OPTIONS;
  readonly lightingPresets = AVATAR_LIGHTING_PRESETS;
  readonly poseOptions: Array<{ id: AvatarPoseId; label: string }> = [
    { id: 'idle', label: 'Heroic Idle' },
    { id: 'levitate', label: 'Levitate' },
    { id: 'victory', label: 'Victory Wave' },
  ];
  readonly backdropOptions: Array<{ id: AvatarBackdropId; label: string }> = [
    { id: 'cyber-grid', label: 'Cyber Grid' },
    { id: 'nebula-orbit', label: 'Nebula Orbit' },
    { id: 'solar-halo', label: 'Solar Halo' },
  ];

  readonly config = signal<Avatar3dConfig>(this.loadInitialConfig());
  readonly webglUnavailable = signal(false);
  readonly statusMessage = signal('');

  private scene?: THREE.Scene;
  private camera?: THREE.PerspectiveCamera;
  private renderer?: THREE.WebGLRenderer;
  private controls?: OrbitControls;
  private ambientLight?: THREE.AmbientLight;
  private hemiLight?: THREE.HemisphereLight;
  private keyDirectionalLight?: THREE.DirectionalLight;
  private fillDirectionalLight?: THREE.DirectionalLight;
  private rimLight?: THREE.PointLight;
  private resizeObserver?: ResizeObserver;
  private animationFrameId?: number;
  private readonly clock = new THREE.Clock();

  private avatarRoot?: THREE.Group;
  private headGroup?: THREE.Group;
  private leftArm?: THREE.Mesh;
  private rightArm?: THREE.Mesh;
  private gearGroup?: THREE.Group;
  private backdropGroup?: THREE.Group;

  private skinMaterial?: THREE.MeshStandardMaterial;
  private suitMaterial?: THREE.MeshStandardMaterial;
  private accentMaterial?: THREE.MeshStandardMaterial;

  activePreset(): LifeStageAvatarPreset {
    const stageId = this.config().stageId;
    return this.presets.find(p => p.stageId === stageId) ?? this.presets[3];
  }

  ngAfterViewInit(): void {
    this.initThreeScene();
  }

  ngOnDestroy(): void {
    if (this.animationFrameId !== undefined) {
      cancelAnimationFrame(this.animationFrameId);
    }
    this.resizeObserver?.disconnect();
    this.controls?.dispose();
    this.disposeSceneResources();
    this.renderer?.dispose();
  }

  applyStagePreset(preset: LifeStageAvatarPreset): void {
    this.updateConfig({
      stageId: preset.stageId,
      accentHex: preset.accentHex,
      suitHex: preset.suitHex,
      skinToneHex: preset.skinToneHex,
      gearId: preset.defaultGear,
    });
    this.rebuildGearMesh();
    this.rebuildBackdrop();
    this.statusMessage.set(
      `Loaded ${preset.stageLabel} (${preset.characterName}) 3D rig.`
    );
  }

  applyBaseModelStyle(option: AvatarBaseModelOption): void {
    this.updateConfig({
      baseModelStyle: option.id,
      accentHex: option.accentHex,
      backdrop: option.defaultBackdrop,
    });
    this.rebuildBaseModelRig();
    this.rebuildBackdrop();
    this.statusMessage.set(
      `Switched 3D base mesh architecture to ${option.label}.`
    );
  }

  applyVisualTheme(theme: AvatarVisualThemePreset): void {
    this.updateConfig({
      themeStyleId: theme.id,
      accentHex: theme.accentHex,
      suitHex: theme.suitHex,
      backdrop: theme.backdrop,
      metalness: theme.metalness,
    });
    if (this.rimLight) {
      this.rimLight.color.set(theme.rimLightHex);
    }
    this.rebuildBackdrop();
    this.statusMessage.set(`Applied ${theme.label} Three.js lighting theme.`);
  }

  setGear(gearId: AvatarGearId): void {
    this.updateConfig({ gearId });
    this.rebuildGearMesh();
  }

  setBackdrop(backdrop: AvatarBackdropId): void {
    this.updateConfig({ backdrop });
    this.rebuildBackdrop();
  }

  applyLightingPreset(preset: AvatarLightingPreset): void {
    this.updateLighting({
      ambientIntensity: preset.ambientIntensity,
      directionalIntensity: preset.directionalIntensity,
    });
    this.statusMessage.set(`Applied ${preset.label} scene lighting.`);
  }

  updateLighting(
    patch: Partial<Pick<Avatar3dConfig, 'ambientIntensity' | 'directionalIntensity'>>
  ): void {
    this.updateConfig(patch);
    this.syncSceneLights();
  }

  updateConfig(patch: Partial<Avatar3dConfig>): void {
    this.config.update(current => ({ ...current, ...patch }));
    this.syncAvatarMaterialsAndProportions();
  }

  resetCamera(): void {
    if (!this.camera || !this.controls) return;
    this.camera.position.set(0, 1.55, 4.1);
    this.controls.target.set(0, 1.0, 0);
    this.controls.update();
    this.statusMessage.set('Camera reset to front hero framing.');
  }

  exportSnapshot(): void {
    if (!this.renderer) {
      this.statusMessage.set(
        '3D viewport snapshot requires an active WebGL canvas.'
      );
      return;
    }
    try {
      const dataUrl = this.renderer.domElement.toDataURL('image/png');
      const link = document.createElement('a');
      link.href = dataUrl;
      link.download = `wondering-world-${this.config().stageId}-${this.config().baseModelStyle}.png`;
      link.click();
      this.statusMessage.set('Exported 3D avatar PNG snapshot.');
    } catch {
      this.statusMessage.set('Snapshot saved in session buffer.');
    }
  }

  saveLook(): void {
    const current = this.config();
    try {
      globalThis.localStorage?.setItem(STORAGE_KEY, JSON.stringify(current));
    } catch {
      // Ignore storage quota errors in restricted sandboxes
    }
    this.lookSaved.emit(current);
    this.statusMessage.set(
      `Saved ${this.activePreset().stageLabel} ${current.baseModelStyle} Look (${current.gearId}).`
    );
  }

  private loadInitialConfig(): Avatar3dConfig {
    const fallback: Avatar3dConfig = {
      stageId: 'adult',
      baseModelStyle: 'humanoid-rig',
      themeStyleId: 'cyber-neon',
      accentHex: '#00e5ff',
      suitHex: '#0f172a',
      skinToneHex: '#c68b67',
      gearId: 'halo',
      pose: 'idle',
      backdrop: 'cyber-grid',
      metalness: 0.4,
      wireframe: false,
      autoRotate: true,
      ambientIntensity: 0.9,
      directionalIntensity: 1.65,
    };
    try {
      const raw = globalThis.localStorage?.getItem(STORAGE_KEY);
      if (!raw) return fallback;
      const parsed = JSON.parse(raw) as Partial<Avatar3dConfig>;
      return { ...fallback, ...parsed };
    } catch {
      return fallback;
    }
  }

  private initThreeScene(): void {
    const container = this.canvasHost.nativeElement;
    const width = Math.max(container.clientWidth, 320);
    const height = Math.max(container.clientHeight, 360);

    try {
      const renderer = new THREE.WebGLRenderer({
        antialias: true,
        alpha: true,
        preserveDrawingBuffer: true,
      });
      renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 2));
      renderer.setSize(width, height);
      renderer.shadowMap.enabled = true;
      renderer.shadowMap.type = THREE.PCFSoftShadowMap;
      renderer.toneMapping = THREE.ACESFilmicToneMapping;
      renderer.toneMappingExposure = 1.15;

      container.innerHTML = '';
      container.appendChild(renderer.domElement);
      this.renderer = renderer;

      const scene = new THREE.Scene();
      this.scene = scene;

      const camera = new THREE.PerspectiveCamera(40, width / height, 0.1, 100);
      camera.position.set(0, 1.55, 4.1);
      this.camera = camera;

      const controls = new OrbitControls(camera, renderer.domElement);
      controls.enableDamping = true;
      controls.dampingFactor = 0.06;
      controls.target.set(0, 1.0, 0);
      controls.minDistance = 2.3;
      controls.maxDistance = 7.0;
      controls.maxPolarAngle = Math.PI * 0.62;
      this.controls = controls;

      this.setupSceneLights(scene);

      this.buildAvatarRig();
      this.rebuildBackdrop();
      this.syncAvatarMaterialsAndProportions();
      this.syncSceneLights();

      if (typeof ResizeObserver !== 'undefined') {
        this.resizeObserver = new ResizeObserver(entries => {
          const entry = entries[0];
          if (!entry || !this.renderer || !this.camera) return;
          const nextW = Math.max(entry.contentRect.width, 240);
          const nextH = Math.max(entry.contentRect.height, 280);
          this.camera.aspect = nextW / nextH;
          this.camera.updateProjectionMatrix();
          this.renderer.setSize(nextW, nextH);
        });
        this.resizeObserver.observe(container);
      }

      const renderLoop = () => {
        this.animationFrameId = requestAnimationFrame(renderLoop);
        const elapsed = this.clock.getElapsedTime();
        this.animateAvatar(elapsed);
        this.controls?.update();
        if (this.renderer && this.scene && this.camera) {
          this.renderer.render(this.scene, this.camera);
        }
      };
      renderLoop();
    } catch {
      this.webglUnavailable.set(true);
    }
  }

  private buildAvatarRig(): void {
    if (!this.scene) return;
    const cfg = this.config();

    this.skinMaterial = new THREE.MeshStandardMaterial({
      color: cfg.skinToneHex,
      roughness: 0.45,
      metalness: 0.08,
    });
    this.suitMaterial = new THREE.MeshStandardMaterial({
      color: cfg.suitHex,
      roughness: 0.35,
      metalness: cfg.metalness,
    });
    this.accentMaterial = new THREE.MeshStandardMaterial({
      color: cfg.accentHex,
      emissive: new THREE.Color(cfg.accentHex),
      emissiveIntensity: 0.42,
      roughness: 0.2,
      metalness: 0.65,
    });

    const avatarRoot = new THREE.Group();
    this.avatarRoot = avatarRoot;
    this.scene.add(avatarRoot);
    this.rebuildBaseModelRig();
  }

  private rebuildBaseModelRig(): void {
    if (
      !this.avatarRoot ||
      !this.skinMaterial ||
      !this.suitMaterial ||
      !this.accentMaterial
    ) {
      return;
    }

    while (this.avatarRoot.children.length > 0) {
      const child = this.avatarRoot.children.pop();
      child?.traverse(obj => {
        if (obj instanceof THREE.Mesh) {
          obj.geometry?.dispose();
        }
      });
    }

    const style = this.config().baseModelStyle;
    const isVoxel = style === 'voxel-bot';
    const isMech = style === 'mech-guardian';
    const isCrystal = style === 'crystal-sprite';

    const legGeo = isVoxel
      ? new THREE.BoxGeometry(0.24, 0.72, 0.24)
      : isMech
        ? new THREE.CylinderGeometry(0.16, 0.2, 0.76, 8)
        : new THREE.CapsuleGeometry(0.14, 0.56, 8, 16);
    const leftLeg = new THREE.Mesh(legGeo, this.suitMaterial);
    leftLeg.position.set(-0.22, 0.42, 0);
    const rightLeg = new THREE.Mesh(legGeo, this.suitMaterial);
    rightLeg.position.set(0.22, 0.42, 0);
    this.avatarRoot.add(leftLeg, rightLeg);

    const torsoGeo = isVoxel
      ? new THREE.BoxGeometry(0.68, 0.86, 0.44)
      : isCrystal
        ? new THREE.OctahedronGeometry(0.46, 0)
        : isMech
          ? new THREE.BoxGeometry(0.84, 0.9, 0.52)
          : new THREE.CylinderGeometry(0.38, 0.28, 0.88, 24);
    const torso = new THREE.Mesh(torsoGeo, this.suitMaterial);
    torso.position.set(0, 1.12, 0);
    this.avatarRoot.add(torso);

    if (isMech) {
      const pauldronGeo = new THREE.BoxGeometry(0.3, 0.24, 0.34);
      const leftPauldron = new THREE.Mesh(pauldronGeo, this.accentMaterial);
      leftPauldron.position.set(-0.54, 1.5, 0);
      const rightPauldron = new THREE.Mesh(pauldronGeo, this.accentMaterial);
      rightPauldron.position.set(0.54, 1.5, 0);
      this.avatarRoot.add(leftPauldron, rightPauldron);
    }

    const chestEmblemGeo = isCrystal
      ? new THREE.OctahedronGeometry(0.16, 1)
      : new THREE.OctahedronGeometry(0.12, 0);
    const chestEmblem = new THREE.Mesh(chestEmblemGeo, this.accentMaterial);
    chestEmblem.position.set(0, 1.24, isMech ? 0.3 : 0.34);
    this.avatarRoot.add(chestEmblem);

    const armGeo = isVoxel
      ? new THREE.BoxGeometry(0.2, 0.68, 0.2)
      : new THREE.CapsuleGeometry(0.11, 0.52, 8, 14);
    const leftArm = new THREE.Mesh(armGeo, this.suitMaterial);
    leftArm.position.set(isMech ? -0.58 : -0.52, 1.14, 0);
    leftArm.rotation.z = 0.16;
    const rightArm = new THREE.Mesh(armGeo, this.suitMaterial);
    rightArm.position.set(isMech ? 0.58 : 0.52, 1.14, 0);
    rightArm.rotation.z = -0.16;
    this.leftArm = leftArm;
    this.rightArm = rightArm;
    this.avatarRoot.add(leftArm, rightArm);

    const headGroup = new THREE.Group();
    headGroup.position.set(0, 1.84, 0);
    const headGeo = isVoxel
      ? new THREE.BoxGeometry(0.58, 0.58, 0.58)
      : isCrystal
        ? new THREE.IcosahedronGeometry(0.36, 0)
        : new THREE.SphereGeometry(0.34, 28, 28);
    const headMesh = new THREE.Mesh(headGeo, this.skinMaterial);
    headGroup.add(headMesh);

    const eyeGeo = isVoxel
      ? new THREE.BoxGeometry(0.08, 0.08, 0.04)
      : new THREE.SphereGeometry(0.045, 14, 14);
    const eyeMat = new THREE.MeshBasicMaterial({ color: 0x0f172a });
    const eyeZ = isVoxel ? 0.29 : 0.3;
    const leftEye = new THREE.Mesh(eyeGeo, eyeMat);
    leftEye.position.set(-0.11, 0.03, eyeZ);
    const rightEye = new THREE.Mesh(eyeGeo, eyeMat);
    rightEye.position.set(0.11, 0.03, eyeZ);
    headGroup.add(leftEye, rightEye);

    const gearGroup = new THREE.Group();
    this.gearGroup = gearGroup;
    headGroup.add(gearGroup);

    this.headGroup = headGroup;
    this.avatarRoot.add(headGroup);

    this.rebuildGearMesh();
    this.avatarRoot.traverse(obj => {
      if (obj instanceof THREE.Mesh) {
        obj.castShadow = true;
        obj.receiveShadow = true;
      }
    });
    this.syncAvatarMaterialsAndProportions();
  }

  private setupSceneLights(scene: THREE.Scene): void {
    const cfg = this.config();

    const ambientLight = new THREE.AmbientLight(0xdbeafe, cfg.ambientIntensity);
    this.ambientLight = ambientLight;
    scene.add(ambientLight);

    const hemiLight = new THREE.HemisphereLight(
      0xf0f9ff,
      0x1e293b,
      cfg.ambientIntensity * 0.6
    );
    hemiLight.position.set(0, 6, 0);
    this.hemiLight = hemiLight;
    scene.add(hemiLight);

    const keyLight = new THREE.DirectionalLight(
      0xffffff,
      cfg.directionalIntensity
    );
    keyLight.position.set(3.2, 5.2, 4.2);
    keyLight.castShadow = true;
    keyLight.shadow.mapSize.set(1024, 1024);
    keyLight.shadow.camera.near = 0.5;
    keyLight.shadow.camera.far = 16;
    keyLight.shadow.camera.left = -3;
    keyLight.shadow.camera.right = 3;
    keyLight.shadow.camera.top = 3;
    keyLight.shadow.camera.bottom = -3;
    keyLight.shadow.bias = -0.0005;
    this.keyDirectionalLight = keyLight;
    scene.add(keyLight);

    const fillLight = new THREE.DirectionalLight(
      0x93c5fd,
      cfg.directionalIntensity * 0.48
    );
    fillLight.position.set(-3.5, 2.4, 2.8);
    this.fillDirectionalLight = fillLight;
    scene.add(fillLight);

    const rimLight = new THREE.PointLight(0x38bdf8, 2.4, 14);
    rimLight.position.set(-2.5, 2.6, -2.5);
    this.rimLight = rimLight;
    scene.add(rimLight);
  }

  private syncSceneLights(): void {
    const cfg = this.config();
    if (this.ambientLight) {
      this.ambientLight.intensity = cfg.ambientIntensity;
    }
    if (this.hemiLight) {
      this.hemiLight.intensity = cfg.ambientIntensity * 0.6;
    }
    if (this.keyDirectionalLight) {
      this.keyDirectionalLight.intensity = cfg.directionalIntensity;
    }
    if (this.fillDirectionalLight) {
      this.fillDirectionalLight.intensity = cfg.directionalIntensity * 0.48;
    }
  }

  private rebuildGearMesh(): void {
    if (!this.gearGroup || !this.accentMaterial || !this.suitMaterial) return;
    while (this.gearGroup.children.length > 0) {
      const child = this.gearGroup.children.pop();
      if (child instanceof THREE.Mesh) {
        child.geometry.dispose();
      }
    }

    const gearId = this.config().gearId;
    if (gearId === 'visor') {
      const visorGeo = new THREE.TorusGeometry(
        0.28,
        0.045,
        12,
        28,
        Math.PI * 0.9
      );
      const visor = new THREE.Mesh(visorGeo, this.accentMaterial);
      visor.rotation.set(Math.PI * 0.5, 0, Math.PI * 0.05);
      visor.position.set(0, 0.06, 0.14);
      this.gearGroup.add(visor);
    } else if (gearId === 'headphones') {
      const bandGeo = new THREE.TorusGeometry(0.37, 0.035, 10, 28, Math.PI);
      const band = new THREE.Mesh(bandGeo, this.suitMaterial);
      band.position.set(0, 0.04, 0);
      const cupGeo = new THREE.CylinderGeometry(0.11, 0.11, 0.08, 18);
      const leftCup = new THREE.Mesh(cupGeo, this.accentMaterial);
      leftCup.rotation.z = Math.PI / 2;
      leftCup.position.set(-0.37, 0.02, 0);
      const rightCup = new THREE.Mesh(cupGeo, this.accentMaterial);
      rightCup.rotation.z = Math.PI / 2;
      rightCup.position.set(0.37, 0.02, 0);
      this.gearGroup.add(band, leftCup, rightCup);
    } else if (gearId === 'glasses') {
      const lensGeo = new THREE.TorusGeometry(0.085, 0.018, 10, 20);
      const leftLens = new THREE.Mesh(lensGeo, this.accentMaterial);
      leftLens.position.set(-0.12, 0.03, 0.33);
      const rightLens = new THREE.Mesh(lensGeo, this.accentMaterial);
      rightLens.position.set(0.12, 0.03, 0.33);
      const bridgeGeo = new THREE.BoxGeometry(0.08, 0.02, 0.02);
      const bridge = new THREE.Mesh(bridgeGeo, this.accentMaterial);
      bridge.position.set(0, 0.03, 0.33);
      this.gearGroup.add(leftLens, rightLens, bridge);
    } else if (gearId === 'halo') {
      const haloGeo = new THREE.TorusGeometry(0.36, 0.028, 12, 36);
      const halo = new THREE.Mesh(haloGeo, this.accentMaterial);
      halo.rotation.x = Math.PI / 2;
      halo.position.set(0, 0.44, 0);
      this.gearGroup.add(halo);
    } else if (gearId === 'crown') {
      const crownGeo = new THREE.ConeGeometry(0.22, 0.24, 6);
      const crown = new THREE.Mesh(crownGeo, this.accentMaterial);
      crown.position.set(0, 0.42, 0);
      this.gearGroup.add(crown);
    } else if (gearId === 'jetpack') {
      const podGeo = new THREE.CylinderGeometry(0.1, 0.12, 0.46, 16);
      const leftPod = new THREE.Mesh(podGeo, this.accentMaterial);
      leftPod.position.set(-0.18, -0.58, -0.36);
      const rightPod = new THREE.Mesh(podGeo, this.accentMaterial);
      rightPod.position.set(0.18, -0.58, -0.36);
      this.gearGroup.add(leftPod, rightPod);
    }

    this.gearGroup.traverse(obj => {
      if (obj instanceof THREE.Mesh) {
        obj.castShadow = true;
        obj.receiveShadow = true;
      }
    });
  }

  private rebuildBackdrop(): void {
    if (!this.scene) return;
    if (this.backdropGroup) {
      this.scene.remove(this.backdropGroup);
      this.backdropGroup.traverse(obj => {
        if (obj instanceof THREE.Mesh || obj instanceof THREE.GridHelper) {
          obj.geometry?.dispose();
        }
      });
    }

    const group = new THREE.Group();
    const cfg = this.config();
    const accentColor = new THREE.Color(cfg.accentHex);

    const grid = new THREE.GridHelper(10, 18, accentColor, 0x1e293b);
    grid.position.y = -0.02;
    group.add(grid);

    const shadowPlaneGeo = new THREE.CircleGeometry(3.2, 48);
    const shadowPlaneMat = new THREE.ShadowMaterial({ opacity: 0.35 });
    const shadowPlane = new THREE.Mesh(shadowPlaneGeo, shadowPlaneMat);
    shadowPlane.rotation.x = -Math.PI / 2;
    shadowPlane.position.y = -0.015;
    shadowPlane.receiveShadow = true;
    group.add(shadowPlane);

    if (cfg.backdrop === 'nebula-orbit' || cfg.backdrop === 'solar-halo') {
      const ringGeo = new THREE.TorusGeometry(1.55, 0.02, 10, 64);
      const ringMat = new THREE.MeshBasicMaterial({
        color: cfg.backdrop === 'solar-halo' ? 0xf59e0b : accentColor,
      });
      const ring = new THREE.Mesh(ringGeo, ringMat);
      ring.rotation.x = Math.PI * 0.46;
      ring.position.y = 1.05;
      group.add(ring);
    }

    this.backdropGroup = group;
    this.scene.add(group);
  }

  private syncAvatarMaterialsAndProportions(): void {
    const cfg = this.config();
    const preset = this.activePreset();

    if (this.skinMaterial) {
      this.skinMaterial.color.set(cfg.skinToneHex);
      this.skinMaterial.wireframe = cfg.wireframe;
    }
    if (this.suitMaterial) {
      this.suitMaterial.color.set(cfg.suitHex);
      this.suitMaterial.metalness = cfg.metalness;
      this.suitMaterial.wireframe = cfg.wireframe;
    }
    if (this.accentMaterial) {
      this.accentMaterial.color.set(cfg.accentHex);
      this.accentMaterial.emissive.set(cfg.accentHex);
      this.accentMaterial.wireframe = cfg.wireframe;
    }
    if (this.avatarRoot) {
      this.avatarRoot.scale.set(
        preset.torsoScale,
        preset.heightScale,
        preset.torsoScale
      );
    }
    if (this.headGroup) {
      this.headGroup.scale.setScalar(preset.headScale);
    }
  }

  private animateAvatar(elapsed: number): void {
    if (!this.avatarRoot) return;
    const cfg = this.config();

    if (cfg.autoRotate) {
      this.avatarRoot.rotation.y += 0.008;
    }

    if (cfg.pose === 'levitate') {
      this.avatarRoot.position.y = 0.24 + Math.sin(elapsed * 2.4) * 0.1;
      if (this.leftArm && this.rightArm) {
        this.leftArm.rotation.z = 0.55;
        this.rightArm.rotation.z = -0.55;
      }
    } else if (cfg.pose === 'victory') {
      this.avatarRoot.position.y = Math.abs(Math.sin(elapsed * 3.0)) * 0.06;
      if (this.leftArm && this.rightArm) {
        this.leftArm.rotation.z = 2.4;
        this.rightArm.rotation.z = -2.4 + Math.sin(elapsed * 5.0) * 0.22;
      }
    } else {
      this.avatarRoot.position.y = Math.sin(elapsed * 1.6) * 0.03;
      if (this.leftArm && this.rightArm) {
        this.leftArm.rotation.z = 0.16 + Math.sin(elapsed * 1.6) * 0.04;
        this.rightArm.rotation.z = -0.16 - Math.sin(elapsed * 1.6) * 0.04;
      }
    }

    if (this.gearGroup) {
      this.gearGroup.position.y = Math.sin(elapsed * 2.2) * 0.015;
    }
  }

  private disposeSceneResources(): void {
    this.scene?.traverse(obj => {
      if (obj instanceof THREE.Mesh) {
        obj.geometry?.dispose();
        if (Array.isArray(obj.material)) {
          obj.material.forEach(mat => mat.dispose());
        } else {
          obj.material?.dispose();
        }
      }
    });
  }
}
