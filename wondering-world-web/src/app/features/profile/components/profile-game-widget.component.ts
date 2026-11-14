// src/app/features/profile/components/profile-game-widget.component.ts
import {
  Component,
  DestroyRef,
  ElementRef,
  inject,
  Input,
  OnInit,
  signal,
  ViewChild,
} from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import {
  ProfileGameConfig,
  ProfileGameMode,
  ProfileWidgetInstance,
} from '../../../data-access/models/profile-customization.model';
import { ProfileCustomizationService } from '../../../data-access/services/profile-customization.service';

@Component({
  selector: 'app-profile-game-widget',
  standalone: true,
  template: `
    <div class="game-widget" data-testid="widget-game-body">
      <div class="mode-tabs">
        <button
          type="button"
          class="mini-btn"
          [class.active]="mode() === 'reaction-canvas'"
          data-testid="widget-game-mode-canvas"
          (click)="setMode('reaction-canvas')"
        >
          HTML5 Canvas Dash
        </button>
        <button
          type="button"
          class="mini-btn"
          [class.active]="mode() === 'memory-grid'"
          data-testid="widget-game-mode-memory"
          (click)="setMode('memory-grid')"
        >
          Color Match
        </button>
        <button
          type="button"
          class="mini-btn"
          [class.active]="mode() === 'custom-embed'"
          data-testid="widget-game-mode-embed"
          (click)="setMode('custom-embed')"
        >
          Embed Game URL
        </button>
      </div>

      @if (mode() === 'reaction-canvas') {
        <div class="canvas-game-box">
          <div class="hud">
            <span data-testid="widget-game-score">Score: {{ canvasScore() }}</span>
            <span>Best: {{ canvasBest() }}</span>
            <button
              type="button"
              class="mini-btn primary"
              data-testid="widget-game-start-btn"
              (click)="startCanvasRound()"
            >
              {{ canvasRunning() ? 'Restart' : 'Play (15s)' }}
            </button>
          </div>
          <canvas
            #gameCanvas
            width="280"
            height="140"
            class="arcade-canvas"
            data-testid="widget-game-canvas"
            (click)="onCanvasClick($event)"
          ></canvas>
          <p class="hint">Click glowing orbit targets on the HTML5 canvas!</p>
        </div>
      }

      @if (mode() === 'memory-grid') {
        <div class="memory-box">
          <div class="hud">
            <span>Tap Tile: <strong>{{ targetColor() }}</strong></span>
            <span>Streak: {{ memoryStreak() }}</span>
          </div>
          <div class="color-tiles">
            @for (tile of colorTiles; track tile.name) {
              <button
                type="button"
                class="color-tile"
                [style.background]="tile.hex"
                [attr.aria-label]="'Pick ' + tile.name"
                (click)="pickColorTile(tile.name)"
              >
                {{ tile.name }}
              </button>
            }
          </div>
        </div>
      }

      @if (mode() === 'custom-embed') {
        <div class="embed-box">
          <div class="embed-form">
            <input
              type="url"
              [value]="embedUrlInput()"
              (input)="onEmbedInput($event)"
              placeholder="https://example.com/mini-game"
              data-testid="widget-game-embed-input"
            />
            <button
              type="button"
              class="mini-btn primary"
              data-testid="widget-game-embed-apply"
              (click)="applyEmbedUrl()"
            >
              Load
            </button>
          </div>

          @if (embedError()) {
            <p class="embed-error" data-testid="widget-game-embed-error">
              {{ embedError() }}
            </p>
          }

          @if (safeEmbedUrl(); as safeUrl) {
            <iframe
              [src]="safeUrl"
              title="Sandboxed HTML5 Profile Game"
              sandbox="allow-scripts"
              referrerpolicy="no-referrer"
              class="game-iframe"
              data-testid="widget-game-iframe"
            ></iframe>
          } @else {
            <p class="hint">
              Enter an HTTPS HTML5 game URL. Frames run in a strict
              <code>sandbox="allow-scripts"</code> container.
            </p>
          }
        </div>
      }
    </div>
  `,
  styles: [
    `
      .game-widget {
        display: grid;
        gap: 10px;
      }

      .mode-tabs {
        display: flex;
        flex-wrap: wrap;
        gap: 6px;
      }

      .mini-btn {
        min-height: 32px;
        padding: 4px 10px;
        border-radius: 8px;
        border: 1px solid #334155;
        background: #0f172a;
        color: #cbd5e1;
        font-size: 0.78rem;
        font-weight: 600;
        cursor: pointer;
      }

      .mini-btn.active,
      .mini-btn.primary {
        border-color: var(--profile-accent, #38bdf8);
        background: rgba(56, 189, 248, 0.16);
        color: #f8fafc;
      }

      .hud {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 8px;
        font-size: 0.84rem;
        color: #e2e8f0;
      }

      .arcade-canvas {
        width: 100%;
        max-width: 280px;
        height: 140px;
        border-radius: 10px;
        border: 1px solid #334155;
        background: #020817;
        cursor: crosshair;
      }

      .color-tiles {
        display: grid;
        grid-template-columns: repeat(2, minmax(0, 1fr));
        gap: 8px;
      }

      .color-tile {
        min-height: 44px;
        border-radius: 10px;
        border: none;
        color: #020817;
        font-weight: 800;
        cursor: pointer;
      }

      .embed-form {
        display: flex;
        gap: 8px;
      }

      .embed-form input {
        flex: 1;
        min-height: 34px;
        padding: 6px 10px;
        border-radius: 8px;
        border: 1px solid #334155;
        background: #0f172a;
        color: #f8fafc;
        font-size: 0.82rem;
      }

      .game-iframe {
        width: 100%;
        height: 170px;
        border-radius: 10px;
        border: 1px solid #334155;
        background: #020817;
      }

      .hint,
      .embed-error {
        margin: 0;
        font-size: 0.78rem;
        color: #94a3b8;
      }

      .embed-error {
        color: #fca5a5;
      }
    `,
  ],
})
export class ProfileGameWidgetComponent implements OnInit {
  private readonly customizationService = inject(ProfileCustomizationService);
  private readonly domSanitizer = inject(DomSanitizer);
  private readonly destroyRef = inject(DestroyRef);

  @Input({ required: true }) widget!: ProfileWidgetInstance;
  @ViewChild('gameCanvas') gameCanvasRef?: ElementRef<HTMLCanvasElement>;

  readonly colorTiles = [
    { name: 'Sky', hex: '#38bdf8' },
    { name: 'Mint', hex: '#34d399' },
    { name: 'Amber', hex: '#fbbf24' },
    { name: 'Violet', hex: '#c084fc' },
  ] as const;

  readonly mode = signal<ProfileGameMode>('reaction-canvas');
  readonly canvasScore = signal(0);
  readonly canvasBest = signal(0);
  readonly canvasRunning = signal(false);
  readonly targetColor = signal<string>('Sky');
  readonly memoryStreak = signal(0);
  readonly embedUrlInput = signal('');
  readonly embedError = signal<string | null>(null);
  readonly safeEmbedUrl = signal<SafeResourceUrl | null>(null);

  private targetX = 140;
  private targetY = 70;
  private roundTimer?: ReturnType<typeof setTimeout>;

  ngOnInit(): void {
    const cfg = this.config();
    this.mode.set(cfg.mode);
    if (cfg.embedUrl) {
      this.embedUrlInput.set(cfg.embedUrl);
      this.applyEmbedUrl();
    }
    this.destroyRef.onDestroy(() => {
      if (this.roundTimer) {
        clearTimeout(this.roundTimer);
      }
    });
  }

  config(): ProfileGameConfig {
    return (
      this.widget.gameConfig ?? {
        mode: 'reaction-canvas',
        embedUrl: '',
      }
    );
  }

  setMode(nextMode: ProfileGameMode): void {
    this.mode.set(nextMode);
    this.customizationService.updateGameConfig(this.widget.id, {
      mode: nextMode,
    });
  }

  startCanvasRound(): void {
    if (this.roundTimer) {
      clearTimeout(this.roundTimer);
    }
    this.canvasScore.set(0);
    this.canvasRunning.set(true);
    this.spawnNextTarget();
    this.roundTimer = setTimeout(() => {
      this.canvasRunning.set(false);
    }, 15000);
  }

  onCanvasClick(event: MouseEvent): void {
    const canvas = this.gameCanvasRef?.nativeElement;
    if (!canvas) {
      return;
    }
    if (!this.canvasRunning()) {
      this.startCanvasRound();
      return;
    }
    const rect = canvas.getBoundingClientRect();
    const scaleX = canvas.width / Math.max(1, rect.width);
    const scaleY = canvas.height / Math.max(1, rect.height);
    const x = (event.clientX - rect.left) * scaleX;
    const y = (event.clientY - rect.top) * scaleY;

    const distance = Math.hypot(x - this.targetX, y - this.targetY);
    if (distance <= 24) {
      const nextScore = this.canvasScore() + 1;
      this.canvasScore.set(nextScore);
      if (nextScore > this.canvasBest()) {
        this.canvasBest.set(nextScore);
      }
      this.spawnNextTarget();
    }
  }

  pickColorTile(name: string): void {
    if (name === this.targetColor()) {
      this.memoryStreak.update(s => s + 1);
    } else {
      this.memoryStreak.set(0);
    }
    const next =
      this.colorTiles[Math.floor(Math.random() * this.colorTiles.length)];
    this.targetColor.set(next.name);
  }

  onEmbedInput(event: Event): void {
    const input = event.target as HTMLInputElement | null;
    this.embedUrlInput.set(input?.value ?? '');
  }

  applyEmbedUrl(): void {
    const raw = this.embedUrlInput().trim();
    if (!raw) {
      this.embedError.set(null);
      this.safeEmbedUrl.set(null);
      return;
    }
    try {
      const parsed = new URL(raw);
      if (parsed.protocol !== 'https:') {
        this.embedError.set(
          'Only secure HTTPS HTML5 game URLs are permitted.'
        );
        this.safeEmbedUrl.set(null);
        return;
      }
      this.embedError.set(null);
      this.safeEmbedUrl.set(
        this.domSanitizer.bypassSecurityTrustResourceUrl(parsed.toString())
      );
      this.customizationService.updateGameConfig(this.widget.id, {
        mode: 'custom-embed',
        embedUrl: parsed.toString(),
      });
    } catch {
      this.embedError.set('Enter a valid HTTPS URL (e.g. https://example.com/game).');
      this.safeEmbedUrl.set(null);
    }
  }

  private spawnNextTarget(): void {
    this.targetX = 28 + Math.floor(Math.random() * 224);
    this.targetY = 28 + Math.floor(Math.random() * 84);
    const canvas = this.gameCanvasRef?.nativeElement;
    const ctx = canvas?.getContext('2d');
    if (!ctx || !canvas) {
      return;
    }
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = '#020817';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    ctx.beginPath();
    ctx.arc(this.targetX, this.targetY, 16, 0, Math.PI * 2);
    ctx.fillStyle = '#38bdf8';
    ctx.fill();
    ctx.lineWidth = 3;
    ctx.strokeStyle = '#f8fafc';
    ctx.stroke();
  }
}
