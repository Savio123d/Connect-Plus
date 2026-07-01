import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface EmpresaConfiguracaoBackend {
  idEmpresa?: number;
  razaoSocial?: string;
  nomeFantasia?: string;
  cnpj?: string;
  cidade?: string;
  uf?: string;
  status?: string;
}

export interface AlterarSenhaRequest {
  senhaAtual: string;
  novaSenha: string;
}

@Injectable({
  providedIn: 'root',
})
export class ConfiguracoesService {
  private readonly apiEmpresas = `${environment.apiBase}/api/empresas`;
  private readonly apiUsuarios = `${environment.apiBase}/api/usuarios`;

  constructor(private http: HttpClient) {}

  buscarEmpresa(idEmpresa: number): Observable<EmpresaConfiguracaoBackend> {
    return this.http.get<EmpresaConfiguracaoBackend>(`${this.apiEmpresas}/${idEmpresa}`);
  }

  atualizarEmpresa(
    idEmpresa: number,
    empresa: EmpresaConfiguracaoBackend,
  ): Observable<EmpresaConfiguracaoBackend> {
    return this.http.put<EmpresaConfiguracaoBackend>(`${this.apiEmpresas}/${idEmpresa}`, empresa);
  }

  alterarSenha(idUsuario: number, request: AlterarSenhaRequest): Observable<void> {
    return this.http.patch<void>(`${this.apiUsuarios}/${idUsuario}/senha`, request);
  }
}