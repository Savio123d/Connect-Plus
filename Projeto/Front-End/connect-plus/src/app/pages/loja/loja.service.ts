import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';

export type CategoriaLoja = 'Beneficio' | 'Desenvolvimento' | 'Conquista';
export type IconeLoja = 'Presente' | 'Medalha' | 'Raio' | 'Coroa' | 'Estrela' | 'Trofeu';
export type CorLoja = 'Azul' | 'Roxo' | 'Laranja' | 'Rosa' | 'Verde' | 'Vermelho';

export interface ItemLoja {
  id?: number;
  idLoja?: number;
  idEmpresa?: number;
  nome: string;
  descricao: string;
  custoXp: number;
  ativa?: boolean;
  quantidadeDisponivel?: number;
  categoria?: CategoriaLoja;
  icone?: IconeLoja;
  cor?: CorLoja;
  resgatada?: boolean;
  dataCriacao?: string;
  dataAtualizacao?: string;
}

export interface ItemLojaRequest {
  idEmpresa: number;
  nome: string;
  descricao: string;
  custoXp: number;
  ativa: boolean;
  quantidadeDisponivel?: number;
  categoria?: CategoriaLoja;
  icone?: IconeLoja;
  cor?: CorLoja;
}

export interface ResgateLojaRequest {
  idEmpresa: number;
  idUsuarioEmpresa: number;
  quantidade: number;
}

interface SaldoXpResponse {
  saldoXp?: number;
  saldo?: number;
  totalXp?: number;
}

@Injectable({
  providedIn: 'root',
})
export class LojaService {
  private readonly apiLoja = `${environment.apiBase}/api/lojas`;
  private readonly apiSaldo = `${environment.apiBase}/api/saldos-xp/me`;

  constructor(private http: HttpClient) {}

  listarItens(empresaId: number, usuarioEmpresaId?: number): Observable<ItemLoja[]> {
    const usuarioParam = usuarioEmpresaId ? `&usuarioEmpresaId=${usuarioEmpresaId}` : '';

    return this.http.get<ItemLoja[]>(
      `${this.apiLoja}?empresaId=${empresaId}${usuarioParam}&somenteAtivas=false`,
    );
  }

  buscarPorId(id: number, empresaId: number, usuarioEmpresaId?: number): Observable<ItemLoja> {
    const usuarioParam = usuarioEmpresaId ? `&usuarioEmpresaId=${usuarioEmpresaId}` : '';

    return this.http.get<ItemLoja>(`${this.apiLoja}/${id}?empresaId=${empresaId}${usuarioParam}`);
  }

  criarItem(item: ItemLojaRequest): Observable<ItemLoja> {
    return this.http.post<ItemLoja>(this.apiLoja, item);
  }

  editarItem(id: number, item: ItemLojaRequest): Observable<ItemLoja> {
    return this.http.put<ItemLoja>(`${this.apiLoja}/${id}`, item);
  }

  deletarItem(id: number, empresaId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiLoja}/${id}?empresaId=${empresaId}`);
  }

  esgotarItem(id: number, empresaId: number): Observable<ItemLoja> {
    return this.http.patch<ItemLoja>(`${this.apiLoja}/${id}/esgotar?empresaId=${empresaId}`, {});
  }

  reporItem(id: number, empresaId: number, quantidade = 1): Observable<ItemLoja> {
    return this.http.patch<ItemLoja>(`${this.apiLoja}/${id}/repor/${quantidade}?empresaId=${empresaId}`, {});
  }

  resgatarItem(id: number, request: ResgateLojaRequest): Observable<ItemLoja> {
    return this.http.post<ItemLoja>(`${this.apiLoja}/${id}/resgatar`, request);
  }

  buscarSaldoXp(usuarioEmpresaId: number): Observable<number> {
    return this.http
      .get<SaldoXpResponse | number>(`${this.apiSaldo}?usuEmpId=${usuarioEmpresaId}`)
      .pipe(
        map((resposta) => {
          if (typeof resposta === 'number') {
            return resposta;
          }

          return resposta.saldoXp ?? resposta.saldo ?? resposta.totalXp ?? 0;
        }),
      );
  }
}
