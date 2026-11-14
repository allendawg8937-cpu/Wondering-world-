// src/app/features/profile/components/photo-studio.component.spec.ts
import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  AvatarStudioSavedEvent,
  StudioFilterId,
  StudioFrameId,
} from '../../../data-access/models/photo-studio.model';
import { ProfileService } from '../../../data-access/services/profile.service';
import { PhotoStudioComponent } from './photo-studio.component';

describe('PhotoStudioComponent', () => {
  let fixture: ComponentFixture<PhotoStudioComponent>;
  let component: PhotoStudioComponent;
  let profileService: ProfileService;
  let originalMediaDevicesDescriptor: PropertyDescriptor | undefined;

  beforeEach(async () => {
    localStorage.clear();
    originalMediaDevicesDescriptor = Object.getOwnPropertyDescriptor(
      navigator,
      'mediaDevices'
    );

    await TestBed.configureTestingModule({
      imports: [PhotoStudioComponent],
    }).compileComponents();

    profileService = TestBed.inject(ProfileService);
    fixture = TestBed.createComponent(PhotoStudioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    fixture.destroy();
    localStorage.clear();
    if (originalMediaDevicesDescriptor) {
      Object.defineProperty(
        navigator,
        'mediaDevices',
        originalMediaDevicesDescriptor
      );
    }
  });

  it('applies creative filters and frames and saves customizations to ProfileService', () => {
    const filters: StudioFilterId[] = [
      'original',
      'vibrant',
      'golden',
      'noir',
      'neon',
      'soft-glow',
    ];
    const frames: StudioFrameId[] = [
      'none',
      'aurora',
      'gold-ring',
      'polaroid',
      'cyber',
      'valor-crest',
    ];

    for (const id of filters) {
      expect(
        fixture.nativeElement.querySelector(`[data-testid="studio-filter-${id}"]`)
      ).not.toBeNull();
    }
    for (const id of frames) {
      expect(
        fixture.nativeElement.querySelector(`[data-testid="studio-frame-${id}"]`)
      ).not.toBeNull();
    }

    let savedEvent: AvatarStudioSavedEvent | undefined;
    const sub = component.avatarSaved.subscribe(event => {
      savedEvent = event;
    });

    (
      fixture.nativeElement.querySelector(
        '[data-testid="studio-filter-noir"]'
      ) as HTMLButtonElement
    ).click();
    (
      fixture.nativeElement.querySelector(
        '[data-testid="studio-frame-aurora"]'
      ) as HTMLButtonElement
    ).click();
    fixture.detectChanges();

    expect(component.computedCssFilter()).toContain('grayscale(1)');
    expect(
      fixture.nativeElement.querySelector('[data-testid="studio-frame-badge"]')
        ?.textContent
    ).toContain('AURORA');

    (
      fixture.nativeElement.querySelector(
        '[data-testid="studio-save-btn"]'
      ) as HTMLButtonElement
    ).click();
    fixture.detectChanges();

    expect(savedEvent?.filterId).toBe('noir');
    expect(savedEvent?.frameId).toBe('aurora');
    expect(profileService.currentProfile().avatarFilterId).toBe('noir');
    expect(profileService.currentProfile().avatarFrameId).toBe('aurora');
    expect(
      fixture.nativeElement.querySelector(
        '[data-testid="studio-save-confirmation"]'
      )
    ).not.toBeNull();

    sub.unsubscribe();
  });

  it('requests camera permission, captures a new photo when permitted, and stops media tracks', async () => {
    const stopSpy = jasmine.createSpy('stop');
    const mockStream = {
      getTracks: () => [{ stop: stopSpy }],
    } as unknown as MediaStream;

    const getUserMediaSpy = jasmine
      .createSpy('getUserMedia')
      .and.returnValue(Promise.resolve(mockStream));

    Object.defineProperty(navigator, 'mediaDevices', {
      configurable: true,
      value: { getUserMedia: getUserMediaSpy },
    });

    await component.enableCamera();
    fixture.detectChanges();

    expect(getUserMediaSpy).toHaveBeenCalled();
    expect(component.cameraState()).toBe('granted');
    expect(
      fixture.nativeElement.querySelector('[data-testid="studio-camera-video"]')
    ).not.toBeNull();

    component.capturePhoto();
    fixture.detectChanges();

    expect(stopSpy).toHaveBeenCalled();
    expect(component.cameraState()).toBe('idle');
    expect(component.previewAvatarUrl().startsWith('data:image/png')).toBeTrue();
  });

  it('handles camera permission denial and unsupported mediaDevices gracefully', async () => {
    Object.defineProperty(navigator, 'mediaDevices', {
      configurable: true,
      value: {
        getUserMedia: () =>
          Promise.reject(new DOMException('Permission denied', 'NotAllowedError')),
      },
    });

    await component.enableCamera();
    fixture.detectChanges();

    expect(component.cameraState()).toBe('denied');
    expect(
      fixture.nativeElement.querySelector('[data-testid="studio-camera-status"]')
        ?.textContent
    ).toContain('denied');

    Object.defineProperty(navigator, 'mediaDevices', {
      configurable: true,
      value: undefined,
    });

    await component.enableCamera();
    fixture.detectChanges();

    expect(component.cameraState()).toBe('unsupported');
  });
});
