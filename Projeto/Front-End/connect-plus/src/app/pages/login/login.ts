import { Component, ChangeDetectorRef, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
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
  private cdr = inject(ChangeDetectorRef);

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
      this.cdr.detectChanges();
      return;
    }

    this.carregando = true;
    this.cdr.detectChanges();

    const dadosLogin = this.loginForm.getRawValue();

    this.loginService.login(dadosLogin)
      .pipe(
        finalize(() => {
          this.carregando = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (resposta) => {
          if (resposta.token) {
            localStorage.setItem('token', resposta.token);
          }

          this.mensagemSucesso = 'Login realizado com sucesso!';
          this.cdr.detectChanges();

          this.router.navigate(['/dashboard']);
        },

        error: (erro) => {
          console.log('Erro no login:', erro);

          if (erro.status === 401 || erro.status === 403) {
            this.mensagemErro = 'E-mail ou senha incorretos.';
          } else if (erro.status === 0) {
            this.mensagemErro = 'Não foi possível conectar com o servidor.';
          } else {
            this.mensagemErro = 'Erro ao tentar fazer login. Tente novamente.';
          }

          this.cdr.detectChanges();
        }
      });
  }
}