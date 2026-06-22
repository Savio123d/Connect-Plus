import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ChatEvento,
  ConversaDetalhe,
  ConversaResumo,
  Mensagem,
  TipoConversa,
  UsuarioChat,
} from './chat.model';

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  private readonly apiUrl = 'http://localhost:8080/api';
  private readonly wsUrl = 'ws://localhost:8080/ws/chat';

  constructor(private http: HttpClient) {}

  listarConversas(
    idUsuarioEmpresa: number,
    tipo?: TipoConversa | '',
  ): Observable<ConversaResumo[]> {
    let params = new HttpParams();

    if (tipo) {
      params = params.set('tipo', tipo);
    }

    return this.http.get<ConversaResumo[]>(`${this.apiUrl}/conversas`, {
      headers: this.criarHeaders(idUsuarioEmpresa),
      params,
    });
  }

  listarUsuariosDaEmpresa(idEmpresa: number): Observable<UsuarioChat[]> {
    return this.http.get<UsuarioChat[]>(`${this.apiUrl}/usuarios/empresa/${idEmpresa}`);
  }

  criarConversaPrivada(
    idUsuarioEmpresa: number,
    idDestinatarioUsuarioEmpresa: number,
  ): Observable<ConversaDetalhe> {
    return this.http.post<ConversaDetalhe>(
      `${this.apiUrl}/conversas/privada`,
      {
        idDestinatarioUsuarioEmpresa,
      },
      {
        headers: this.criarHeaders(idUsuarioEmpresa),
      },
    );
  }

  detalharConversa(idUsuarioEmpresa: number, idConversa: number): Observable<ConversaDetalhe> {
    return this.http.get<ConversaDetalhe>(`${this.apiUrl}/conversas/${idConversa}`, {
      headers: this.criarHeaders(idUsuarioEmpresa),
    });
  }

  listarMensagens(idUsuarioEmpresa: number, idConversa: number): Observable<Mensagem[]> {
    return this.http.get<Mensagem[]>(`${this.apiUrl}/conversas/${idConversa}/mensagens`, {
      headers: this.criarHeaders(idUsuarioEmpresa),
    });
  }

  enviarMensagem(
    idUsuarioEmpresa: number,
    idConversa: number,
    conteudo: string,
  ): Observable<Mensagem> {
    return this.http.post<Mensagem>(
      `${this.apiUrl}/conversas/${idConversa}/mensagens`,
      {
        conteudo,
        tipo: 'texto',
      },
      {
        headers: this.criarHeaders(idUsuarioEmpresa),
      },
    );
  }

  marcarMensagemComoLida(idUsuarioEmpresa: number, idMensagem: number): Observable<Mensagem> {
    return this.http.put<Mensagem>(
      `${this.apiUrl}/mensagens/${idMensagem}/lida`,
      {},
      {
        headers: this.criarHeaders(idUsuarioEmpresa),
      },
    );
  }

  criarConversaGrupo(
    idUsuarioEmpresa: number,
    nome: string,
    idsParticipantes: number[],
  ): Observable<ConversaDetalhe> {
    return this.http.post<ConversaDetalhe>(
      `${this.apiUrl}/conversas/grupo`,
      {
        nome,
        idsParticipantes,
      },
      {
        headers: this.criarHeaders(idUsuarioEmpresa),
      },
    );
  }

  conectarWebSocket(
    idUsuarioEmpresa: number,
    onEvento: (evento: ChatEvento) => void,
    onFechar?: () => void,
  ): WebSocket {
    const socket = new WebSocket(`${this.wsUrl}?idUsuarioEmpresa=${idUsuarioEmpresa}`);

    socket.onmessage = (message) => {
      try {
        const evento = JSON.parse(message.data) as ChatEvento;
        onEvento(evento);
      } catch (erro) {
        console.error('Erro ao ler evento do chat:', erro);
      }
    };

    socket.onclose = () => {
      if (onFechar) {
        onFechar();
      }
    };

    socket.onerror = (erro) => {
      console.error('Erro no WebSocket do chat:', erro);
    };

    return socket;
  }

  private criarHeaders(idUsuarioEmpresa: number): HttpHeaders {
    return new HttpHeaders({
      'X-Usuario-Empresa-Id': String(idUsuarioEmpresa),
    });
  }
}
