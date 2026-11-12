// src/app/data-access/models/content-report.model.ts
export type ReportTargetType = 'post' | 'comment' | 'profile';
export type ReportReason = 'harassment' | 'spam' | 'inappropriate content' | 'other';
export type ReportStatus = 'flagged_for_review' | 'approved' | 'removed';

export interface ContentReport {
  id: string;
  targetId: string;
  targetType: ReportTargetType;
  targetLabel: string;
  reason: ReportReason;
  details: string;
  status: ReportStatus;
  createdAt: string;
}
