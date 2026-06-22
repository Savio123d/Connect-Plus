import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface UsuarioLogado {
  idUsuario?: number;
  idUsuarioEmpresa?: number;
  idEmpresa?: number;
  idSetor?: number;

  nome: string;
  email: string;
  status?: string;

  cargo?: string;
  departamento?: string;
  xp?: number;
  nivel?: number;

  avatar?: string | null;
  temaPerfil?: string | null;
  nivelAtual?: number;
}

export interface LoginResponse {
  mensagem: string;
  usuario: UsuarioLogado;
}

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8080/api/auth/login';

  login(dados: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.apiUrl, dados);
  }
}
