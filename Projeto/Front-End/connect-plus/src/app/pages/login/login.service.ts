import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthSessionService, UsuarioSessao } from '../../core/auth-session.service';

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
  idUsuario?: number;
  nome?: string;
  email?: string;
  empresaId?: number;
  idEmpresa?: number;
  usuarioEmpresaId?: number;
  idUsuarioEmpresa?: number;
  papel?: string;
  cargo?: string;
  status?: string;
  token?: string;
  usuario?: UsuarioLogado;
}

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private http = inject(HttpClient);
  private authSessionService = inject(AuthSessionService);

  private readonly apiUrl = 'http://localhost:8080/api/auth/login';

  login(dados: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.apiUrl, dados);
  }

  salvarLogin(dadosUsuario: LoginResponse): void {
    const usuario = this.normalizarUsuario(dadosUsuario);
    const dadosNormalizados = {
      ...dadosUsuario,
      idUsuario: usuario.idUsuario,
      nome: usuario.nome,
      email: usuario.email,
      idEmpresa: usuario.idEmpresa,
      empresaId: usuario.idEmpresa,
      idUsuarioEmpresa: usuario.idUsuarioEmpresa,
      usuarioEmpresaId: usuario.idUsuarioEmpresa,
      papel: usuario.cargo ?? dadosUsuario.papel ?? '',
      cargo: usuario.cargo,
      status: usuario.status ?? '',
      usuario,
    };

    localStorage.setItem('usuarioLogado', JSON.stringify(dadosNormalizados));

    localStorage.setItem('idUsuario', String(usuario.idUsuario ?? ''));
    localStorage.setItem('nome', usuario.nome);
    localStorage.setItem('email', usuario.email);

    localStorage.setItem('empresaId', String(usuario.idEmpresa ?? ''));
    localStorage.setItem('idEmpresa', String(usuario.idEmpresa ?? ''));

    localStorage.setItem('usuarioEmpresaId', String(usuario.idUsuarioEmpresa ?? ''));
    localStorage.setItem('idUsuarioEmpresa', String(usuario.idUsuarioEmpresa ?? ''));

    localStorage.setItem('papel', usuario.cargo ?? dadosUsuario.papel ?? '');
    localStorage.setItem('status', usuario.status ?? '');

    this.authSessionService.salvarSessao({
      token: dadosUsuario.token,
      usuario,
    });
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
    this.authSessionService.limparSessao();
  }

  private normalizarUsuario(resposta: LoginResponse): UsuarioSessao {
    const usuarioResposta: Partial<UsuarioLogado> = resposta.usuario ?? {};

    return {
      idUsuario: this.lerNumero(usuarioResposta.idUsuario ?? resposta.idUsuario),
      idEmpresa: this.lerNumero(usuarioResposta.idEmpresa ?? resposta.idEmpresa ?? resposta.empresaId),
      idUsuarioEmpresa: this.lerNumero(
        usuarioResposta.idUsuarioEmpresa ?? resposta.idUsuarioEmpresa ?? resposta.usuarioEmpresaId,
      ),
      idSetor: this.lerNumero(usuarioResposta.idSetor),
      nome: String(usuarioResposta.nome ?? resposta.nome ?? ''),
      email: String(usuarioResposta.email ?? resposta.email ?? ''),
      cargo: usuarioResposta.cargo ?? resposta.cargo ?? resposta.papel,
      departamento: usuarioResposta.departamento,
      status: usuarioResposta.status ?? resposta.status,
      avatar: usuarioResposta.avatar ?? undefined,
      temaPerfil: usuarioResposta.temaPerfil ?? undefined,
    };
  }

  private lerNumero(valor: unknown): number | undefined {
    const numero = Number(valor);
    return Number.isFinite(numero) && numero > 0 ? numero : undefined;
  }
}
