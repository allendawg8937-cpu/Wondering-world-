// src/app/data-access/models/finance-goal.model.ts
export interface FinanceGoal {
  id: string;
  title: string;
  targetAmount: number;
  currentAmount: number;
  dueDate?: string;
}
