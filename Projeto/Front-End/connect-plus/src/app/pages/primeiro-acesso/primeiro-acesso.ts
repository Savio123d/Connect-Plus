import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { PrimeiroAcessoService, Usuario } from './primeiro-acesso.service'; // Importe a interface e o serviço
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-primeiro-acesso',
  standalone: true,
  templateUrl: './primeiro-acesso.html',
  styleUrls: ['./primeiro-acesso.css'],
  imports: [ReactiveFormsModule],
})
export class PrimeiroAcesso implements OnInit {

  usuarioForm = new FormGroup({
    nome: new FormControl<string>('', [Validators.required]),
    email: new FormControl<string>('', [Validators.required, Validators.email]),
    senha: new FormControl<string>('', [Validators.required, Validators.minLength(8)]),
    confirmarSenha: new FormControl<string>('', [Validators.required]),
  });

  constructor(private service: PrimeiroAcessoService) {}

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.usuarioForm.invalid) {
      alert('Por favor, preencha todos os campos corretamente.');
      return;
    }

    // Validação simples de senha
    if (this.usuarioForm.value.senha !== this.usuarioForm.value.confirmarSenha) {
      alert('As senhas não coincidem!');
      return;
    }

    this.usuarioForm = new FormGroup({
    nomeAdmin: new FormControl<string>('', [Validators.required]), // ou o nome que estava no seu HTML
    emailAcesso: new FormControl<string>('', [Validators.required, Validators.email]),
    senhaTemporaria: new FormControl<string>('', [Validators.required, Validators.minLength(8)]),
    confirmarSenha: new FormControl<string>('', [Validators.required]),
  });

  const dadosParaSalvar: Usuario = {
    nome: this.usuarioForm.value.nomeAdmin!,
    email: this.usuarioForm.value.emailAcesso!,
    senha: this.usuarioForm.value.senhaTemporaria!
  };

    this.service.cadastrarAdmin(dadosParaSalvar).subscribe({
      next: () => {
        alert('Administrador cadastrado com sucesso! ✅');

        this.usuarioForm.reset();
      },
      error: (e : HttpErrorResponse) => {
        console.error('Erro ao salvar no banco:', e);

      },
    });
  }
}
