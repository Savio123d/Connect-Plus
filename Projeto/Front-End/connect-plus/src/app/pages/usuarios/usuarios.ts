import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Sidebar } from '../../components/sidebar/sidebar';
import { Usuario, UsuarioService } from '../../services/usuario.service';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, Sidebar],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css'
})
export class Usuarios implements OnInit {
  private router = inject(Router);
  private usuarioService = inject(UsuarioService);

  carregando = false;
  erro = '';
  usuarios: Usuario[] = [];
  usuarioSelecionadoId: number | null = null;

  ngOnInit(): void {
    this.carregarUsuarios();
  }

  carregarUsuarios(): void {
    this.carregando = true;
    this.erro = '';

    this.usuarioService.listar().subscribe({
      next: (resposta) => {
        this.usuarios = resposta;
        this.carregando = false;
      },
      error: (erroResposta) => {
        console.error('Erro ao carregar usuários:', erroResposta);
        this.erro = 'Não foi possível carregar os usuários.';
        this.carregando = false;
      }
    });
  }

  selecionarUsuario(id: number): void {
    this.usuarioSelecionadoId = id;
  }

  novoUsuario(): void {
    this.router.navigate(['/usuarios/novo']);
  }

  editarUsuario(): void {
    if (this.usuarioSelecionadoId === null) {
      return;
    }

    this.router.navigate(['/usuarios/editar', this.usuarioSelecionadoId]);
  }
}