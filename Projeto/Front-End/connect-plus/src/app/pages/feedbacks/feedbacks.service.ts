import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { AuthSessionService } from '../../core/auth-session.service';
import { environment } from '../../../environments/environment';

export type FeedbackClassificacao = 'POSITIVO' | 'MEDIANO' | 'NEGATIVO';
export type FeedbackFiltro = 'todos' | 'avaliacao360' | 'positivos' | 'medianos' | 'negativos';
export type AbaFeedback = 'projetos' | '360';

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
  autorEmail?: string;
  autorIniciais?: string;
  destinatarioUsuarioEmpresaId: number;
  destinatarioNome: string;
  destinatarioEmail?: string;
  destinatarioIniciais?: string;
  projetoId?: number | null;
  projetoNome?: string | null;
  tarefaId?: number | null;
  tarefaTitulo?: string | null;
  nota?: number | null;
  classificacao: FeedbackClassificacao | null;
  categoria: string;
  comentario: string;
  avaliacao360: boolean;
  assiduidade?: number | null;
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
  avaliacaoId?: number | null;
  empresaId: number;
  autorUsuarioEmpresaId: number;
  destinatarioUsuarioEmpresaId?: number | null;
  projetoId?: number | null;
  nota?: number | null;
  assiduidade: number;
  nivelEntregas: number;
  comunicacao: number;
  colaboracao: number;
  comprometimento: number;
  comentario?: string;
}

export interface Feedback360Pendente {
  avaliacaoId: number;
  rodadaId: number;
  projetoId: number;
  projetoNome: string;
  obrigatoria: boolean;
  ordem: number;
  destinatarioUsuarioEmpresaId: number;
  destinatarioNome: string;
  destinatarioIniciais: string;
  concluidoEm?: string | null;
  prazoLimite?: string | null;
  diasRestantes?: number | null;
  vencido?: boolean | null;
}

export interface Feedback360Status {
  bloqueiaSistema: boolean;
  rodadaId: number | null;
  projetoId: number | null;
  projetoNome: string | null;
  obrigatoria: boolean;
  pendentes: number;
}

export interface Feedback360UsuarioCard {
  rodadaId: number;
  projetoId: number;
  projetoNome: string;
  obrigatoria: boolean;
  concluidaPeloUsuario: boolean;
  abertaEm: string;
}

export interface Feedback360GestorItem {
  projetoId: number;
  projetoNome: string;
  avaliadoId: number;
  avaliadoNome: string;
  mediaGeral: number;
  mediaAssiduidade: number;
  mediaNivelEntregas: number;
  mediaComunicacao: number;
  mediaColaboracao: number;
  mediaComprometimento: number;
  quantidadeAvaliacoes: number;
  comentarios: string[];
  observacoesProjeto: string[];
}

export interface Feedback360ProjetoGestor {
  projetoId: number;
  projetoNome: string;
  mediaGeral: number;
  quantidadeAvaliacoes: number;
  observacoesProjeto: string[];
  avaliados: Feedback360GestorItem[];
  aberto: boolean;
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

export interface ProjetoFeedback {
  id: number;
  nome: string;
  status?: string;
  membros?: ColaboradorFeedback[];
  lider?: ColaboradorFeedback | null;
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

interface ProjetoBackend {
  id?: number;
  idProjeto?: number;
  nome?: string;
  status?: string | { valor?: string; name?: string };
  membros?: UsuarioBackend[];
  lider?: UsuarioBackend | null;
}

@Injectable({
  providedIn: 'root',
})
export class FeedbacksService {
  private readonly apiFeedbacks = `${environment.apiBase}/api/feedbacks`;
  private readonly apiUsuarios = `${environment.apiBase}/api/usuarios`;
  private readonly apiProjetos = `${environment.apiBase}/api/projetos`;

  constructor(
    private http: HttpClient,
    private authSessionService: AuthSessionService,
  ) {}

  listar(filtro: FeedbackFiltro = 'todos'): Observable<FeedbackItem[]> {
    const empresaId = this.getEmpresaId();
    const params = new HttpParams().set('filtro', filtro);

    return this.http
      .get<FeedbackItem[]>(`${this.apiFeedbacks}/empresa/${empresaId}`, { params })
      .pipe(map((feedbacks) => feedbacks.map((feedback) => this.normalizarFeedback(feedback))));
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

    return this.http.post<FeedbackItem>(this.apiFeedbacks, body).pipe(map((feedback) => this.normalizarFeedback(feedback)));
  }

  listarPendentes360(): Observable<Feedback360Pendente[]> {
    const empresaId = this.getEmpresaId();
    const params = new HttpParams().set('autorUsuarioEmpresaId', String(this.getUsuarioEmpresaId()));

    return this.http.get<Feedback360Pendente[]>(`${this.apiFeedbacks}/empresa/${empresaId}/360/pendentes`, {
      params,
    });
  }

  buscarStatus360(): Observable<Feedback360Status> {
    const params = new HttpParams()
      .set('empresaId', String(this.getEmpresaId()))
      .set('usuarioEmpresaId', String(this.getUsuarioEmpresaId()));

    return this.http.get<Feedback360Status>(`${this.apiFeedbacks}/360/status`, { params }).pipe(
      map((status) => ({
        bloqueiaSistema: Boolean(status?.bloqueiaSistema),
        rodadaId: status?.rodadaId ?? null,
        projetoId: status?.projetoId ?? null,
        projetoNome: status?.projetoNome ?? null,
        obrigatoria: Boolean(status?.obrigatoria),
        pendentes: Number(status?.pendentes ?? 0),
      })),
    );
  }

  criarAvaliacao360(
    dados: Omit<Feedback360Request, 'empresaId' | 'autorUsuarioEmpresaId'>,
  ): Observable<FeedbackItem> {
    const body: Feedback360Request = {
      ...dados,
      empresaId: this.getEmpresaId(),
      autorUsuarioEmpresaId: this.getUsuarioEmpresaId(),
      nota: dados.nota ?? this.calcularMedia360(dados),
    };

    return this.http
      .post<FeedbackItem>(`${this.apiFeedbacks}/360`, body)
      .pipe(map((feedback) => this.normalizarFeedback(feedback)));
  }

  salvarObservacaoProjeto360(rodadaId: number, observacao: string): Observable<void> {
    const body = {
      empresaId: this.getEmpresaId(),
      usuarioEmpresaId: this.getUsuarioEmpresaId(),
      observacao,
    };

    return this.http.post<void>(`${this.apiFeedbacks}/360/rodadas/${rodadaId}/observacao`, body);
  }

  listarCardsUsuario360(): Observable<Feedback360UsuarioCard[]> {
    const params = new HttpParams()
      .set('empresaId', String(this.getEmpresaId()))
      .set('usuarioEmpresaId', String(this.getUsuarioEmpresaId()));

    return this.http.get<Feedback360UsuarioCard[]>(`${this.apiFeedbacks}/360/usuario/cards`, { params });
  }

  listarResumoGestor360(): Observable<Feedback360ProjetoGestor[]> {
    const params = new HttpParams()
      .set('empresaId', String(this.getEmpresaId()))
      .set('gestorUsuarioEmpresaId', String(this.getUsuarioEmpresaId()));

    return this.http
      .get<Feedback360GestorItem[]>(`${this.apiFeedbacks}/360/gestor`, { params })
      .pipe(map((itens) => this.agruparResumoGestor(itens)));
  }

  definirObrigatoriedadeProjeto360(projetoId: number, obrigatoria: boolean): Observable<void> {
    const body = {
      empresaId: this.getEmpresaId(),
      gestorUsuarioEmpresaId: this.getUsuarioEmpresaId(),
      obrigatoria,
    };

    return this.http.patch<void>(`${this.apiFeedbacks}/360/projeto/${projetoId}/obrigatoriedade`, body);
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

  listarProjetos(): Observable<ProjetoFeedback[]> {
    const params = new HttpParams().set('empresaId', String(this.getEmpresaId()));

    return this.http.get<ProjetoBackend[]>(this.apiProjetos, { params }).pipe(
      map((projetos) =>
        projetos.map((projeto) => ({
          id: Number(projeto.id ?? projeto.idProjeto ?? 0),
          nome: projeto.nome ?? 'Projeto sem nome',
          status: typeof projeto.status === 'string'
            ? projeto.status
            : String(projeto.status?.valor ?? projeto.status?.name ?? ''),
          membros: (projeto.membros ?? []).map((membro) => this.normalizarColaborador(membro)),
          lider: projeto.lider ? this.normalizarColaborador(projeto.lider) : null,
        })).filter((projeto) => !!projeto.id),
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

  getUsuarioLogado() {
    return this.authSessionService.obterUsuario();
  }

  gerarIniciaisPublico(nome: string): string {
    return this.gerarIniciais(nome);
  }

  private normalizarFeedback(feedback: FeedbackItem): FeedbackItem {
    const autorNome = feedback.autorNome ?? 'Remetente';
    const destinatarioNome = feedback.destinatarioNome ?? 'Destinatário';
    const mediaCalculada = this.calcularMedia360({
      assiduidade: feedback.assiduidade ?? 0,
      nivelEntregas: feedback.nivelEntregas ?? 0,
      comunicacao: feedback.comunicacao ?? 0,
      colaboracao: feedback.colaboracao ?? 0,
      comprometimento: feedback.comprometimento ?? 0,
    });

    return {
      ...feedback,
      autorIniciais: feedback.autorIniciais ?? this.gerarIniciais(autorNome),
      destinatarioIniciais: feedback.destinatarioIniciais ?? this.gerarIniciais(destinatarioNome),
      media360: feedback.media360 ?? (feedback.avaliacao360 ? mediaCalculada : null),
    };
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

  private agruparResumoGestor(itens: Feedback360GestorItem[]): Feedback360ProjetoGestor[] {
    const mapa = new Map<number, Feedback360ProjetoGestor>();

    for (const item of itens ?? []) {
      const projetoId = Number(item.projetoId ?? 0);

      if (!mapa.has(projetoId)) {
        mapa.set(projetoId, {
          projetoId,
          projetoNome: item.projetoNome ?? 'Projeto sem nome',
          mediaGeral: 0,
          quantidadeAvaliacoes: 0,
          observacoesProjeto: [],
          avaliados: [],
          aberto: false,
        });
      }

      const projeto = mapa.get(projetoId)!;
      projeto.avaliados.push(item);
      projeto.quantidadeAvaliacoes += Number(item.quantidadeAvaliacoes ?? 0);
      projeto.observacoesProjeto = Array.from(
        new Set([...(projeto.observacoesProjeto ?? []), ...(item.observacoesProjeto ?? [])].filter(Boolean)),
      );
    }

    return Array.from(mapa.values()).map((projeto, index) => {
      const somaMedias = projeto.avaliados.reduce((total, avaliado) => total + Number(avaliado.mediaGeral ?? 0), 0);

      return {
        ...projeto,
        mediaGeral: projeto.avaliados.length ? somaMedias / projeto.avaliados.length : 0,
        aberto: index === 0,
      };
    });
  }

  private calcularMedia360(dados: Partial<Feedback360Request>): number {
    const notas = [
      Number(dados.assiduidade ?? 0),
      Number(dados.nivelEntregas ?? 0),
      Number(dados.comunicacao ?? 0),
      Number(dados.colaboracao ?? 0),
      Number(dados.comprometimento ?? 0),
    ].filter((nota) => nota > 0);

    if (!notas.length) {
      return 0;
    }

    const media = notas.reduce((total, nota) => total + nota, 0) / notas.length;
    return Math.round(media * 10) / 10;
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
