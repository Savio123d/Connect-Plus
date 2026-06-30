import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface PerfilUsuario {
  idUsuario?: number;
  idUsuarioEmpresa?: number;
  nome: string;
  email: string;
  cargo: string;
  departamento: string;
  nivel: number;
  xpAtual: number;
  xpProximoNivel: number;
}

export interface ConquistaPerfil {
  titulo: string;
  icone: string;
  cor: string;
}

export interface HistoricoDesempenho {
  mes: string;
  tarefasConcluidas: number;
  xpGanho: number;
}

export interface PerfilResponse {
  usuario: PerfilUsuario;
  conquistas: ConquistaPerfil[];
  historico: HistoricoDesempenho[];
}

@Injectable({
  providedIn: 'root',
})
export class PerfilService {
  private readonly apiUrl = `${environment.apiBase}/api/perfil`;

  constructor(private http: HttpClient) {}

  buscarPerfil(idUsuarioEmpresa: number): Observable<PerfilResponse> {
    return this.http.get<PerfilResponse>(`${this.apiUrl}/${idUsuarioEmpresa}`);
  }
}
