// src/app/data-access/services/css-sanitizer.service.ts
import { Injectable } from '@angular/core';
import { CssSanitizationReport } from '../models/profile-customization.model';

const ALLOWED_PROPERTIES: ReadonlySet<string> = new Set([
  'color',
  'background',
  'background-color',
  'background-image',
  'border',
  'border-color',
  'border-width',
  'border-style',
  'border-radius',
  'box-shadow',
  'text-shadow',
  'font-family',
  'font-size',
  'font-weight',
  'font-style',
  'letter-spacing',
  'line-height',
  'text-align',
  'text-transform',
  'padding',
  'margin',
  'opacity',
  'gap',
]);

const DANGEROUS_PATTERNS: readonly { label: string; regex: RegExp }[] = [
  { label: 'HTML or style tag injection', regex: /<\/?\s*(script|style|iframe|object|embed|link|meta)[^>]*>/gi },
  { label: '@import / external at-rules', regex: /@(import|charset|namespace|font-face|keyframes)[^;{]*(;|\{[^}]*\})?/gi },
  { label: 'url() resource loading', regex: /url\s*\([^)]*\)/gi },
  { label: 'expression() script execution', regex: /expression\s*\([^)]*\)/gi },
  { label: 'javascript:/vbscript:/data: URI', regex: /(javascript|vbscript|data)\s*:/gi },
  { label: 'browser binding (-moz-binding / behavior)', regex: /(-moz-binding|behavior)\s*:[^;]*(;|$)/gi },
];

@Injectable({
  providedIn: 'root',
})
export class CssSanitizerService {
  private static readonly PROFILE_SCOPE = '[data-profile-scope="active"]';

  sanitizeProfileCss(rawCss: string): CssSanitizationReport {
    if (!rawCss || !rawCss.trim()) {
      return {
        sanitizedCss: '',
        blockedPatterns: [],
        ruleCount: 0,
        wasModified: false,
      };
    }

    const blockedSet = new Set<string>();
    let working = rawCss;

    // Strip CSS comments first so obfuscated payloads cannot split tokens.
    if (/\/\*[\s\S]*?\*\//.test(working)) {
      working = working.replace(/\/\*[\s\S]*?\*\//g, '');
    }

    for (const detector of DANGEROUS_PATTERNS) {
      if (detector.regex.test(working)) {
        blockedSet.add(detector.label);
        working = working.replace(detector.regex, '');
      }
      detector.regex.lastIndex = 0;
    }

    // Remove any remaining angle brackets to prevent tag breakout.
    if (/[<>]/.test(working)) {
      blockedSet.add('Angle bracket characters (< or >)');
      working = working.replace(/[<>]/g, '');
    }

    const safeBlocks: string[] = [];
    const ruleRegex = /([^{}]+)\{([^{}]*)\}/g;
    let match: RegExpExecArray | null = ruleRegex.exec(working);

    while (match !== null) {
      const rawSelector = match[1].trim();
      const rawDeclarations = match[2].trim();
      const scopedSelector = this.scopeSelectors(rawSelector);
      const safeDeclarations = this.sanitizeDeclarations(
        rawDeclarations,
        blockedSet
      );

      if (scopedSelector && safeDeclarations.length > 0) {
        safeBlocks.push(`${scopedSelector} { ${safeDeclarations.join(' ')} }`);
      }

      match = ruleRegex.exec(working);
    }

    const sanitizedCss = safeBlocks.join('\n');
    return {
      sanitizedCss,
      blockedPatterns: Array.from(blockedSet),
      ruleCount: safeBlocks.length,
      wasModified:
        blockedSet.size > 0 || sanitizedCss.trim() !== rawCss.trim(),
    };
  }

  private scopeSelectors(rawSelectorGroup: string): string {
    const selectors = rawSelectorGroup
      .split(',')
      .map(s => s.trim())
      .filter(s => s.length > 0 && !s.startsWith('@'));

    const scoped: string[] = [];
    for (const selector of selectors) {
      // Allow only clean CSS selectors (classes, IDs, element tags, pseudo-classes).
      if (!/^[a-zA-Z0-9\s.#_:\-[\]="*~^$|+>]+$/.test(selector)) {
        continue;
      }
      if (selector.includes('data-profile-scope')) {
        scoped.push(selector);
      } else if (/^(html|body|:root)$/i.test(selector)) {
        scoped.push(CssSanitizerService.PROFILE_SCOPE);
      } else {
        scoped.push(`${CssSanitizerService.PROFILE_SCOPE} ${selector}`);
      }
    }
    return scoped.join(', ');
  }

  private sanitizeDeclarations(
    rawDeclarations: string,
    blockedSet: Set<string>
  ): string[] {
    const safe: string[] = [];
    const entries = rawDeclarations
      .split(';')
      .map(item => item.trim())
      .filter(Boolean);

    for (const entry of entries) {
      const colonIdx = entry.indexOf(':');
      if (colonIdx <= 0) {
        continue;
      }
      const prop = entry.slice(0, colonIdx).trim().toLowerCase();
      const value = entry.slice(colonIdx + 1).trim();

      if (prop === 'position' && /fixed|sticky/i.test(value)) {
        blockedSet.add('Fixed/sticky positioning overlay');
        continue;
      }

      if (!ALLOWED_PROPERTIES.has(prop)) {
        blockedSet.add(`Disallowed CSS property (${prop})`);
        continue;
      }

      if (
        !value ||
        /[<>\\{}]/.test(value) ||
        /(url|expression|javascript|vbscript|data)\s*[:(]/i.test(value)
      ) {
        blockedSet.add(`Unsafe value in ${prop}`);
        continue;
      }

      safe.push(`${prop}: ${value};`);
    }

    return safe;
  }
}
