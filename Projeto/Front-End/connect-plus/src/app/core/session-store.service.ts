import { HttpClient } from '@angular/common/http';
import { Injectable, computed, signal } from '@angular/core';
import { Observable, catchError, forkJoin, map, of, shareReplay, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  AuthSessionService,
  PapelEmpresa,
  UsuarioSessao,
} from './auth-session.service';

export interface SessionState {
  usuario: UsuarioSessao | null;
  papel: PapelEmpresa | null;
  permissoes: readonly string[];
  empresa: Record<string, unknown> | null;
  perfil: Record<string, unknown> | null;
  notificacoesNaoLidas: number;
  carregado: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class SessionStoreService {
  private readonly estadoInterno = signal<SessionState>(this.estadoDaSessao());
  private carregamento$: Observable<SessionState> | null = null;
  private chaveCarregada = '';

  readonly estado = this.estadoInterno.asReadonly();
  readonly usuario = computed(() => this.estadoInterno().usuario);
  readonly papel = computed(() => this.estadoInterno().papel);
  readonly empresa = computed(() => this.estadoInterno().empresa);
  readonly perfil = computed(() => this.estadoInterno().perfil);
  readonly notificacoesNaoLidas = computed(
    () => this.estadoInterno().notificacoesNaoLidas,
  );

  constructor(
    private readonly http: HttpClient,
    private readonly authSessionService: AuthSessionService,
  ) {}

  restaurarDaSessao(): void {
    this.estadoInterno.set(this.estadoDaSessao());
    this.carregamento$ = null;
    this.chaveCarregada = '';
  }

  precarregarEssencial(forcar = false): Observable<SessionState> {
    const usuario = this.authSessionService.obterUsuario();
    const idEmpresa = usuario?.idEmpresa;
    const idUsuarioEmpresa = usuario?.idUsuarioEmpresa;

    if (!usuario || !idEmpresa || !idUsuarioEmpresa) {
      this.invalidar();
      return of(this.estadoInterno());
    }

    const chave = `${idEmpresa}:${idUsuarioEmpresa}`;
    if (!forcar && this.carregamento$ && this.chaveCarregada === chave) {
      return this.carregamento$;
    }

    this.chaveCarregada = chave;
    this.carregamento$ = forkJoin({
      empresa: this.http
        .get<Record<string, unknown>>(
          `${environment.apiBase}/api/empresas/${idEmpresa}`,
        )
        .pipe(catchError(() => of(null))),
      perfil: this.http
        .get<Record<string, unknown>>(
          `${environment.apiBase}/api/perfil/${idUsuarioEmpresa}`,
        )
        .pipe(catchError(() => of(null))),
      notificacoesNaoLidas: this.http
        .get<number>(
          `${environment.apiBase}/api/notificacoes/usuario-empresa/${idUsuarioEmpresa}/nao-lidas/quantidade`,
        )
        .pipe(catchError(() => of(0))),
    }).pipe(
      map((dados) => ({
        ...this.estadoDaSessao(),
        ...dados,
        carregado: true,
      })),
      tap((estado) => this.estadoInterno.set(estado)),
      shareReplay({ bufferSize: 1, refCount: false }),
    );

    return this.carregamento$;
  }

  invalidar(): void {
    this.carregamento$ = null;
    this.chaveCarregada = '';
    this.estadoInterno.set({
      usuario: null,
      papel: null,
      permissoes: [],
      empresa: null,
      perfil: null,
      notificacoesNaoLidas: 0,
      carregado: false,
    });
  }

  pode(permissao: string): boolean {
    return this.estadoInterno().permissoes.includes(permissao);
  }

  private estadoDaSessao(): SessionState {
    const usuario = this.authSessionService.obterUsuario();
    const papel = this.authSessionService.obterPapel();

    return {
      usuario,
      papel,
      permissoes: this.permissoesPorPapel(papel),
      empresa: null,
      perfil: null,
      notificacoesNaoLidas: 0,
      carregado: false,
    };
  }

  private permissoesPorPapel(papel: PapelEmpresa | null): readonly string[] {
    switch (papel) {
      case 'gestor':
        return [
          'gerenciar_usuarios',
          'gerenciar_empresa',
          'gerenciar_assinatura',
          'gerenciar_equipes',
          'gerenciar_recompensas',
          'movimentar_projetos',
          'movimentar_tarefas',
        ];
      case 'colaborador':
        return [
          'movimentar_projetos',
          'movimentar_tarefas',
          'usar_chat',
          'usar_loja',
        ];
      case 'cliente':
        return ['visualizar_projetos', 'visualizar_feedbacks'];
      default:
        return [];
    }
  }
}
