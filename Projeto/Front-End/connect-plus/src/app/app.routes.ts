import { Routes } from '@angular/router';
import { Loja } from './pages/loja/loja';
export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: 'cadastro-empresa',
    loadComponent: () =>
      import('./pages/cadastro-empresa/cadastro-empresa').then(
        (module) => module.CadastroEmpresa,
      ),
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./pages/dashboard/dashboard').then(
        (module) => module.Menu,
      ),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login').then(
        (module) => module.Login,
      ),
  },
  {
        path: 'usuarios',
    loadComponent: () =>
      import('./pages/usuarios/usuarios').then(
        (module) => module.Usuarios,
      ),
  },
  {
    path: 'loja',
    loadComponent: () =>
      import('./pages/loja/loja').then(
        (module) => module.Loja,
      ),
    }
];
