// src/app/features/dashboard/components/persona-stage-icon.component.ts
import { NgSwitch, NgSwitchCase } from '@angular/common';
import { Component, Input } from '@angular/core';
import { LifePersonaId } from '../../../data-access/models/persona.model';

@Component({
  selector: 'app-persona-stage-icon',
  standalone: true,
  imports: [NgSwitch, NgSwitchCase],
  template: `
    <span
      class="icon-frame"
      [class.is-active]="active"
      [style.--icon-size.px]="size"
      [attr.data-testid]="'persona-icon-' + personaId"
      aria-hidden="true"
    >
      <span class="halo"></span>
      <ng-container [ngSwitch]="personaId">
        <!-- Kid: Floating Rocket & Twinkling Stars -->
        <svg
          *ngSwitchCase="'kid'"
          viewBox="0 0 48 48"
          class="stage-svg svg-kid"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <circle class="spark star-a" cx="10" cy="13" r="2" fill="#7dd3fc" />
          <circle class="spark star-b" cx="38" cy="11" r="1.8" fill="#bae6fd" />
          <path
            class="rocket-body"
            d="M24 7C29 12 31 19 31 26L24 31L17 26C17 19 19 12 24 7Z"
            fill="#38bdf8"
            fill-opacity="0.24"
            stroke="#38bdf8"
            stroke-width="2.2"
            stroke-linejoin="round"
          />
          <circle cx="24" cy="19" r="3" fill="#e0f2fe" />
          <path
            d="M17 23L12 28L17 29M31 23L36 28L31 29"
            stroke="#38bdf8"
            stroke-width="2.2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
          <path
            class="flame"
            d="M21 32L24 40L27 32"
            stroke="#facc15"
            stroke-width="2.4"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>

        <!-- Teen: Creator Headphones & Equalizer Waves -->
        <svg
          *ngSwitchCase="'teen'"
          viewBox="0 0 48 48"
          class="stage-svg svg-teen"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M11 26V22C11 14.8 16.8 9 24 9C31.2 9 37 14.8 37 22V26"
            stroke="#c084fc"
            stroke-width="2.4"
            stroke-linecap="round"
          />
          <rect
            x="8"
            y="24"
            width="6"
            height="12"
            rx="3"
            fill="#a855f7"
            fill-opacity="0.28"
            stroke="#a855f7"
            stroke-width="2"
          />
          <rect
            x="34"
            y="24"
            width="6"
            height="12"
            rx="3"
            fill="#a855f7"
            fill-opacity="0.28"
            stroke="#a855f7"
            stroke-width="2"
          />
          <line class="eq bar-1" x1="19" y1="33" x2="19" y2="23" stroke="#e9d5ff" stroke-width="2.5" stroke-linecap="round" />
          <line class="eq bar-2" x1="24" y1="35" x2="24" y2="19" stroke="#d8b4fe" stroke-width="2.5" stroke-linecap="round" />
          <line class="eq bar-3" x1="29" y1="33" x2="29" y2="24" stroke="#e9d5ff" stroke-width="2.5" stroke-linecap="round" />
        </svg>

        <!-- Student: Floating Mortarboard & Swinging Tassel -->
        <svg
          *ngSwitchCase="'student'"
          viewBox="0 0 48 48"
          class="stage-svg svg-student"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <g class="cap-group">
            <path
              d="M24 11L40 19L24 27L8 19L24 11Z"
              fill="#10b981"
              fill-opacity="0.24"
              stroke="#34d399"
              stroke-width="2.2"
              stroke-linejoin="round"
            />
            <path
              d="M15 23V31C15 33.8 19 36 24 36C29 36 33 33.8 33 31V23"
              stroke="#10b981"
              stroke-width="2.2"
              stroke-linecap="round"
            />
          </g>
          <g class="tassel">
            <path d="M37 21V31" stroke="#a7f3d0" stroke-width="2" stroke-linecap="round" />
            <circle cx="37" cy="33" r="2.2" fill="#6ee7b7" />
          </g>
        </svg>

        <!-- Adult: Balanced Compass Rose & Orbiting Ring -->
        <svg
          *ngSwitchCase="'adult'"
          viewBox="0 0 48 48"
          class="stage-svg svg-adult"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <circle
            class="orbit-ring"
            cx="24"
            cy="24"
            r="15"
            stroke="#3b82f6"
            stroke-width="2.2"
            stroke-dasharray="5 4"
          />
          <circle
            cx="24"
            cy="24"
            r="11"
            fill="#3b82f6"
            fill-opacity="0.18"
            stroke="#60a5fa"
            stroke-width="1.8"
          />
          <g class="needle">
            <polygon points="24,13 28,24 24,22 20,24" fill="#93c5fd" />
            <polygon points="24,35 28,24 24,26 20,24" fill="#3b82f6" />
          </g>
          <circle cx="24" cy="24" r="2.2" fill="#f8fafc" />
        </svg>

        <!-- Business: Ascending Growth Bars & Trend Arrow -->
        <svg
          *ngSwitchCase="'business'"
          viewBox="0 0 48 48"
          class="stage-svg svg-business"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <rect class="biz-bar b1" x="10" y="27" width="6" height="11" rx="2" fill="#f59e0b" fill-opacity="0.45" stroke="#fbbf24" stroke-width="1.8" />
          <rect class="biz-bar b2" x="21" y="21" width="6" height="17" rx="2" fill="#f59e0b" fill-opacity="0.65" stroke="#fbbf24" stroke-width="1.8" />
          <rect class="biz-bar b3" x="32" y="15" width="6" height="23" rx="2" fill="#f59e0b" fill-opacity="0.85" stroke="#fde68a" stroke-width="1.8" />
          <g class="trend-arrow">
            <path d="M11 19L22 12L28 15L37 8" stroke="#fde68a" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round" />
            <path d="M31 8H37V14" stroke="#fde68a" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round" />
          </g>
        </svg>

        <!-- Veteran: Valor Shield & Radiating Star Emblem -->
        <svg
          *ngSwitchCase="'veteran'"
          viewBox="0 0 48 48"
          class="stage-svg svg-veteran"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            class="shield-aura"
            d="M24 7L37 12.5V23.5C37 31.8 31.3 37.8 24 41C16.7 37.8 11 31.8 11 23.5V12.5L24 7Z"
            fill="#ef4444"
            fill-opacity="0.22"
            stroke="#f87171"
            stroke-width="2.2"
            stroke-linejoin="round"
          />
          <polygon
            class="valor-star"
            points="24,14 26.6,19.4 32.5,20.2 28.2,24.4 29.2,30.2 24,27.4 18.8,30.2 19.8,24.4 15.5,20.2 21.4,19.4"
            fill="#fecaca"
            stroke="#fca5a5"
            stroke-width="1.4"
            stroke-linejoin="round"
          />
        </svg>
      </ng-container>
    </span>
  `,
  styles: [
    `
      .icon-frame {
        --icon-size: 42px;
        position: relative;
        width: var(--icon-size);
        height: var(--icon-size);
        display: inline-flex;
        align-items: center;
        justify-content: center;
        border-radius: 14px;
        background: rgba(15, 23, 42, 0.75);
        border: 1px solid rgba(148, 163, 184, 0.25);
        flex-shrink: 0;
        transition: transform 0.28s cubic-bezier(0.22, 1, 0.36, 1),
          box-shadow 0.28s ease,
          border-color 0.25s ease;
      }

      .icon-frame.is-active {
        transform: scale(1.07);
        border-color: currentColor;
        box-shadow: 0 0 18px rgba(255, 255, 255, 0.14);
      }

      .stage-svg {
        width: 78%;
        height: 78%;
        overflow: visible;
      }

      /* Kid Animations */
      .is-active .rocket-body,
      .icon-frame:hover .rocket-body {
        animation: rocketFloat 2s ease-in-out infinite;
      }
      .is-active .flame {
        animation: flamePulse 0.8s ease-in-out infinite;
        transform-origin: 24px 32px;
      }
      .is-active .star-a {
        animation: starTwinkle 1.7s ease-in-out infinite;
      }
      .is-active .star-b {
        animation: starTwinkle 1.7s ease-in-out 0.6s infinite;
      }

      /* Teen Equalizer Animations */
      .is-active .bar-1,
      .icon-frame:hover .bar-1 {
        animation: eqBounce 0.9s ease-in-out infinite;
        transform-origin: 19px 33px;
      }
      .is-active .bar-2,
      .icon-frame:hover .bar-2 {
        animation: eqBounce 0.9s ease-in-out 0.25s infinite;
        transform-origin: 24px 35px;
      }
      .is-active .bar-3,
      .icon-frame:hover .bar-3 {
        animation: eqBounce 0.9s ease-in-out 0.5s infinite;
        transform-origin: 29px 33px;
      }

      /* Student Mortarboard Animations */
      .is-active .cap-group,
      .icon-frame:hover .cap-group {
        animation: capLevitate 2.2s ease-in-out infinite;
      }
      .is-active .tassel {
        animation: tasselSway 2.2s ease-in-out infinite;
        transform-origin: 37px 21px;
      }

      /* Adult Compass Animations */
      .is-active .orbit-ring,
      .icon-frame:hover .orbit-ring {
        animation: orbitSpin 12s linear infinite;
        transform-origin: 24px 24px;
      }
      .is-active .needle {
        animation: compassBalance 2.6s ease-in-out infinite;
        transform-origin: 24px 24px;
      }

      /* Business Growth Animations */
      .is-active .trend-arrow,
      .icon-frame:hover .trend-arrow {
        animation: trendLift 1.8s ease-in-out infinite;
      }
      .is-active .b1 {
        animation: barPulse 1.8s ease-in-out infinite;
      }
      .is-active .b2 {
        animation: barPulse 1.8s ease-in-out 0.2s infinite;
      }
      .is-active .b3 {
        animation: barPulse 1.8s ease-in-out 0.4s infinite;
      }

      /* Veteran Shield & Star Animations */
      .is-active .valor-star,
      .icon-frame:hover .valor-star {
        animation: starGleam 2.2s ease-in-out infinite;
        transform-origin: 24px 23px;
      }
      .is-active .shield-aura {
        animation: shieldPulse 2.4s ease-in-out infinite;
      }

      @keyframes rocketFloat {
        0%, 100% { transform: translateY(0px); }
        50% { transform: translateY(-2.5px); }
      }
      @keyframes flamePulse {
        0%, 100% { transform: scaleY(0.85); opacity: 0.75; }
        50% { transform: scaleY(1.2); opacity: 1; }
      }
      @keyframes starTwinkle {
        0%, 100% { opacity: 0.35; transform: scale(0.85); }
        50% { opacity: 1; transform: scale(1.25); }
      }
      @keyframes eqBounce {
        0%, 100% { transform: scaleY(0.65); }
        50% { transform: scaleY(1.15); }
      }
      @keyframes capLevitate {
        0%, 100% { transform: translateY(0px); }
        50% { transform: translateY(-2px); }
      }
      @keyframes tasselSway {
        0%, 100% { transform: rotate(-6deg); }
        50% { transform: rotate(7deg); }
      }
      @keyframes orbitSpin {
        from { transform: rotate(0deg); }
        to { transform: rotate(360deg); }
      }
      @keyframes compassBalance {
        0%, 100% { transform: rotate(-10deg); }
        50% { transform: rotate(12deg); }
      }
      @keyframes trendLift {
        0%, 100% { transform: translate(0px, 0px); }
        50% { transform: translate(1.5px, -2px); }
      }
      @keyframes barPulse {
        0%, 100% { opacity: 0.78; }
        50% { opacity: 1; }
      }
      @keyframes starGleam {
        0%, 100% { transform: scale(0.95); filter: brightness(0.95); }
        50% { transform: scale(1.1); filter: brightness(1.25); }
      }
      @keyframes shieldPulse {
        0%, 100% { stroke-opacity: 0.75; }
        50% { stroke-opacity: 1; }
      }

      @media (prefers-reduced-motion: reduce) {
        .icon-frame,
        .stage-svg * {
          animation: none !important;
          transition: none !important;
        }
      }
    `,
  ],
})
export class PersonaStageIconComponent {
  @Input({ required: true }) personaId!: LifePersonaId;
  @Input() active = false;
  @Input() size = 42;
}
