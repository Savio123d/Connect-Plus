import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

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

  titulo = 'Connect+';
  mostrarSenha = false;
  carregando = false;
  mensagemErro = '';
  mensagemSucesso = '';

  loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required, Validators.minLength(6)]],
    lembrarMe: [false]
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

    setTimeout(() => {
      this.carregando = false;
      this.mensagemSucesso = 'Login enviado com sucesso.';
      console.log('Dados do login:', dadosLogin);

      this.router.navigate(['/dashboard']);
    }, 1000);
  }
}