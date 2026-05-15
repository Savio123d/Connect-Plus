import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Empresa {
  idEmpresa?: number;
  razaoSocial: string;
  nomeFantasia: string;
  cnpj: string;
  cidade: string;
  status: string;
  uf: string;
}

export interface Usuario {
  idUsuario?: number;
  nome: string;
  email: string;
  senha?: string;
  status?: string;
}

@Injectable({
  providedIn: 'root',
})
export class EmpresaService {
  private apiUrl = '/api/empresas';
  private apiUrlusuario = '/api/usuarios';

  constructor(private http: HttpClient) {}

  salvar(empresa: Empresa): Observable<Empresa> {
    return this.http.post<Empresa>(this.apiUrl, empresa);
  }

  cadastrarAdmin(usuario: Usuario): Observable<Usuario> {
    return this.http.post<Usuario>(this.apiUrlusuario, usuario);
  }
}
