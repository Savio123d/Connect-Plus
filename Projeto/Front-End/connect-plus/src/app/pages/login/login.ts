import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { LoginService } from './login.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private loginService = inject(LoginService);

  titulo = 'Connect+';
  mostrarSenha = false;
  carregando = false;
  mensagemErro = '';
  mensagemSucesso = '';

  loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required, Validators.minLength(6)]],
    lembrarMe: [false],
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

    const dadosForm = this.loginForm.getRawValue();

    const dadosLogin = {
      email: dadosForm.email,
      senha: dadosForm.senha,
    };

    this.loginService.login(dadosLogin).subscribe({
      next: (resposta) => {
        this.carregando = false;

        if (!resposta.usuario) {
          this.mensagemErro = 'Login realizado, mas não foi possível carregar os dados do usuário.';
          return;
        }

        localStorage.setItem('usuarioLogado', JSON.stringify(resposta.usuario));

        if (resposta.usuario.idUsuario) {
          localStorage.setItem('idUsuario', String(resposta.usuario.idUsuario));
        }

        if (resposta.usuario.idUsuarioEmpresa) {
          localStorage.setItem('idUsuarioEmpresa', String(resposta.usuario.idUsuarioEmpresa));
        }

        if (resposta.usuario.idEmpresa) {
          localStorage.setItem('idEmpresa', String(resposta.usuario.idEmpresa));
        }

        this.mensagemSucesso = resposta.mensagem || 'Login realizado com sucesso!';

        this.router.navigate(['/dashboard']);
      },

      error: (erro) => {
        this.carregando = false;

        console.error('Erro no login:', erro);

        if (erro.status === 401 || erro.status === 403) {
          this.mensagemErro = 'E-mail ou senha incorretos.';
        } else if (erro.status === 0) {
          this.mensagemErro = 'Não foi possível conectar com o servidor.';
        } else {
          this.mensagemErro = 'Erro ao tentar fazer login. Tente novamente.';
        }
      },
    });
  }
}
