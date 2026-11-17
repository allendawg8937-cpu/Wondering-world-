import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DownloadCenterComponent } from './download-center.component';

describe('DownloadCenterComponent', () => {
  let fixture: ComponentFixture<DownloadCenterComponent>;
  let component: DownloadCenterComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DownloadCenterComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DownloadCenterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should render direct APK and SDK download links', () => {
    const host = fixture.nativeElement as HTMLElement;
    const apkLink = host.querySelector(
      '[data-testid="download-apk-release-link"]'
    ) as HTMLAnchorElement | null;
    const sdkZipLink = host.querySelector(
      '[data-testid="download-sdk-zip-link"]'
    ) as HTMLAnchorElement | null;

    expect(apkLink?.href).toContain('stages-of-life-debug.apk');
    expect(sdkZipLink?.href).toContain('wondering-world-avatar-3d-sdk.zip');
  });

  it('should generate typed SDK source containing all 6 life-stage presets', () => {
    const source = component.buildSdkSource();
    expect(source).toContain('wondering-world-avatar-sdk');
    expect(source).toContain('"stageId": "kid"');
    expect(source).toContain('"stageId": "veteran"');
  });
});
