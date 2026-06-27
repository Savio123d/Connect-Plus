import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface CadastroEmpresaCompleto {
  razaoSocial: string;
  nomeFantasia: string;
  cnpj: string;
  cidade: string;
  uf: string;
  nomeAdmin: string;
  emailAdmin: string;
  senhaAdmin: string;
}

@Injectable({
  providedIn: 'root',
})
export class EmpresaService {
  private readonly apiUrl = 'http://localhost:8080/api/empresas';

  constructor(private http: HttpClient) {}

  cadastrarEmpresa(dados: CadastroEmpresaCompleto) {
    return this.http.post(this.apiUrl, dados, {
      responseType: 'text' as const,
    });
  }
}
