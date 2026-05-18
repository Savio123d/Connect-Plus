import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Usuario, UsuarioService } from './usuario.service';
import { Sidebar } from '../../components/sidebar/sidebar';

type ModalUsuario = 'criar' | 'editar' | 'excluir' | null;

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule, Sidebar],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css'
})
export class Usuarios implements OnInit {
  usuarios: Usuario[] = [];
  busca = '';
  modal: ModalUsuario = null;

  carregando = false;
  erro = '';

  usuarioSelecionado: Usuario | null = null;
  formUsuario: Usuario = this.criarFormularioVazio();

  constructor(private usuarioService: UsuarioService) {}

  ngOnInit(): void {
    this.carregarUsuarios();
  }

  criarFormularioVazio(): Usuario {
    return {
      nome: '',
      email: '',
      cargo: '',
      departamento: '',
      senha: '',
      status: 'Ativo',
      xp: 0,
      nivel: 1
    };
  }

  carregarUsuarios(): void {
    this.carregando = true;
    this.erro = '';

    this.usuarioService.listar().subscribe({
      next: (usuarios) => {
        this.usuarios = usuarios;
        this.carregando = false;
      },
      error: () => {
        this.erro = 'Erro ao carregar usuários.';
        this.carregando = false;
      }
    });
  }

  get usuariosFiltrados(): Usuario[] {
    const texto = this.busca.toLowerCase().trim();

    if (!texto) {
      return this.usuarios;
    }

    return this.usuarios.filter(usuario =>
      usuario.nome?.toLowerCase().includes(texto) ||
      usuario.email?.toLowerCase().includes(texto) ||
      usuario.cargo?.toLowerCase().includes(texto) ||
      usuario.departamento?.toLowerCase().includes(texto) ||
      usuario.status?.toLowerCase().includes(texto)
    );
  }

  abrirCriar(): void {
    this.formUsuario = this.criarFormularioVazio();
    this.usuarioSelecionado = null;
    this.erro = '';
    this.modal = 'criar';
  }

  abrirEditar(usuario: Usuario): void {
    this.usuarioSelecionado = usuario;
    this.formUsuario = { ...usuario };
    this.erro = '';
    this.modal = 'editar';
  }

  abrirExcluir(usuario: Usuario): void {
    this.usuarioSelecionado = usuario;
    this.erro = '';
    this.modal = 'excluir';
  }

  fecharModal(): void {
    this.modal = null;
    this.usuarioSelecionado = null;
    this.erro = '';
  }

  salvarUsuario(): void {
    if (!this.formUsuario.nome || !this.formUsuario.email) {
      this.erro = 'Preencha nome e email.';
      return;
    }

    this.carregando = true;

    const id = this.pegarId(this.formUsuario);

    const requisicao = this.modal === 'criar'
      ? this.usuarioService.criar(this.formUsuario)
      : this.usuarioService.editar(id, this.formUsuario);

    requisicao.subscribe({
      next: () => {
        this.fecharModal();
        this.carregarUsuarios();
      },
      error: () => {
        this.erro = 'Erro ao salvar usuário.';
        this.carregando = false;
      }
    });
  }

  confirmarExclusao(): void {
    if (!this.usuarioSelecionado) {
      return;
    }

    this.carregando = true;

    const id = this.pegarId(this.usuarioSelecionado);

    this.usuarioService.excluir(id).subscribe({
      next: () => {
        this.fecharModal();
        this.carregarUsuarios();
      },
      error: () => {
        this.erro = 'Erro ao excluir usuário.';
        this.carregando = false;
      }
    });
  }

  pegarId(usuario: Usuario): number {
    return usuario.idUsuario ?? usuario.id ?? 0;
  }

  iniciais(nome: string): string {
    return nome
      .split(' ')
      .map(parte => parte.charAt(0))
      .join('')
      .substring(0, 2)
      .toUpperCase();
  }

  classeStatus(status: string): string {
    const classes: Record<string, string> = {
      Ativo: 'ativo',
      Inativo: 'inativo',
    };

    return classes[status] || '';
  }

  tituloModal(): string {
    return this.modal === 'criar' ? 'Criar Novo Usuário' : 'Editar Usuário';
  }

  textoBotaoModal(): string {
    return this.modal === 'criar' ? 'Criar Usuário' : 'Salvar Alterações';
  }
}