import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Sidebar } from '../../../components/sidebar/sidebar';
import { UsuarioRequest, UsuarioService } from '../usuario.service';

@Component({
  selector: 'app-usuario-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, Sidebar],
  templateUrl: './usuario-form.html',
  styleUrl: './usuario-form.css'
})
export class UsuarioForm implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private usuarioService = inject(UsuarioService);

  modoEdicao = false;
  usuarioId: number | null = null;
  tituloPagina = 'Novo Usuário';
  carregando = false;
  salvando = false;
  mensagemErro = '';
  mensagemSucesso = '';

  usuarioForm = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    status: ['ativo', [Validators.required]]
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.modoEdicao = true;
      this.usuarioId = Number(id);
      this.tituloPagina = 'Editar Usuário';
      this.carregarUsuario(this.usuarioId);
    }
  }

  get nome() {
    return this.usuarioForm.controls.nome;
  }

  get email() {
    return this.usuarioForm.controls.email;
  }

  get status() {
    return this.usuarioForm.controls.status;
  }

  carregarUsuario(id: number): void {
    this.carregando = true;
    this.mensagemErro = '';

    this.usuarioService.buscarPorId(id).subscribe({
      next: (usuario) => {
        this.usuarioForm.patchValue({
          nome: usuario.nome,
          email: usuario.email,
          status: usuario.status
        });
        this.carregando = false;
      },
      error: () => {
        this.mensagemErro = 'Não foi possível carregar o usuário.';
        this.carregando = false;
      }
    });
  }

  salvar(): void {
    this.mensagemErro = '';
    this.mensagemSucesso = '';

    if (this.usuarioForm.invalid) {
      this.usuarioForm.markAllAsTouched();
      return;
    }

    this.salvando = true;

    const dadosFormulario: UsuarioRequest = this.usuarioForm.getRawValue();

    if (this.modoEdicao && this.usuarioId !== null) {
      this.usuarioService.editar(this.usuarioId, dadosFormulario).subscribe({
        next: () => {
          this.mensagemSucesso = 'Usuário atualizado com sucesso!';
          this.salvando = false;

          setTimeout(() => {
            this.router.navigate(['/usuarios']);
          }, 800);
        },
        error: () => {
          this.mensagemErro = 'Não foi possível atualizar o usuário.';
          this.salvando = false;
        }
      });
    } else {
      this.usuarioService.criar(dadosFormulario).subscribe({
        next: () => {
          this.mensagemSucesso = 'Usuário cadastrado com sucesso!';
          this.salvando = false;

          setTimeout(() => {
            this.router.navigate(['/usuarios']);
          }, 800);
        },
        error: () => {
          this.mensagemErro = 'Não foi possível cadastrar o usuário.';
          this.salvando = false;
        }
      });
    }
  }

  voltar(): void {
    this.router.navigate(['/usuarios']);
  }
}