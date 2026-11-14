// src/app/features/community/community-resources-page.component.ts
import { Component } from '@angular/core';
import { PersonaSwitcherComponent } from '../dashboard/components/persona-switcher.component';
import { CommunityResourcesComponent } from './components/community-resources.component';

@Component({
  selector: 'app-community-resources-page',
  standalone: true,
  imports: [PersonaSwitcherComponent, CommunityResourcesComponent],
  template: `
    <section class="resources-page">
      <header class="page-header">
        <p class="eyebrow">Persona-Matched Library</p>
        <h1>Community Advice &amp; Curated Resources</h1>
        <p class="lead">
          Switch life stages anytime to fetch tailored guidance, practical steps,
          and vetted resources matched to your current role.
        </p>
      </header>

      <app-persona-switcher />
      <app-community-resources />
    </section>
  `,
  styles: [
    `
      .resources-page {
        display: grid;
        gap: 24px;
      }

      .page-header {
        background: linear-gradient(135deg, #0f172a, #1e293b);
        border: 1px solid #334155;
        border-radius: 18px;
        padding: 24px;
      }

      .eyebrow {
        margin: 0 0 6px;
        text-transform: uppercase;
        letter-spacing: 0.08em;
        font-size: 0.72rem;
        color: #38bdf8;
      }

      h1 {
        margin: 0 0 8px;
        font-size: clamp(1.5rem, 2vw, 2.1rem);
        color: #f8fafc;
      }

      .lead {
        margin: 0;
        color: #cbd5e1;
        max-width: 66ch;
        line-height: 1.5;
      }
    `,
  ],
})
export class CommunityResourcesPageComponent {}
