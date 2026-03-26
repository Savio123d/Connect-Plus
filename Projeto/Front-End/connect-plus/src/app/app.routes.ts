import { Menu } from './../pages/dashboard/dashboard';
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: 'cadastro-empresa',
    loadComponent: () =>
      import('../pages/cadastro-empresa/cadastro-empresa').then(
        (module) => module.CadastroEmpresa,
      ),
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('../pages/dashboard/dashboard').then(
        (module) => module.Menu,
      ),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('../pages/login/login').then(
        (module) => module.Login,
      ),
  }
];
