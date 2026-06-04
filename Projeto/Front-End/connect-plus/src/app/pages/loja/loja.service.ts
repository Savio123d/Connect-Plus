import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export type CategoriaLoja = 'Benefício' | 'Desenvolvimento' | 'Conquista';
export type IconeLoja = 'Presente' | 'Medalha' | 'Raio' | 'Coroa' | 'Estrela' | 'Troféu';
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
  incluido?: string;
  excluido?: string;
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

interface SaldoXpResponse {
  saldoXp?: number;
  saldo?: number;
  totalXp?: number;
}

@Injectable({
  providedIn: 'root',
})
export class LojaService {
  private readonly apiLoja = 'http://localhost:8080/api/lojas';
  private readonly apiSaldo = 'http://localhost:8080/api/saldo-xp/me';

  constructor(private http: HttpClient) {}

  listarItens(empresaId = 1): Observable<ItemLoja[]> {
    return this.http.get<ItemLoja[]>(`${this.apiLoja}?empresaId=${empresaId}`);
  }

  listarTodos(): Observable<ItemLoja[]> {
    return this.http.get<ItemLoja[]>(`${this.apiLoja}?somenteAtivas=false`);
  }

  buscarPorId(id: number): Observable<ItemLoja> {
    return this.http.get<ItemLoja>(`${this.apiLoja}/${id}`);
  }

  criarItem(item: ItemLojaRequest): Observable<ItemLoja> {
    return this.http.post<ItemLoja>(this.apiLoja, item);
  }

  editarItem(id: number, item: ItemLojaRequest): Observable<ItemLoja> {
    return this.http.put<ItemLoja>(`${this.apiLoja}/${id}`, item);
  }

  deletarItem(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiLoja}/${id}`);
  }

  esgotarItem(id: number): Observable<ItemLoja> {
    return this.http.patch<ItemLoja>(`${this.apiLoja}/${id}/esgotar`, {});
  }

  reporItem(id: number, quantidade = 1): Observable<ItemLoja> {
    return this.http.patch<ItemLoja>(`${this.apiLoja}/${id}/repor/${quantidade}`, {});
  }

  resgatarItem(id: number): Observable<ItemLoja> {
    return this.http.post<ItemLoja>(`${this.apiLoja}/${id}/resgatar`, {});
  }

  buscarSaldoXp(usuarioEmpresaId = 1): Observable<number> {
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
