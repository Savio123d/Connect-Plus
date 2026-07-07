import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject, forkJoin } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NotificacaoDTO, NotificacoesService } from './notificacoes.service';

interface NotificacaoPushDTO extends NotificacaoDTO {
  evento: 'NOVA_NOTIFICACAO' | 'NOTIFICACAO_LIDA';
}

@Injectable({
  providedIn: 'root',
})
export class NotificacoesRealtimeService {
  private socket?: WebSocket;
  private reconnectTimer?: ReturnType<typeof setTimeout>;
  private fechamentoManual = false;

  private readonly ultimasSubject = new BehaviorSubject<NotificacaoDTO[]>([]);
  private readonly naoLidasSubject = new BehaviorSubject<number>(0);
  private readonly toastSubject = new Subject<NotificacaoDTO>();

  ultimas$ = this.ultimasSubject.asObservable();
  naoLidas$ = this.naoLidasSubject.asObservable();
  toast$ = this.toastSubject.asObservable();

  constructor(private notificacoesService: NotificacoesService) {}

  iniciar(): void {
    if (
      this.socket?.readyState === WebSocket.OPEN ||
      this.socket?.readyState === WebSocket.CONNECTING
    ) {
      return;
    }

    this.fechamentoManual = false;
    this.conectar();
  }

  parar(): void {
    this.fechamentoManual = true;

    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
    }

    this.socket?.close();
  }

  sincronizarComRest(): void {
    const usuarioEmpresaId = this.getUsuarioEmpresaId();

    if (!usuarioEmpresaId) {
      return;
    }

    forkJoin({
      ultimas: this.notificacoesService.buscarUltimas(usuarioEmpresaId),
      naoLidas: this.notificacoesService.contarNaoLidas(usuarioEmpresaId),
    }).subscribe({
      next: (resposta) => {
        this.ultimasSubject.next(resposta.ultimas);
        this.naoLidasSubject.next(resposta.naoLidas);
      },
      error: (erro) => {
        console.error('Erro ao sincronizar notificações', erro);
      },
    });
  }

  marcarComoLida(notificacao: NotificacaoDTO): void {
    if (notificacao.lida) {
      return;
    }

    this.notificacoesService.marcarComoLida(notificacao.idNotificacao).subscribe({
      next: () => this.aplicarNotificacaoLida(notificacao.idNotificacao),
      error: (erro) => console.error('Erro ao marcar notificação como lida', erro),
    });
  }

  private conectar(): void {
    const usuarioEmpresaId = this.getUsuarioEmpresaId();

    if (!usuarioEmpresaId) {
      return;
    }

    const url = this.montarUrlWebSocket(usuarioEmpresaId);

    this.socket = new WebSocket(url);

    this.socket.onopen = () => {
      this.sincronizarComRest();
    };

    this.socket.onmessage = (evento) => {
      this.tratarMensagem(evento.data);
    };

    this.socket.onclose = () => {
      if (!this.fechamentoManual) {
        this.agendarReconexao();
      }
    };

    this.socket.onerror = () => {
      this.socket?.close();
    };
  }

  private tratarMensagem(data: string): void {
    try {
      const payload = JSON.parse(data) as NotificacaoPushDTO;

      if (payload.evento === 'NOVA_NOTIFICACAO') {
        this.adicionarNovaNotificacao(payload);
        return;
      }

      if (payload.evento === 'NOTIFICACAO_LIDA') {
        this.aplicarNotificacaoLida(payload.idNotificacao);
        return;
      }
    } catch (erro) {
      console.error('Mensagem inválida recebida no WebSocket de notificações', erro);
    }
  }

  private adicionarNovaNotificacao(notificacao: NotificacaoDTO): void {
    const atuais = this.ultimasSubject.value;

    const novas = [
      notificacao,
      ...atuais.filter((item) => item.idNotificacao !== notificacao.idNotificacao),
    ].slice(0, 10);

    this.ultimasSubject.next(novas);
    this.naoLidasSubject.next(this.naoLidasSubject.value + 1);
    this.toastSubject.next(notificacao);
  }

  private aplicarNotificacaoLida(idNotificacao: number): void {
    const atualizadas = this.ultimasSubject.value.map((notificacao) => {
      if (notificacao.idNotificacao !== idNotificacao) {
        return notificacao;
      }

      return {
        ...notificacao,
        lida: true,
      };
    });

    const antes = this.ultimasSubject.value.find(
      (notificacao) => notificacao.idNotificacao === idNotificacao,
    );

    this.ultimasSubject.next(atualizadas);

    if (antes && !antes.lida) {
      this.naoLidasSubject.next(Math.max(this.naoLidasSubject.value - 1, 0));
    }
  }

  private agendarReconexao(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
    }

    this.reconnectTimer = setTimeout(() => {
      this.conectar();
    }, 3000);
  }

  private montarUrlWebSocket(usuarioEmpresaId: number): string {
    const base = environment.apiBase.replace(/^http/, 'ws').replace(/\/api$/, '');

    return `${base}/ws/notificacoes?usuarioEmpresaId=${usuarioEmpresaId}`;
  }

  private getUsuarioEmpresaId(): number | null {
    const direto = localStorage.getItem('usuarioEmpresaId');

    if (direto) {
      return Number(direto);
    }

    const possiveisChaves = ['usuarioLogado', 'usuario', 'auth', 'login'];

    for (const chave of possiveisChaves) {
      const valor = localStorage.getItem(chave);

      if (!valor) {
        continue;
      }

      try {
        const objeto = JSON.parse(valor);

        if (objeto?.usuarioEmpresaId) {
          return Number(objeto.usuarioEmpresaId);
        }
      } catch {
        continue;
      }
    }

    return null;
  }
}
