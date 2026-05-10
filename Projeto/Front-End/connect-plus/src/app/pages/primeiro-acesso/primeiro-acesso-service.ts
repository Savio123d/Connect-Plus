import { PrimeiroAcesso } from './primeiro-acesso';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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

export class PrimeiroAcessoService {
  private apiUrl = '/api/usuarios';

  constructor(private http: HttpClient) {}

 cadastrarAdmin(usuario: Usuario): Observable<Usuario> {
    return this.http.post<Usuario>(this.apiUrl, usuario);
  }
  
}
