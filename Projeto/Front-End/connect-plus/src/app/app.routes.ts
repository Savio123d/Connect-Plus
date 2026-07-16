import { Routes } from '@angular/router';
import { authChildGuard, authGuard } from './core/auth.guard';
import { roleGuard } from './core/role.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'sobre-nos',
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login').then((module) => module.Login),
  },
  {
    path: 'cadastro-empresa',
    loadComponent: () =>
      import('./pages/cadastro-empresa/cadastro-empresa').then(
        (module) => module.CadastroEmpresa,
      ),
  },
  {
    path: 'sobre-nos',
    loadComponent: () =>
      import('./pages/sobre-nos/sobre-nos').then((module) => module.SobreNos),
  },
  {
    path: '',
    canActivate: [authGuard],
    canActivateChild: [authChildGuard],
    loadComponent: () =>
      import('./components/sidebar/sidebar').then((module) => module.Sidebar),
    children: [
      {
        path: 'dashboard',
        data: { preload: true },
        loadComponent: () =>
          import('./pages/dashboard/dashboard').then((module) => module.Menu),
      },
      {
        path: 'perfil',
        data: { preload: true },
        loadComponent: () =>
          import('./pages/perfil/perfil').then((module) => module.Perfil),
      },
      {
        path: 'usuarios',
        canActivate: [roleGuard],
        data: { papeis: ['gestor'] },
        loadComponent: () =>
          import('./pages/usuarios/usuarios').then((module) => module.Usuarios),
      },
      {
        path: 'tarefas',
        canActivate: [roleGuard],
        data: { papeis: ['gestor', 'colaborador'] },
        loadComponent: () =>
          import('./pages/tarefas/tarefas').then((module) => module.Tarefas),
      },
      {
        path: 'feedbacks',
        canActivate: [roleGuard],
        data: { papeis: ['gestor', 'colaborador'] },
        loadComponent: () =>
          import('./pages/feedbacks/feedbacks').then(
            (module) => module.Feedbacks,
          ),
      },
      {
        path: 'chat',
        canActivate: [roleGuard],
        data: { papeis: ['gestor', 'colaborador'] },
        loadComponent: () =>
          import('./pages/chat/chat').then((module) => module.Chat),
      },
      {
        path: 'projetos',
        loadComponent: () =>
          import('./pages/projetos/projetos').then(
            (module) => module.Projetos,
          ),
      },
      {
        path: 'projetos/novo',
        canActivate: [roleGuard],
        data: { papeis: ['gestor', 'colaborador'] },
        loadComponent: () =>
          import('./pages/projetos/projeto-form').then(
            (module) => module.ProjetoForm,
          ),
      },
      {
        path: 'projetos/:id',
        loadComponent: () =>
          import('./pages/projetos/projeto-detalhe').then(
            (module) => module.ProjetoDetalhe,
          ),
      },
      {
        path: 'loja',
        canActivate: [roleGuard],
        data: { papeis: ['gestor', 'colaborador'] },
        loadComponent: () =>
          import('./pages/loja/loja').then((module) => module.Loja),
      },
      {
        path: 'configuracoes',
        canActivate: [roleGuard],
        data: { papeis: ['gestor'] },
        loadComponent: () =>
          import('./pages/configuracoes/configuracoes').then(
            (module) => module.Configuracoes,
          ),
      },
      {
        path: 'suporte-interno',
        loadComponent: () =>
          import('./pages/suporte-interno/suporte-interno.component').then(
            (module) => module.SuporteInternoComponent,
          ),
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
