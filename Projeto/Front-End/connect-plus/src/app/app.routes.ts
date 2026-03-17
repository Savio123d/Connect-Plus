import { Routes } from '@angular/router';
import { VisualizarTarefasComponent } from './visualizar-tarefas/visualizar-tarefas';

export const routes: Routes = [
  { path: '', redirectTo: 'visualizar-tarefas', pathMatch: 'full' },
  { path: 'visualizar-tarefas', component: VisualizarTarefasComponent },
];
