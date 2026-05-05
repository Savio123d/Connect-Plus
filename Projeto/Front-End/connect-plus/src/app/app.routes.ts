import { Routes } from '@angular/router';
<<<<<<< HEAD
import { VisualizarTarefasComponent } from './visualizar-tarefas/visualizar-tarefas';
import { Login } from './pages/login/login';
import { Dashboard } from './pages/dashboard/dashboard';
import { CadastroEmpresa } from './pages/cadastro-empresa/cadastro-empresa';

=======
import { Loja } from './pages/loja/loja';
>>>>>>> 25faf32da0a8a3ae8f25b0584a40c695afbcc8b8
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
<<<<<<< HEAD
  }
  
    path: 'visualizar-tarefas',
    loadComponent: () =>
      import('./visualizar-tarefas/visualizar-tarefas').then(
        (module) => module.VisualizarTarefasComponent,
      ),
  },
   {
    path: 'projetos',
    loadComponent: () =>
      import('./pages/projetos/projetos').then(
        (module) => module.ProjetosComponent,
      ),
  }
=======
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
>>>>>>> 25faf32da0a8a3ae8f25b0584a40c695afbcc8b8
];
