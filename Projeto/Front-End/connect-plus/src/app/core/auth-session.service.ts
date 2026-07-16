import { Injectable } from '@angular/core';

export type PapelEmpresa = 'gestor' | 'colaborador' | 'cliente';

export interface UsuarioSessao {
  idUsuario?: number;
  idUsuarioEmpresa?: number;
  idEmpresa?: number;
  idSetor?: number;
  nome: string;
  email: string;
  papel?: PapelEmpresa;
  cargo?: string;
  departamento?: string;
  status?: string;
  avatar?: string;
  temaPerfil?: string;
}

export interface SessaoAutenticada {
  token?: string;
  usuario?: UsuarioSessao;
}

@Injectable({
  providedIn: 'root',
})
export class AuthSessionService {
  private readonly chaveUsuario = 'connect-plus.usuario';
  private readonly chaveToken = 'connect-plus.token';

  salvarSessao(sessao: SessaoAutenticada): void {
    if (sessao.token) {
      sessionStorage.setItem(this.chaveToken, sessao.token);
    } else {
      sessionStorage.removeItem(this.chaveToken);
    }

    if (sessao.usuario) {
      sessionStorage.setItem(this.chaveUsuario, JSON.stringify(sessao.usuario));
    } else {
      sessionStorage.removeItem(this.chaveUsuario);
    }

    this.limparDadosLegados();
  }

  obterToken(): string | null {
    return sessionStorage.getItem(this.chaveToken) ?? localStorage.getItem('token');
  }

  obterUsuario(): UsuarioSessao | null {
    const conteudo = sessionStorage.getItem(this.chaveUsuario);

    if (conteudo) {
      try {
        return JSON.parse(conteudo) as UsuarioSessao;
      } catch {
        this.limparSessao();
        return null;
      }
    }

    return this.obterUsuarioDoLocalStorage();
  }

  obterPapel(): PapelEmpresa | null {
    const usuario = this.obterUsuario();
    return this.normalizarPapel(
      usuario?.papel ?? usuario?.cargo ?? localStorage.getItem('papel'),
    );
  }

  obterIdUsuarioEmpresa(): number {
    return (
      this.obterUsuario()?.idUsuarioEmpresa ??
      this.lerNumeroLocalStorage([
        'usuarioEmpresaId',
        'idUsuarioEmpresa',
        'usuario_empresa_id',
      ])
    );
  }

  obterIdEmpresa(): number {
    return (
      this.obterUsuario()?.idEmpresa ??
      this.lerNumeroLocalStorage([
        'empresaId',
        'idEmpresa',
        'usuarioEmpresaIdEmpresa',
      ])
    );
  }

  estaAutenticado(): boolean {
    const token = this.obterToken();
    const usuario = this.obterUsuario();

    return Boolean(
      token?.trim() &&
        usuario?.idUsuario &&
        usuario.idEmpresa &&
        usuario.idUsuarioEmpresa,
    );
  }

  temAlgumPapel(papeis: readonly PapelEmpresa[]): boolean {
    const papelAtual = this.obterPapel();
    return papelAtual !== null && papeis.includes(papelAtual);
  }

  limparSessao(): void {
    sessionStorage.removeItem(this.chaveUsuario);
    sessionStorage.removeItem(this.chaveToken);
    sessionStorage.removeItem('chat-usuario-atual');

    this.limparDadosLegados();
  }

  private limparDadosLegados(): void {
    [
      'token',
      'chat-usuario-atual',
      'usuarioLogado',
      'idUsuario',
      'nome',
      'email',
      'empresaId',
      'idEmpresa',
      'usuarioEmpresaId',
      'idUsuarioEmpresa',
      'usuario_empresa_id',
      'usuarioEmpresaIdEmpresa',
      'idSetor',
      'cargo',
      'departamento',
      'papel',
      'status',
    ].forEach((chave) => localStorage.removeItem(chave));
  }

  private obterUsuarioDoLocalStorage(): UsuarioSessao | null {
    const usuarioSalvo = localStorage.getItem('usuarioLogado');

    if (!usuarioSalvo) {
      const idUsuario = this.lerNumeroLocalStorage(['idUsuario']);
      const idEmpresa = this.lerNumeroLocalStorage([
        'empresaId',
        'idEmpresa',
        'usuarioEmpresaIdEmpresa',
      ]);
      const idUsuarioEmpresa = this.lerNumeroLocalStorage([
        'usuarioEmpresaId',
        'idUsuarioEmpresa',
        'usuario_empresa_id',
      ]);
      const nome = localStorage.getItem('nome') ?? '';
      const email = localStorage.getItem('email') ?? '';

      if (!idUsuario && !idEmpresa && !idUsuarioEmpresa && !nome && !email) {
        return null;
      }

      const papel = this.normalizarPapel(localStorage.getItem('papel'));
      return {
        idUsuario: idUsuario || undefined,
        idEmpresa: idEmpresa || undefined,
        idUsuarioEmpresa: idUsuarioEmpresa || undefined,
        nome,
        email,
        papel: papel ?? undefined,
        cargo: papel ?? undefined,
        status: localStorage.getItem('status') ?? undefined,
      };
    }

    try {
      const usuario = JSON.parse(usuarioSalvo) as Record<string, unknown>;
      const usuarioInterno = this.lerObjeto(usuario['usuario']);
      const papel = this.normalizarPapel(
        usuarioInterno['papel'] ??
          usuarioInterno['cargo'] ??
          usuario['papel'] ??
          usuario['cargo'],
      );

      return {
        idUsuario: this.lerNumero(
          usuarioInterno['idUsuario'] ?? usuario['idUsuario'],
        ),
        idEmpresa: this.lerNumero(
          usuarioInterno['idEmpresa'] ??
            usuarioInterno['empresaId'] ??
            usuario['idEmpresa'] ??
            usuario['empresaId'],
        ),
        idUsuarioEmpresa: this.lerNumero(
          usuarioInterno['idUsuarioEmpresa'] ??
            usuarioInterno['usuarioEmpresaId'] ??
            usuario['idUsuarioEmpresa'] ??
            usuario['usuarioEmpresaId'],
        ),
        idSetor: this.lerNumero(
          usuarioInterno['idSetor'] ?? usuario['idSetor'],
        ),
        nome: String(usuarioInterno['nome'] ?? usuario['nome'] ?? ''),
        email: String(usuarioInterno['email'] ?? usuario['email'] ?? ''),
        papel: papel ?? undefined,
        cargo: papel ?? this.lerString(usuarioInterno['cargo'] ?? usuario['cargo']),
        departamento: this.lerString(usuarioInterno['departamento']),
        status: this.lerString(
          usuarioInterno['status'] ?? usuario['status'],
        ),
        avatar: this.lerString(usuarioInterno['avatar']),
        temaPerfil: this.lerString(usuarioInterno['temaPerfil']),
      };
    } catch {
      return null;
    }
  }

  private normalizarPapel(valor: unknown): PapelEmpresa | null {
    if (typeof valor !== 'string') {
      return null;
    }

    const papel = valor.trim().toLowerCase();
    return papel === 'gestor' || papel === 'colaborador' || papel === 'cliente'
      ? papel
      : null;
  }

  private lerNumeroLocalStorage(chaves: string[]): number {
    for (const chave of chaves) {
      const valor = this.lerNumero(localStorage.getItem(chave));

      if (valor) {
        return valor;
      }
    }

    return 0;
  }

  private lerNumero(valor: unknown): number | undefined {
    if (valor === null || valor === undefined || valor === '') {
      return undefined;
    }

    const numero = Number(valor);
    return Number.isFinite(numero) && numero > 0 ? numero : undefined;
  }

  private lerString(valor: unknown): string | undefined {
    return typeof valor === 'string' && valor.trim() ? valor : undefined;
  }

  private lerObjeto(valor: unknown): Record<string, unknown> {
    return valor && typeof valor === 'object'
      ? (valor as Record<string, unknown>)
      : {};
  }
}
