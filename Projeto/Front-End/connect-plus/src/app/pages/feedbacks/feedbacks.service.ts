import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { AuthSessionService } from '../../core/auth-session.service';
import { environment } from '../../../environments/environment';

export type FeedbackClassificacao = 'POSITIVO' | 'MEDIANO' | 'NEGATIVO';
export type FeedbackFiltro = 'todos' | 'avaliacao360' | 'positivos' | 'medianos' | 'negativos';

export interface FeedbackResumo {
  positivos: number;
  medianos: number;
  negativos: number;
}

export interface FeedbackItem {
  idFeedback: number;
  empresaId: number;
  autorUsuarioEmpresaId: number;
  autorNome: string;
  autorIniciais: string;
  destinatarioUsuarioEmpresaId: number;
  destinatarioNome: string;
  destinatarioIniciais: string;
  projetoId?: number | null;
  projetoNome?: string | null;
  classificacao: FeedbackClassificacao;
  categoria: string;
  comentario: string;
  avaliacao360: boolean;
  comprometimento?: number | null;
  nivelEntregas?: number | null;
  colaboracao?: number | null;
  comunicacao?: number | null;
  media360?: number | null;
  dataCriacao: string;
}

export interface FeedbackRequest {
  empresaId: number;
  autorUsuarioEmpresaId: number;
  destinatarioUsuarioEmpresaId: number;
  classificacao: FeedbackClassificacao;
  categoria: string;
  comentario: string;
  avaliacao360: boolean;
  projetoId?: number | null;
  tarefaId?: number | null;
}

export interface Feedback360Request {
  empresaId: number;
  autorUsuarioEmpresaId: number;
  destinatarioUsuarioEmpresaId: number;
  projetoId: number;
  comprometimento: number;
  nivelEntregas: number;
  colaboracao: number;
  comunicacao: number;
  comentario?: string;
}

export interface Feedback360Pendente {
  projetoId: number;
  projetoNome: string;
  destinatarioUsuarioEmpresaId: number;
  destinatarioNome: string;
  destinatarioIniciais: string;
  concluidoEm: string;
  prazoLimite: string;
  diasRestantes: number;
  vencido: boolean;
}

export interface ColaboradorFeedback {
  idUsuarioEmpresa: number;
  idUsuario?: number;
  nome: string;
  email: string;
  cargo?: string;
  departamento?: string;
  iniciais: string;
}

interface UsuarioBackend {
  id?: number;
  idUsuario?: number;
  idUsuarioEmpresa?: number;
  nome?: string;
  email?: string;
  cargo?: string;
  papel?: string;
  departamento?: string;
}

@Injectable({
  providedIn: 'root',
})
export class FeedbacksService {
  private readonly apiFeedbacks = `${environment.apiBase}/api/feedbacks`;
  private readonly apiUsuarios = `${environment.apiBase}/api/usuarios`;

  constructor(
    private http: HttpClient,
    private authSessionService: AuthSessionService,
  ) {}

  listar(filtro: FeedbackFiltro = 'todos'): Observable<FeedbackItem[]> {
    const empresaId = this.getEmpresaId();
    const params = new HttpParams().set('filtro', filtro);

    return this.http.get<FeedbackItem[]>(`${this.apiFeedbacks}/empresa/${empresaId}`, { params });
  }

  buscarResumo(): Observable<FeedbackResumo> {
    const empresaId = this.getEmpresaId();

    return this.http
      .get<Partial<FeedbackResumo>>(`${this.apiFeedbacks}/empresa/${empresaId}/resumo`)
      .pipe(
        map((resumo) => ({
          positivos: Number(resumo.positivos ?? 0),
          medianos: Number(resumo.medianos ?? 0),
          negativos: Number(resumo.negativos ?? 0),
        })),
      );
  }

  criar(dados: Omit<FeedbackRequest, 'empresaId' | 'autorUsuarioEmpresaId'>): Observable<FeedbackItem> {
    const body: FeedbackRequest = {
      ...dados,
      empresaId: this.getEmpresaId(),
      autorUsuarioEmpresaId: this.getUsuarioEmpresaId(),
      avaliacao360: dados.avaliacao360 ?? false,
      projetoId: dados.projetoId ?? null,
      tarefaId: dados.tarefaId ?? null,
    };

    return this.http.post<FeedbackItem>(this.apiFeedbacks, body);
  }

  listarPendentes360(): Observable<Feedback360Pendente[]> {
    const empresaId = this.getEmpresaId();
    const params = new HttpParams().set('autorUsuarioEmpresaId', String(this.getUsuarioEmpresaId()));

    return this.http.get<Feedback360Pendente[]>(`${this.apiFeedbacks}/empresa/${empresaId}/360/pendentes`, {
      params,
    });
  }

  criarAvaliacao360(
    dados: Omit<Feedback360Request, 'empresaId' | 'autorUsuarioEmpresaId'>,
  ): Observable<FeedbackItem> {
    const body: Feedback360Request = {
      ...dados,
      empresaId: this.getEmpresaId(),
      autorUsuarioEmpresaId: this.getUsuarioEmpresaId(),
    };

    return this.http.post<FeedbackItem>(`${this.apiFeedbacks}/360`, body);
  }

  listarColaboradores(): Observable<ColaboradorFeedback[]> {
    const empresaId = this.getEmpresaId();

    return this.http
      .get<UsuarioBackend[]>(`${this.apiUsuarios}/empresa/${empresaId}`)
      .pipe(
        map((usuarios) =>
          usuarios
            .map((usuario) => this.normalizarColaborador(usuario))
            .filter((usuario) => !!usuario.idUsuarioEmpresa)
            .filter((usuario) => usuario.idUsuarioEmpresa !== this.getUsuarioEmpresaId()),
        ),
      );
  }

  getEmpresaId(): number {
    const empresaId = this.authSessionService.obterIdEmpresa();

    if (!empresaId) {
      throw new Error('Empresa do usuário logado não encontrada. Faça login novamente.');
    }

    return empresaId;
  }

  getUsuarioEmpresaId(): number {
    const usuarioEmpresaId = this.authSessionService.obterIdUsuarioEmpresa();

    if (!usuarioEmpresaId) {
      throw new Error('Usuário da empresa não encontrado. Faça login novamente.');
    }

    return usuarioEmpresaId;
  }

  private normalizarColaborador(usuario: UsuarioBackend): ColaboradorFeedback {
    const nome = usuario.nome ?? 'Usuário';

    return {
      idUsuarioEmpresa: Number(usuario.idUsuarioEmpresa ?? 0),
      idUsuario: Number(usuario.idUsuario ?? usuario.id ?? 0) || undefined,
      nome,
      email: usuario.email ?? '',
      cargo: usuario.cargo ?? usuario.papel ?? 'Colaborador',
      departamento: usuario.departamento ?? 'Não informado',
      iniciais: this.gerarIniciais(nome),
    };
  }

  private gerarIniciais(nome: string): string {
    const partes = nome.trim().split(/\s+/).filter(Boolean);

    if (partes.length === 0) {
      return 'US';
    }

    if (partes.length === 1) {
      return partes[0].substring(0, 2).toUpperCase();
    }

    return `${partes[0][0]}${partes[partes.length - 1][0]}`.toUpperCase();
  }
}
