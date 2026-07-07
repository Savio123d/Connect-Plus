import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject, forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthSessionService } from '../../core/auth-session.service';
import { NotificacaoDTO, NotificacoesService } from './notificacoes.service';

@Injectable({
  providedIn: 'root',
})
export class NotificacoesRealtimeService {
  private readonly naoLidasSubject = new BehaviorSubject<number>(0);
  private readonly ultimasSubject = new BehaviorSubject<NotificacaoDTO[]>([]);
  private readonly toastSubject = new Subject<NotificacaoDTO>();

  private intervaloSincronizacao?: ReturnType<typeof setInterval>;
  private idUsuarioEmpresaAtual = 0;
  private primeiraSincronizacao = true;

  readonly naoLidas$ = this.naoLidasSubject.asObservable();
  readonly ultimas$ = this.ultimasSubject.asObservable();
  readonly toast$ = this.toastSubject.asObservable();

  constructor(
    private authSessionService: AuthSessionService,
    private notificacoesService: NotificacoesService,
  ) {}

  iniciar(): void {
    const idUsuarioEmpresa = this.authSessionService.obterIdUsuarioEmpresa();

    if (!idUsuarioEmpresa) {
      this.limparEstado();
      return;
    }

    if (this.idUsuarioEmpresaAtual === idUsuarioEmpresa && this.intervaloSincronizacao) {
      return;
    }

    this.parar();
    this.idUsuarioEmpresaAtual = idUsuarioEmpresa;
    this.primeiraSincronizacao = true;
    this.sincronizarComRest();
    this.intervaloSincronizacao = setInterval(() => this.sincronizarComRest(), 30000);
  }

  sincronizarComRest(): void {
    const idUsuarioEmpresa =
      this.idUsuarioEmpresaAtual || this.authSessionService.obterIdUsuarioEmpresa();

    if (!idUsuarioEmpresa) {
      this.limparEstado();
      return;
    }

    forkJoin({
      ultimas: this.notificacoesService
        .listarUltimasPorUsuarioEmpresa(idUsuarioEmpresa)
        .pipe(catchError(() => of([] as NotificacaoDTO[]))),
      naoLidas: this.notificacoesService
        .contarNaoLidasPorUsuarioEmpresa(idUsuarioEmpresa)
        .pipe(catchError(() => of(0))),
    }).subscribe(({ ultimas, naoLidas }) => {
      this.emitirToastDeNovasNotificacoes(ultimas);
      this.ultimasSubject.next(ultimas);
      this.naoLidasSubject.next(naoLidas);
      this.primeiraSincronizacao = false;
    });
  }

  marcarComoLida(notificacao: NotificacaoDTO): void {
    if (!notificacao.idNotificacao) {
      return;
    }

    this.notificacoesService.marcarComoLida(notificacao.idNotificacao).subscribe({
      next: (notificacaoAtualizada) => {
        const notificacoesAtualizadas = this.ultimasSubject.value.map((item) =>
          item.idNotificacao === notificacaoAtualizada.idNotificacao
            ? { ...item, ...notificacaoAtualizada, lida: true }
            : item,
        );

        this.ultimasSubject.next(notificacoesAtualizadas);
        this.naoLidasSubject.next(Math.max(this.naoLidasSubject.value - 1, 0));
      },
      error: () => this.sincronizarComRest(),
    });
  }

  parar(): void {
    if (this.intervaloSincronizacao) {
      clearInterval(this.intervaloSincronizacao);
      this.intervaloSincronizacao = undefined;
    }
  }

  private emitirToastDeNovasNotificacoes(notificacoes: NotificacaoDTO[]): void {
    if (this.primeiraSincronizacao) {
      return;
    }

    const idsAtuais = new Set(
      this.ultimasSubject.value.map((notificacao) => notificacao.idNotificacao),
    );
    const novaNotificacao = notificacoes.find(
      (notificacao) => !idsAtuais.has(notificacao.idNotificacao),
    );

    if (novaNotificacao) {
      this.toastSubject.next(novaNotificacao);
    }
  }

  private limparEstado(): void {
    this.parar();
    this.idUsuarioEmpresaAtual = 0;
    this.primeiraSincronizacao = true;
    this.ultimasSubject.next([]);
    this.naoLidasSubject.next(0);
  }
}
