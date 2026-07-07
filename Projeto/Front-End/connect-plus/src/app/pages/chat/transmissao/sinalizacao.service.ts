import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { MensagemSinalizacao } from './transmissao.model';

export type SinalizacaoStatus = 'conectado' | 'conectando' | 'desconectado';

@Injectable({
  providedIn: 'root',
})
export class SinalizacaoService {
  private socket?: WebSocket;
  private readonly statusSubject = new BehaviorSubject<SinalizacaoStatus>('desconectado');
  private readonly eventosSubject = new Subject<MensagemSinalizacao>();

  readonly status$ = this.statusSubject.asObservable();
  readonly eventos$ = this.eventosSubject.asObservable();

  conectar(idUsuarioEmpresa: number): void {
    if (!idUsuarioEmpresa) {
      return;
    }

    this.desconectar();
    this.statusSubject.next('conectando');

    try {
      this.socket = new WebSocket(`${environment.wsBase}/sinalizacao?userId=${idUsuarioEmpresa}`);

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

  enviar(mensagem: MensagemSinalizacao): void {
    this.socket?.send(JSON.stringify(mensagem));
  }

  private processarMensagem(data: string): void {
    try {
      const mensagem = JSON.parse(data) as MensagemSinalizacao;
      this.eventosSubject.next(mensagem);
    } catch {
      console.warn('Mensagem de sinalização inválida:', data);
    }
  }
}
