import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'sobre-nos',
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
    path: 'sobre-nos',
    loadComponent: () => import('./pages/sobre-nos/sobre-nos').then((module) => module.SobreNos),
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
        path: 'perfil',
        loadComponent: () => import('./pages/perfil/perfil').then((module) => module.Perfil),
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
      {
        path: 'configuracoes',
        loadComponent: () =>
          import('./pages/configuracoes/configuracoes').then((module) => module.Configuracoes),
      },
        {
        path: 'sobre-nos',
        loadComponent: () => import('./pages/sobre-nos/sobre-nos').then((module) => module.SobreNos)
        },
        {
        path: 'suporte-interno',
        loadComponent: () => import('./pages/suporte-interno/suporte-interno.component').then((module) => module.SuporteInternoComponent)
        }
    ],
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
