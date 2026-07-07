import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export type TipoPlano = 'gratuito' | 'premium';

export interface CadastroEmpresaCompleto {
  razaoSocial: string;
  nomeFantasia: string;
  cnpj: string;
  cidade: string;
  uf: string;
  tipoPlano: TipoPlano;
  nomeAdmin: string;
  emailAdmin: string;
  senhaAdmin: string;
}

export interface CadastroEmpresaResponse {
  idEmpresa: number;
  tipoPlano: TipoPlano;
  statusAssinatura: 'ativa' | 'pendente' | 'cancelada';
  checkoutUrl?: string | null;
  mensagem: string;
}

@Injectable({
  providedIn: 'root',
})
export class EmpresaService {
  private readonly apiUrl = `${environment.apiBase}/api/empresas`;

  constructor(private http: HttpClient) {}

  cadastrarEmpresa(dados: CadastroEmpresaCompleto) {
    return this.http.post<CadastroEmpresaResponse>(this.apiUrl, dados);
  }
}