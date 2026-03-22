import { Menu } from './../dashboard/dashboard';
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard',
  },
  {
    path: 'cadastro-empresa',
    loadComponent: () =>
      import('../cadastro-empresa/cadastro-empresa-page').then(
        (module) => module.CadastroEmpresa,
      ),
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('../dashboard/dashboard').then(
        (module) => module.Menu,
      ),
  },
];
