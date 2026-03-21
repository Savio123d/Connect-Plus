import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard-gestor',
  },
  {
    path: 'dashboard-gestor',
    loadComponent: () =>
      import('../dashboardGestor/dashboard-gestor').then(
        (module) => module.DashboardGestor,
      ),
  },
];
