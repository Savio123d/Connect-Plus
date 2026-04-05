import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Empresa {
  idEmpresa?: number;
  razaoSocial: string;
  nomeFantasia: string;
  cnpj: string;
  cidade: string;
  situacao: string;
  uf: string;
}

@Injectable({
  providedIn: 'root',
})
export class EmpresaService {
  private apiUrl = 'http://localhost:8020/api/empresas';

  constructor(private http: HttpClient) {}

  listar(): Observable<Empresa[]> {
    return this.http.get<Empresa[]>(this.apiUrl);
  }

  salvar(empresa: Empresa): Observable<Empresa> {
    return this.http.post<Empresa>(this.apiUrl, empresa);
  }

  deleta(idEmpresa: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${idEmpresa}`);
  }
}
