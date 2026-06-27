import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { PrimeiroAcessoService, Usuario } from './primeiro-acesso-service';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-primeiro-acesso',
  standalone: true,
  templateUrl: './primeiro-acesso.html',
  styleUrls: ['./primeiro-acesso.css'],
  imports: [ReactiveFormsModule, RouterLink],
})
export class PrimeiroAcesso implements OnInit {
  usuarioForm = new FormGroup({
    idUsuario: new FormControl<number | null>(null),
    nome: new FormControl<string>('', [Validators.maxLength(120)]),
    email: new FormControl<string>('', [
      Validators.email,
      Validators.maxLength(150),
    ]),
    senha: new FormControl<string>('', [Validators.minLength(8)]),
    confirmarSenha: new FormControl<string>(''),
  });

  constructor(
    private service: PrimeiroAcessoService,
    private router: Router,
  ) {}

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.usuarioForm.invalid) {
      alert('Por favor, preencha todos os campos corretamente.');
      return;
    }

    const { senha, confirmarSenha } = this.usuarioForm.value;
    if (senha !== confirmarSenha) {
      alert('As senhas não coincidem!');
      return;
    }

    const dadosParaSalvar = this.usuarioForm.value as Usuario;

    this.service.cadastrarAdmin(dadosParaSalvar).subscribe({
      next: () => {
        alert('Administrador cadastrado com sucesso! ✅');
        this.usuarioForm.reset();
        this.router.navigate(['/dashboard']);
      },
      error: (e: HttpErrorResponse) => {
        console.error('Erro ao salvar no banco:', e);
        alert('Erro ao cadastrar administrador.');
      },
    });
  }
}

export default PrimeiroAcesso
