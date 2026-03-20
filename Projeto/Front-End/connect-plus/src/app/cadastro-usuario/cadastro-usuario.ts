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
    if (!nome || !email || !cargo) {
      alert('Preencha todos os campos.');
      return;
    }

    const usuario = {
      nome: nome,
      email: email,
      cargo: cargo,
    };

    console.log('Usuário cadastrado:', usuario);
    alert('Usuário cadastrado com sucesso!');
  }
}
