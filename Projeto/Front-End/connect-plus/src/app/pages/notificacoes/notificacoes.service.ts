import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface NotificacaoDTO {
  idNotificacao: number;
  idEmpresa?: number | null;
  idUsuarioEmpresa?: number | null;
  tipo?: string | null;
  titulo?: string | null;
  mensagem?: string | null;
  lida?: boolean | null;
  dataCriacao?: string | null;
  dataLeitura?: string | null;
  criadaEm?: string | null;
}

interface QuantidadeNaoLidasResponse {
  quantidade: number;
}

@Injectable({
  providedIn: 'root',
})
export class NotificacoesService {
  private readonly apiUrl = `${environment.apiBase}/api/notificacoes`;

  constructor(private http: HttpClient) {}

  listarPorUsuarioEmpresa(idUsuarioEmpresa: number): Observable<NotificacaoDTO[]> {
    return this.http.get<NotificacaoDTO[]>(
      `${this.apiUrl}/usuario-empresa/${idUsuarioEmpresa}`,
    );
  }

  listarUltimasPorUsuarioEmpresa(idUsuarioEmpresa: number): Observable<NotificacaoDTO[]> {
    return this.http.get<NotificacaoDTO[]>(
      `${this.apiUrl}/usuario-empresa/${idUsuarioEmpresa}/ultimas`,
    );
  }

  contarNaoLidasPorUsuarioEmpresa(idUsuarioEmpresa: number): Observable<number> {
    return this.http
      .get<QuantidadeNaoLidasResponse>(
        `${this.apiUrl}/usuario-empresa/${idUsuarioEmpresa}/nao-lidas/quantidade`,
      )
      .pipe(map((resposta) => resposta.quantidade ?? 0));
  }

  marcarComoLida(idNotificacao: number): Observable<NotificacaoDTO> {
    return this.http.patch<NotificacaoDTO>(
      `${this.apiUrl}/${idNotificacao}/marcar-como-lida`,
      {},
    );
  }
}
