import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { LoginService } from '../login/login.service';
import { environment } from '../../../environments/environment';

export type StatusUsuario = 'Ativo' | 'Inativo' | 'Pendente';

export interface Opcao {
  label: string;
  value: string;
}

export interface Usuario {
  id?: number;
  idUsuario?: number;
  idUsuarioEmpresa?: number;
  idEmpresa?: number;
  idSetor?: number;

  nome: string;
  email: string;
  cargo: string;
  departamento: string;
  status: StatusUsuario;

  senha?: string;
  xp?: number;
  nivel?: number;
}

interface UsuarioBackend {
  id?: number;
  idUsuario?: number;
  idUsuarioEmpresa?: number;
  idEmpresa?: number;
  idSetor?: number;

  nome: string;
  email: string;
  cargo?: string;
  departamento?: string;
  status?: string;

  xp?: number;
  nivel?: number;
}

@Injectable({
  providedIn: 'root',
})
export class UsuarioService {
  private readonly apiUsuarios = `${environment.apiBase}/api/usuarios`;
  private readonly apiEmpresas = `${environment.apiBase}/api/empresas`;

  constructor(
    private http: HttpClient,
    private loginService: LoginService,
  ) {}

  listar(): Observable<Usuario[]> {
    const idEmpresa = this.getEmpresaId();

    return this.http
      .get<UsuarioBackend[]>(`${this.apiUsuarios}/empresa/${idEmpresa}`)
      .pipe(map((usuarios) => usuarios.map((usuario) => this.normalizarUsuario(usuario))));
  }

  criar(usuario: Usuario): Observable<string> {
    const idEmpresa = this.getEmpresaId();

    const body = {
      nome: usuario.nome,
      email: usuario.email,
      senha: usuario.senha,
      papel: this.converterFuncaoParaBackend(usuario.cargo),

      idSetor: usuario.idSetor,
      departamento: usuario.departamento,
    };

    return this.http.post(`${this.apiEmpresas}/${idEmpresa}/usuarios`, body, {
      responseType: 'text',
    });
  }

  editar(idUsuario: number, usuario: Usuario): Observable<Usuario> {
    const body = {
      nome: usuario.nome,
      email: usuario.email,
      senha: usuario.senha,
      status: this.converterStatusParaBackend(usuario.status),
    };

    return this.http
      .put<UsuarioBackend>(`${this.apiUsuarios}/${idUsuario}`, body)
      .pipe(map((resposta) => this.normalizarUsuario(resposta)));
  }

  excluir(idUsuario: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUsuarios}/${idUsuario}`);
  }

  private getEmpresaId(): number {
    const empresaId = this.loginService.getEmpresaId();

    if (!empresaId) {
      throw new Error('Empresa do usuário logado não encontrada.');
    }

    return empresaId;
  }

  private normalizarUsuario(usuario: UsuarioBackend): Usuario {
    return {
      id: usuario.idUsuario ?? usuario.id,
      idUsuario: usuario.idUsuario ?? usuario.id,
      idUsuarioEmpresa: usuario.idUsuarioEmpresa,
      idEmpresa: usuario.idEmpresa,
      idSetor: usuario.idSetor,

      nome: usuario.nome,
      email: usuario.email,
      cargo: this.converterFuncaoParaFront(usuario.cargo),
      departamento: usuario.departamento || 'Não informado',
      status: this.converterStatusParaFront(usuario.status),

      xp: usuario.xp ?? 0,
      nivel: usuario.nivel ?? 1,
    };
  }

  private converterStatusParaFront(status?: string): StatusUsuario {
    const valor = status?.toLowerCase();

    if (valor === 'inativo') {
      return 'Inativo';
    }

    if (valor === 'pendente') {
      return 'Pendente';
    }

    return 'Ativo';
  }

  private converterStatusParaBackend(status?: string): string {
    const valor = status?.toLowerCase();

    if (valor === 'inativo') {
      return 'inativo';
    }

    return 'ativo';
  }

  private converterFuncaoParaBackend(funcao?: string): string {
    const valor = funcao?.toLowerCase() || '';

    if (valor.includes('gestor')) {
      return 'gestor';
    }

    if (valor.includes('cliente')) {
      return 'cliente';
    }

    return 'colaborador';
  }

  private converterFuncaoParaFront(funcao?: string): string {
    const valor = funcao?.toLowerCase();

    if (valor === 'gestor') {
      return 'Gestor';
    }

    if (valor === 'cliente') {
      return 'Cliente';
    }

    return 'Colaborador';
  }
}
