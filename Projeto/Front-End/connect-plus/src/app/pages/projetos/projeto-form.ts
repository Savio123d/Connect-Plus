import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Pessoa, ProjetosService } from './projetos.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-projeto-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './projeto-form.html',
  styleUrl: './projeto-form.css',
})
export class ProjetoForm implements OnInit {
  nome = '';
  descricao = '';
  prazo = '';
  liderId: number | null = null;
  participantes: Pessoa[] = [];
  carregandoUsuarios = false;
  enviando = false;
  mensagemErro = '';

  constructor(
    public projetosService: ProjetosService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.carregarUsuariosDaEmpresa();
  }

  criarProjeto(): void {
    this.mensagemErro = '';

    if (!this.nome || !this.descricao || !this.prazo || !this.liderId) {
      this.mensagemErro = 'Preencha o nome, descricao, lider e prazo final do projeto.';
      return;
    }

    const membrosIds = this.participantes
      .filter((participante) => participante.selecionado)
      .map((participante) => participante.id)
      .filter((id) => id !== Number(this.liderId));

    this.enviando = true;

    this.projetosService
      .criarProjeto({
        nome: this.nome,
        descricao: this.descricao,
        prazo: this.prazo,
        liderId: Number(this.liderId),
        membrosIds,
      })
      .subscribe({
        next: () => {
          this.enviando = false;
          this.router.navigate(['/projetos']);
        },
        error: (erro: HttpErrorResponse) => {
          this.enviando = false;
          this.mensagemErro = this.extrairMensagemErro(erro);
        },
      });
  }

  private carregarUsuariosDaEmpresa(): void {
    this.carregandoUsuarios = true;

    this.projetosService.carregarUsuariosDisponiveis().subscribe({
      next: (usuarios) => {
        this.carregandoUsuarios = false;
        this.participantes = usuarios.map((usuario) => ({
          ...usuario,
          selecionado: false,
        }));
      },
      error: (erro: HttpErrorResponse) => {
        this.carregandoUsuarios = false;
        this.mensagemErro = this.extrairMensagemErro(erro);
      },
    });
  }

  private extrairMensagemErro(erro: HttpErrorResponse): string {
    if (erro.status === 0) {
      return `Nao foi possivel conectar ao backend em ${environment.apiBase}.`;
    }

    if (typeof erro.error === 'object' && erro.error) {
      const corpoErro = erro.error as Record<string, unknown>;
      const mensagem = corpoErro['erro'] ?? corpoErro['detail'] ?? corpoErro['message'];

      if (mensagem) {
        return String(mensagem);
      }
    }

    return 'Nao foi possivel carregar os usuarios da empresa ou criar o projeto.';
  }
}
