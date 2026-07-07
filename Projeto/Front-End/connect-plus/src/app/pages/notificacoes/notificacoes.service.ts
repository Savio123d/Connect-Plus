import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface NotificacaoDTO {
  idNotificacao: number;
  idUsuarioEmpresa?: number | null;
  idEmpresa?: number | null;
  tipo?: string | null;
  titulo?: string | null;
  mensagem?: string | null;
  lida?: boolean | null;
  dataCriacao?: string | null;
  dataLeitura?: string | null;
  criadaEm?: string | null;
}

@Injectable({
  providedIn: 'root',
})
export class NotificacoesService {
  private readonly api = `${environment.apiBase}/api/notificacoes`;

  constructor(private http: HttpClient) {}

  buscarUltimas(usuarioEmpresaId: number): Observable<NotificacaoDTO[]> {
    return this.http.get<NotificacaoDTO[]>(`${this.api}/usuario-empresa/${usuarioEmpresaId}/ultimas`);
  }

  contarNaoLidas(usuarioEmpresaId: number): Observable<number> {
    return this.http
      .get<{ quantidade: number }>(`${this.api}/usuario-empresa/${usuarioEmpresaId}/nao-lidas/quantidade`)
      .pipe(map((resposta) => resposta.quantidade));
  }

  marcarComoLida(idNotificacao: number): Observable<void> {
    return this.http.patch<void>(`${this.api}/${idNotificacao}/marcar-como-lida`, null);
  }
}
