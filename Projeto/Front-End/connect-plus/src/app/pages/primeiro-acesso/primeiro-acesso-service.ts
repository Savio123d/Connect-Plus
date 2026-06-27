import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Usuario {
  idUsuario?: number | null;
  nome?: string | null;
  email?: string | null;
  senha?: string | null;
  confirmarSenha?: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class PrimeiroAcessoService {
  private readonly apiUrl = 'http://localhost:8080/api/empresas/1/usuarios';

  constructor(private http: HttpClient) {}

  cadastrarAdmin(usuario: Usuario): Observable<string> {
    return this.http.post(
      this.apiUrl,
      {
        nome: usuario.nome,
        email: usuario.email,
        senha: usuario.senha,
        papel: 'gestor'
      },
      { responseType: 'text' }
    );
  }
}
