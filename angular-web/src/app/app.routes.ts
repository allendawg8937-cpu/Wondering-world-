import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login-page.component').then(
        m => m.LoginPageComponent
      ),
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./core/layout/app-shell.component').then(
        m => m.AppShellComponent
      ),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard-page.component').then(
            m => m.DashboardPageComponent
          ),
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/profile/profile-page.component').then(
            m => m.ProfilePageComponent
          ),
      },
      {
        path: 'avatar-3d',
        loadComponent: () =>
          import(
            './features/profile/avatar-3d-customizer/avatar-3d-customizer.component'
          ).then(m => m.Avatar3dCustomizerComponent),
      },
      {
        path: 'finance/goals',
        loadComponent: () =>
          import('./features/finance/goals/goals-page.component').then(
            m => m.GoalsPageComponent
          ),
      },
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];
