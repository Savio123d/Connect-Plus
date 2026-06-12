import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Usuario, UsuarioService } from './usuario.service';
import { Sidebar } from '../../components/sidebar/sidebar';

type ModalUsuario = 'criar' | 'editar' | 'excluir' | null;

interface OpcaoSelect {
  label: string;
  value: string;
}

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule, Sidebar],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css'
})
export class Usuarios implements OnInit {
  usuarios: Usuario[] = [];
  usuariosFiltrados: Usuario[] = [];

  busca = '';
  modal: ModalUsuario = null;

  carregando = false;
  erro = '';

  usuarioSelecionado: Usuario | null = null;
  formUsuario: Usuario = this.criarFormularioVazio();

  funcoes: OpcaoSelect[] = [
    { label: 'Gestor', value: 'Gestor' },
    { label: 'Colaborador', value: 'Colaborador' },
    { label: 'Cliente', value: 'Cliente' }
  ];

  setores: OpcaoSelect[] = [
    { label: 'Tecnologia', value: 'Tecnologia' },
    { label: 'Produto', value: 'Produto' },
    { label: 'Design', value: 'Design' },
    { label: 'Qualidade', value: 'Qualidade' },
    { label: 'Comercial', value: 'Comercial' }
  ];

  statusOptions: OpcaoSelect[] = [
    { label: 'Ativo', value: 'Ativo' },
    { label: 'Inativo', value: 'Inativo' }
  ];

  constructor(
    private usuarioService: UsuarioService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.carregarUsuarios();
  }

  criarFormularioVazio(): Usuario {
    return {
      nome: '',
      email: '',
      cargo: 'Colaborador',
      departamento: 'Tecnologia',
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
        this.filtrarUsuarios();
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: (erro) => {
        console.error('Erro ao carregar usuários:', erro);
        this.erro = 'Erro ao carregar usuários.';
        this.carregando = false;
        this.cdr.detectChanges();
      }
    });
  }

  filtrarUsuarios(): void {
    const texto = this.busca.toLowerCase().trim();

    if (!texto) {
      this.usuariosFiltrados = [...this.usuarios];
      return;
    }

    this.usuariosFiltrados = this.usuarios.filter((usuario) =>
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

    this.formUsuario = {
      ...usuario,
      senha: ''
    };

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
    this.formUsuario = this.criarFormularioVazio();
  }

  salvarUsuario(): void {
  if (!this.formUsuario.nome || !this.formUsuario.email) {
    this.erro = 'Preencha nome e email.';
    return;
  }

  if (this.modal === 'criar' && !this.formUsuario.senha) {
    this.erro = 'Informe uma senha temporária.';
    return;
  }

  this.carregando = true;
  this.erro = '';

  if (this.modal === 'criar') {
    this.usuarioService.criar(this.formUsuario).subscribe({
      next: () => {
        this.fecharModal();
        this.carregarUsuarios();
      },
      error: (erro: any) => {
        console.error('Erro ao criar usuário:', erro);
        this.erro = 'Erro ao criar usuário.';
        this.carregando = false;
        this.cdr.detectChanges();
      }
    });

    return;
  }

  if (this.modal === 'editar') {
    const id = this.pegarId(this.formUsuario);

    if (!id) {
      this.erro = 'Não foi possível identificar o usuário.';
      this.carregando = false;
      return;
    }

    this.usuarioService.editar(id, this.formUsuario).subscribe({
      next: () => {
        this.fecharModal();
        this.carregarUsuarios();
      },
      error: (erro: any) => {
        console.error('Erro ao editar usuário:', erro);
        this.erro = 'Erro ao editar usuário.';
        this.carregando = false;
        this.cdr.detectChanges();
      }
    });
  }
}

  confirmarExclusao(): void {
    if (!this.usuarioSelecionado) {
      return;
    }

    const id = this.pegarId(this.usuarioSelecionado);

    if (!id) {
      this.erro = 'Não foi possível identificar o usuário.';
      return;
    }

    this.carregando = true;
    this.erro = '';

    this.usuarioService.excluir(id).subscribe({
      next: () => {
        this.fecharModal();
        this.carregarUsuarios();
      },
      error: (erro) => {
        console.error('Erro ao excluir usuário:', erro);
        this.erro = 'Erro ao excluir usuário.';
        this.carregando = false;
        this.cdr.detectChanges();
      }
    });
  }

  pegarId(usuario: Usuario): number {
    return usuario.idUsuario ?? usuario.id ?? 0;
  }

  iniciais(nome: string): string {
    if (!nome) {
      return 'US';
    }

    return nome
      .split(' ')
      .filter((parte) => parte.trim() !== '')
      .map((parte) => parte.charAt(0))
      .join('')
      .substring(0, 2)
      .toUpperCase();
  }

  classeStatus(status: string): string {
    const statusNormalizado = status?.toLowerCase();

    if (statusNormalizado === 'inativo') {
      return 'inativo';
    }

    if (statusNormalizado === 'pendente') {
      return 'pendente';
    }

    return 'ativo';
  }

  tituloModal(): string {
    return this.modal === 'criar' ? 'Criar Novo Usuário' : 'Editar Usuário';
  }

  textoBotaoModal(): string {
    return this.modal === 'criar' ? 'Criar Usuário' : 'Salvar Alterações';
  }
}