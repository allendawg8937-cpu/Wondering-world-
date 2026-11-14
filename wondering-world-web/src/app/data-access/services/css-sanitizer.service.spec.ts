// src/app/data-access/services/css-sanitizer.service.spec.ts
import { TestBed } from '@angular/core/testing';
import { CssSanitizerService } from './css-sanitizer.service';

describe('CssSanitizerService', () => {
  let service: CssSanitizerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CssSanitizerService);
  });

  it('allows safe profile styling rules and scopes every selector to [data-profile-scope="active"]', () => {
    const input = `
      .profile-card { border-radius: 24px; border-color: #38bdf8; }
      h1, h2 { letter-spacing: 0.06em; color: #f8fafc; }
    `;
    const report = service.sanitizeProfileCss(input);

    expect(report.blockedPatterns).toEqual([]);
    expect(report.ruleCount).toBe(2);
    expect(report.sanitizedCss).toContain(
      '[data-profile-scope="active"] .profile-card'
    );
    expect(report.sanitizedCss).toContain(
      '[data-profile-scope="active"] h1, [data-profile-scope="active"] h2'
    );
    expect(report.sanitizedCss).toContain('border-radius: 24px;');
  });

  it('blocks XSS tags, @import, url(), expression(), javascript: URIs, and fixed overlays', () => {
    const malicious = `
      </style><script>alert(1)</script>
      @import url("https://evil.test/leak.css");
      .profile-card {
        background-image: url(javascript:alert(2));
        background: java/*bypass*/script:alert(3);
        width: expression(alert(4));
        position: fixed;
        color: #fb7185;
      }
    `;
    const report = service.sanitizeProfileCss(malicious);

    expect(report.sanitizedCss).not.toContain('<script>');
    expect(report.sanitizedCss).not.toContain('</style>');
    expect(report.sanitizedCss).not.toContain('@import');
    expect(report.sanitizedCss).not.toContain('url(');
    expect(report.sanitizedCss).not.toContain('expression(');
    expect(report.sanitizedCss).not.toContain('javascript');
    expect(report.sanitizedCss).not.toContain('fixed');
    expect(report.sanitizedCss).toContain('color: #fb7185;');
    expect(report.blockedPatterns.length).toBeGreaterThanOrEqual(4);
  });
});
