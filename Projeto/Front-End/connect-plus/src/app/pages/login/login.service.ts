import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthSessionService, UsuarioSessao } from '../../core/auth-session.service';
import { environment } from '../../../environments/environment';

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

  private readonly apiUrl = `${environment.apiBase}/api/auth/login`;

  login(dados: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.apiUrl, dados);
  }

  salvarLogin(dadosUsuario: LoginResponse): void {
    const usuario = this.normalizarUsuario(dadosUsuario);

    this.authSessionService.salvarSessao({
      token: dadosUsuario.token,
      usuario,
    });
  }

  getUsuarioLogado(): LoginResponse | null {
    const usuario = this.authSessionService.obterUsuario();
    return usuario ? { usuario } : null;
  }

  getEmpresaId(): number | null {
    return this.authSessionService.obterIdEmpresa() || null;
  }

  getUsuarioEmpresaId(): number | null {
    return this.authSessionService.obterIdUsuarioEmpresa() || null;
  }

  getIdUsuario(): number | null {
    return this.authSessionService.obterUsuario()?.idUsuario ?? null;
  }

  getPapel(): string | null {
    return this.authSessionService.obterPapel();
  }

  estaLogado(): boolean {
    return this.authSessionService.estaAutenticado();
  }

  logout(): void {
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
