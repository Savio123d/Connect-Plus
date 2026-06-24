import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then((module) => module.Login),
  },
  {
    path: 'cadastro-empresa',
    loadComponent: () =>
      import('./pages/cadastro-empresa/cadastro-empresa').then((module) => module.CadastroEmpresa),
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then((module) => module.Login),
  },
  {
    path: '',
    loadComponent: () => import('./components/sidebar/sidebar').then((module) => module.Sidebar),
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./pages/dashboard/dashboard').then((module) => module.Menu),
      },
      {
        path: 'usuarios',
        loadComponent: () => import('./pages/usuarios/usuarios').then((module) => module.Usuarios),
      },
      {
        path: 'tarefas',
        loadComponent: () => import('./pages/tarefas/tarefas').then((module) => module.Tarefas),
      },
      {
        path: 'chat',
        loadComponent: () => import('./pages/chat/chat').then((module) => module.Chat),
      },
        {
       path: 'projetos',
       loadComponent: () =>
        import('./pages/projetos/projetos').then((module) => module.Projetos),
      },
      {
      path: 'projetos/novo',
      loadComponent: () =>
      import('./pages/projetos/projeto-form').then((module) => module.ProjetoForm),
     },
     {
     path: 'projetos/:id',
     loadComponent: () =>
     import('./pages/projetos/projeto-detalhe').then((module) => module.ProjetoDetalhe),
      },
      {
        path: 'loja',
        loadComponent: () => import('./pages/loja/loja').then((module) => module.Loja),
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
