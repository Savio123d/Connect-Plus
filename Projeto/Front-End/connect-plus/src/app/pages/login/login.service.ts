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
  idUsuario: number;
  nome: string;
  email: string;
  empresaId: number;
  usuarioEmpresaId: number;
  papel: string;
  status: string;
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

  salvarLogin(dadosUsuario: LoginResponse): void {
    localStorage.setItem('usuarioLogado', JSON.stringify(dadosUsuario));

    localStorage.setItem('idUsuario', String(dadosUsuario.idUsuario));
    localStorage.setItem('nome', dadosUsuario.nome);
    localStorage.setItem('email', dadosUsuario.email);

    localStorage.setItem('empresaId', String(dadosUsuario.empresaId));
    localStorage.setItem('idEmpresa', String(dadosUsuario.empresaId));

    localStorage.setItem('usuarioEmpresaId', String(dadosUsuario.usuarioEmpresaId));
    localStorage.setItem('idUsuarioEmpresa', String(dadosUsuario.usuarioEmpresaId));

    localStorage.setItem('papel', dadosUsuario.papel);
    localStorage.setItem('status', dadosUsuario.status);
  }

  getUsuarioLogado(): LoginResponse | null {
    const usuario = localStorage.getItem('usuarioLogado');
    return usuario ? JSON.parse(usuario) : null;
  }

  getEmpresaId(): number | null {
    const empresaId = localStorage.getItem('empresaId') || localStorage.getItem('idEmpresa');

    return empresaId ? Number(empresaId) : null;
  }

  getUsuarioEmpresaId(): number | null {
    const usuarioEmpresaId =
      localStorage.getItem('usuarioEmpresaId') || localStorage.getItem('idUsuarioEmpresa');

    return usuarioEmpresaId ? Number(usuarioEmpresaId) : null;
  }

  getIdUsuario(): number | null {
    const idUsuario = localStorage.getItem('idUsuario');
    return idUsuario ? Number(idUsuario) : null;
  }

  getPapel(): string | null {
    return localStorage.getItem('papel');
  }

  estaLogado(): boolean {
    return !!localStorage.getItem('usuarioLogado');
  }

  logout(): void {
    localStorage.clear();
  }
}
