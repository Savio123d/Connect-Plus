import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthSessionService } from '../../core/auth-session.service';

export type ChatStatusConexao = 'conectado' | 'conectando' | 'desconectado';

export interface ChatEventoRealtime {
  tipo: string;
  idConversa?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ChatRealtimeService {
  private readonly authSessionService = inject(AuthSessionService);
  private socket?: WebSocket;
  private readonly statusSubject = new BehaviorSubject<ChatStatusConexao>('desconectado');
  private readonly eventosSubject = new Subject<ChatEventoRealtime>();

  readonly status$ = this.statusSubject.asObservable();
  readonly eventos$ = this.eventosSubject.asObservable();

  conectar(idUsuarioEmpresa: number): void {
    const token = this.authSessionService.obterToken();
    if (!idUsuarioEmpresa || !token) {
      return;
    }

    this.desconectar();
    this.statusSubject.next('conectando');

    try {
      this.socket = new WebSocket(`${environment.wsBase}/ws/chat?usuarioEmpresaId=${idUsuarioEmpresa}&token=${encodeURIComponent(token)}`);

      this.socket.onopen = () => this.statusSubject.next('conectado');
      this.socket.onclose = () => this.statusSubject.next('desconectado');
      this.socket.onerror = () => this.statusSubject.next('desconectado');
      this.socket.onmessage = (evento) => this.processarMensagem(evento.data);
    } catch {
      this.statusSubject.next('desconectado');
    }
  }

  desconectar(): void {
    if (this.socket) {
      this.socket.close();
      this.socket = undefined;
    }

    this.statusSubject.next('desconectado');
  }

  private processarMensagem(data: string): void {
    try {
      const evento = JSON.parse(data) as ChatEventoRealtime;
      this.eventosSubject.next(evento);
    } catch {
      this.eventosSubject.next({ tipo: 'ATUALIZACAO' });
    }
  }
}
