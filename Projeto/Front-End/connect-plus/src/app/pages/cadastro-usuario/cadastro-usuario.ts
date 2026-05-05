import { Component } from '@angular/core';

@Component({
  selector: 'app-cadastro-usuario',
  standalone: true,
  imports: [],
  templateUrl: './cadastro-usuario.html',
  styleUrls: ['./cadastro-usuario.css'],
})
export class CadastroUsuario {
  cadastrar(nome: string, email: string, cargo: string) {
    // validação simples
    if (!nome || !email || !cargo) {
      alert('Preencha todos os campos.');
      return;
    }

    // criando objeto
    const usuario = {
      nome: nome,
      email: email,
      cargo: cargo,
    };

    // mostrando no console
    console.log('Usuário cadastrado:', usuario);

    // mensagem de sucesso
    alert('Usuário cadastrado com sucesso!');
  }
}
