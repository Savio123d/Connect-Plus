import { Injectable } from '@angular/core';

export interface UsuarioSessao {
  idUsuario?: number;
  idUsuarioEmpresa?: number;
  idEmpresa?: number;
  idSetor?: number;
  nome: string;
  email: string;
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
  providedIn: 'root'
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

    localStorage.removeItem('token');
    localStorage.removeItem('chat-usuario-atual');
  }

  obterUsuario(): UsuarioSessao | null {
    const conteudo = sessionStorage.getItem(this.chaveUsuario);

    if (conteudo) {
      try {
        return JSON.parse(conteudo) as UsuarioSessao;
      } catch {
        this.limparSessao();
      }
    }

    return this.obterUsuarioDoLocalStorage();
  }

  obterIdUsuarioEmpresa(): number {
    return this.obterUsuario()?.idUsuarioEmpresa ?? this.lerNumeroLocalStorage([
      'usuarioEmpresaId',
      'idUsuarioEmpresa',
      'usuario_empresa_id',
    ]);
  }

  obterIdEmpresa(): number {
    return this.obterUsuario()?.idEmpresa ?? this.lerNumeroLocalStorage([
      'empresaId',
      'idEmpresa',
      'usuarioEmpresaIdEmpresa',
    ]);
  }

  limparSessao(): void {
    sessionStorage.removeItem(this.chaveUsuario);
    sessionStorage.removeItem(this.chaveToken);
    sessionStorage.removeItem('chat-usuario-atual');

    localStorage.removeItem('token');
    localStorage.removeItem('chat-usuario-atual');
  }

  private obterUsuarioDoLocalStorage(): UsuarioSessao | null {
    const usuarioSalvo = localStorage.getItem('usuarioLogado');

    if (!usuarioSalvo) {
      const idUsuario = this.lerNumeroLocalStorage(['idUsuario']);
      const idEmpresa = this.lerNumeroLocalStorage(['empresaId', 'idEmpresa', 'usuarioEmpresaIdEmpresa']);
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

      return {
        idUsuario: idUsuario || undefined,
        idEmpresa: idEmpresa || undefined,
        idUsuarioEmpresa: idUsuarioEmpresa || undefined,
        nome,
        email,
        cargo: localStorage.getItem('papel') ?? undefined,
        status: localStorage.getItem('status') ?? undefined,
      };
    }

    try {
      const usuario = JSON.parse(usuarioSalvo) as Record<string, unknown>;
      const usuarioInterno = this.lerObjeto(usuario['usuario']);

      return {
        idUsuario: this.lerNumero(usuarioInterno['idUsuario'] ?? usuario['idUsuario']),
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
        idSetor: this.lerNumero(usuarioInterno['idSetor'] ?? usuario['idSetor']),
        nome: String(usuarioInterno['nome'] ?? usuario['nome'] ?? ''),
        email: String(usuarioInterno['email'] ?? usuario['email'] ?? ''),
        cargo: this.lerString(usuarioInterno['cargo'] ?? usuario['papel']),
        departamento: this.lerString(usuarioInterno['departamento']),
        status: this.lerString(usuarioInterno['status'] ?? usuario['status']),
        avatar: this.lerString(usuarioInterno['avatar']),
        temaPerfil: this.lerString(usuarioInterno['temaPerfil']),
      };
    } catch {
      return null;
    }
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

  private lerNumero(valor: unknown): number {
    if (valor === null || valor === undefined || valor === '') {
      return 0;
    }

    const numero = Number(valor);
    return Number.isFinite(numero) ? numero : 0;
  }

  private lerString(valor: unknown): string | undefined {
    return typeof valor === 'string' && valor.trim() ? valor : undefined;
  }

  private lerObjeto(valor: unknown): Record<string, unknown> {
    return valor && typeof valor === 'object' ? valor as Record<string, unknown> : {};
  }
}
