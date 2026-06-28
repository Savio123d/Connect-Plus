import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { AuthSessionService } from '../../core/auth-session.service';
import { environment } from '../../../environments/environment';

export type ProjetoStatus = 'planejamento' | 'em_andamento' | 'concluido' | 'cancelado';
export type PrioridadeProjeto = string;
export type TarefaStatus = string;
export type MarcoStatus = string;

export interface Pessoa {
  id: number;
  nome: string;
  cargo: string;
  email: string;
  iniciais: string;
  horasTrabalhadas?: number;
  descricaoHoras?: string;
  ativo?: boolean;
  selecionado?: boolean;
}

export interface TarefaProjeto {
  id: number;
  titulo: string;
  responsavel: string;
  prioridade: PrioridadeProjeto;
  status: TarefaStatus;
}

export interface MarcoProjeto {
  id: number;
  titulo: string;
  data: string;
  status: MarcoStatus;
}

export interface Projeto {
  id: number;
  nome: string;
  descricao: string;
  status: ProjetoStatus;
  atrasado?: boolean;
  prioridade: PrioridadeProjeto;
  prazo: string;
  inicio: string;
  progresso: number;
  horasTrabalhadas: number;
  horasEstimadas: number;
  lider: Pessoa;
  membros: Pessoa[];
  tarefas: TarefaProjeto[];
  marcos: MarcoProjeto[];
}

@Injectable({
  providedIn: 'root',
})
export class ProjetosService {
  private readonly apiUrl = `${environment.apiBase}/api/projetos`;

  usuariosDisponiveis: Pessoa[] = [];

  constructor(
    private http: HttpClient,
    private authSessionService: AuthSessionService,
  ) {}

  listar(): Observable<Projeto[]> {
    return this.http.get<Projeto[]>(this.apiUrl, {
      params: this.criarParamsEmpresa(),
    });
  }

  buscarPorId(id: number): Observable<Projeto> {
    return this.http.get<Projeto>(`${this.apiUrl}/${id}`, {
      params: this.criarParamsEmpresa(),
    });
  }

  carregarUsuariosDisponiveis(): Observable<Pessoa[]> {
    return this.http
      .get<Pessoa[]>(`${this.apiUrl}/usuarios-disponiveis`, {
        params: this.criarParamsEmpresa(),
      })
      .pipe(tap((usuarios) => (this.usuariosDisponiveis = usuarios)));
  }

  criarProjeto(dados: {
    nome: string;
    descricao: string;
    prazo: string;
    liderId: number;
    membrosIds: number[];
  }): Observable<Projeto> {
    return this.http.post<Projeto>(this.apiUrl, {
      ...dados,
      empresaId: this.getEmpresaId(),
    });
  }

  atualizarStatus(id: number, status: ProjetoStatus): Observable<Projeto> {
    return this.http.patch<Projeto>(`${this.apiUrl}/${id}/status`, { status });
  }

  concluirProjeto(id: number): Observable<Projeto> {
    return this.http.patch<Projeto>(`${this.apiUrl}/${id}/concluir`, {});
  }

  excluirProjeto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  adicionarMembro(projetoId: number, usuarioId: number): Observable<Projeto> {
    return this.http.post<Projeto>(`${this.apiUrl}/${projetoId}/membros`, { usuarioId });
  }

  adicionarMarco(projetoId: number, marco: Omit<MarcoProjeto, 'id'>): Observable<Projeto> {
    return this.http.post<Projeto>(`${this.apiUrl}/${projetoId}/marcos`, marco);
  }

  adicionarTarefa(projetoId: number, tarefa: Omit<TarefaProjeto, 'id'>): Observable<Projeto> {
    return this.http.post<Projeto>(`${this.apiUrl}/${projetoId}/tarefas`, tarefa);
  }

  usuariosForaDoProjeto(projeto: Projeto): Pessoa[] {
    const idsMembros = projeto.membros.map((membro) => membro.id);
    return this.usuariosDisponiveis.filter((usuario) => !idsMembros.includes(usuario.id));
  }

  formatarData(data: string): string {
    if (!data) {
      return '';
    }

    if (data.includes('/')) {
      return data;
    }

    const partes = data.split('-');

    if (partes.length !== 3) {
      return data;
    }

    const [ano, mes, dia] = partes;
    return `${dia}/${mes}/${ano}`;
  }

  textoStatusProjeto(status: ProjetoStatus): string {
    const textos: Record<ProjetoStatus, string> = {
      planejamento: 'Planejamento',
      em_andamento: 'Em Andamento',
      concluido: 'Concluido',
      cancelado: 'Cancelado',
    };

    return textos[status] ?? status;
  }

  private criarParamsEmpresa(): HttpParams {
    return new HttpParams().set('empresaId', String(this.getEmpresaId()));
  }

  private getEmpresaId(): number {
    const empresaId = this.authSessionService.obterIdEmpresa();

    if (!empresaId) {
      throw new Error('Empresa do usuario logado nao encontrada.');
    }

    return empresaId;
  }
}
