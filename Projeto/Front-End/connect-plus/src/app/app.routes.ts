import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard-usuario',
  },
  {
    path: 'dashboard-usuario',
    loadComponent: () =>
      import('../dashboardUsuario/dashboard-usuario').then(
        (module) => module.DashboardUsuario,
      ),
  },
  {
    path: 'dashboard-gestor',
    loadComponent: () =>
      import('../dashboardGestor/dashboard-gestor').then(
        (module) => module.DashboardGestor,
      ),
  },
];
