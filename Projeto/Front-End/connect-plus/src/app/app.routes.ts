import { Routes } from '@angular/router';
import { CadastroUsuario } from './cadastro-usuario/cadastro-usuario';
export const routes: Routes = [
  { path: '', redirectTo: 'cadastro', pathMatch: 'full' },
  { path: 'cadastro', component: CadastroUsuario },
];
