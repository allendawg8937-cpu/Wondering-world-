// src/app/data-access/services/content-reporting.service.ts
import { computed, inject, Injectable, signal } from '@angular/core';
import { LoadingService } from '../../core/loading/loading.service';
import {
  ContentReport,
  ReportReason,
  ReportStatus,
  ReportTargetType,
} from '../models/content-report.model';

@Injectable({
  providedIn: 'root',
})
export class ContentReportingService {
  private readonly loadingService = inject(LoadingService);

  readonly reasonOptions: ReportReason[] = [
    'harassment',
    'inappropriate content',
    'spam',
    'other',
  ];

  private readonly reportsSignal = signal<ContentReport[]>([
    {
      id: 'rep_101',
      targetId: 'post_promo_92',
      targetType: 'post',
      targetLabel: 'Guaranteed 10x crypto return promo link',
      reason: 'spam',
      details: 'Soliciting external links and private phone numbers.',
      status: 'flagged_for_review',
      createdAt: 'Just now',
    },
  ]);

  readonly reports = computed(() => this.reportsSignal());
  readonly pendingAdminReviewCount = computed(
    () => this.reportsSignal().filter(r => r.status === 'flagged_for_review').length
  );

  async submitReport(
    targetId: string,
    targetType: ReportTargetType,
    targetLabel: string,
    reason: ReportReason,
    details = ''
  ): Promise<ContentReport> {
    this.loadingService.show(`Flagging ${targetType} for administrator review...`);
    try {
      await new Promise(resolve => setTimeout(resolve, 250));
      const newReport: ContentReport = {
        id: `rep_${Date.now()}`,
        targetId,
        targetType,
        targetLabel,
        reason,
        details: details.trim() || `Reported ${targetType} for ${reason}`,
        status: 'flagged_for_review',
        createdAt: new Date().toLocaleTimeString(),
      };
      this.reportsSignal.update(current => [newReport, ...current]);
      return newReport;
    } finally {
      this.loadingService.hide();
    }
  }

  resolveReport(reportId: string, status: ReportStatus): void {
    this.reportsSignal.update(current =>
      current.map(item => (item.id === reportId ? { ...item, status } : item))
    );
  }
}
