import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { LoginService } from './login.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private loginService = inject(LoginService);

  mostrarSenha = false;
  carregando = false;
  mensagemErro = '';
  mensagemSucesso = '';

  loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required, Validators.minLength(6)]]
  });

  get email() {
    return this.loginForm.controls.email;
  }

  get senha() {
    return this.loginForm.controls.senha;
  }

  alternarSenha(): void {
    this.mostrarSenha = !this.mostrarSenha;
  }

  onSubmit(): void {
    this.mensagemErro = '';
    this.mensagemSucesso = '';

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.carregando = true;

    const dadosLogin = this.loginForm.getRawValue();

    this.loginService.login(dadosLogin).subscribe({
      next: (resposta) => {
        this.carregando = false;

        if (resposta.token) {
          localStorage.setItem('token', resposta.token);
        }

        this.mensagemSucesso = 'Login realizado com sucesso!';

        setTimeout(() => {
          this.router.navigate(['/dashboard']);
        }, 500);
      },

      error: (erro) => {
        this.carregando = false;

        if (erro.status === 401 || erro.status === 403) {
          this.mensagemErro = 'E-mail ou senha inválidos.';
          return;
        }

        if (erro.status === 0) {
          this.mensagemErro = 'Não foi possível conectar com o servidor.';
          return;
        }

        this.mensagemErro = 'Erro ao tentar fazer login. Tente novamente.';
      }
    });
  }
}