import { Menu } from './../pages/dashboard/dashboard';
import { Routes } from '@angular/router';
import { VisualizarTarefasComponent } from './visualizar-tarefas/visualizar-tarefas';
import { Login } from './pages/login/login';
import { Dashboard } from './pages/dashboard/dashboard';
import { CadastroEmpresa } from './pages/cadastro-empresa/cadastro-empresa';

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
  
    path: 'visualizar-tarefas',
    loadComponent: () =>
      import('./visualizar-tarefas/visualizar-tarefas').then(
        (module) => module.VisualizarTarefasComponent,
      ),
  },
];
