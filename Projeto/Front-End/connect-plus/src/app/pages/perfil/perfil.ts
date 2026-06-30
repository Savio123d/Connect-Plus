import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';

import { AuthSessionService } from '../../core/auth-session.service';
import {
  ConquistaPerfil,
  HistoricoDesempenho,
  PerfilResponse,
  PerfilService,
  PerfilUsuario,
} from './perfil.service';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './perfil.html',
  styleUrls: ['./perfil.css'],
})
export class Perfil implements OnInit {
  carregando = false;
  mensagemErro = '';

  usuario: PerfilUsuario = this.criarPerfilVazio();
  conquistas: ConquistaPerfil[] = [];
  historico: HistoricoDesempenho[] = [];

  constructor(
    private perfilService: PerfilService,
    private authSessionService: AuthSessionService,
  ) {}

  ngOnInit(): void {
    this.carregarPerfil();
  }

  carregarPerfil(): void {
    const idUsuarioEmpresa = this.pegarIdUsuarioEmpresa();

    if (!idUsuarioEmpresa) {
      this.mensagemErro = 'Nao foi possivel identificar o usuario logado. Faca login novamente.';
      return;
    }

    this.carregando = true;
    this.mensagemErro = '';

    this.perfilService.buscarPerfil(idUsuarioEmpresa).subscribe({
      next: (resposta: PerfilResponse) => {
        this.usuario = resposta.usuario;
        this.conquistas = resposta.conquistas ?? [];
        this.historico = resposta.historico ?? [];
        this.carregando = false;
      },
      error: (erro) => {
        console.error('Erro ao buscar perfil:', erro);

        this.limparPerfil();
        this.mensagemErro = 'Nao foi possivel carregar os dados do perfil pela API.';
        this.carregando = false;
      },
    });
  }

  pegarIdUsuarioEmpresa(): number | null {
    const idUsuarioEmpresa = this.authSessionService.obterIdUsuarioEmpresa();
    return idUsuarioEmpresa > 0 ? idUsuarioEmpresa : null;
  }

  limparPerfil(): void {
    this.usuario = this.criarPerfilVazio();
    this.conquistas = [];
    this.historico = [];
  }

  progressoXp(): number {
    if (!this.usuario.xpProximoNivel || this.usuario.xpProximoNivel <= 0) {
      return 0;
    }

    return Math.min((this.usuario.xpAtual / this.usuario.xpProximoNivel) * 100, 100);
  }

  xpRestante(): number {
    return Math.max(this.usuario.xpProximoNivel - this.usuario.xpAtual, 0);
  }

  iniciaisUsuario(): string {
    if (!this.usuario.nome || !this.usuario.nome.trim()) {
      return '--';
    }

    const partesNome = this.usuario.nome.trim().split(' ');

    if (partesNome.length === 1) {
      return partesNome[0].substring(0, 2).toUpperCase();
    }

    const primeiraLetra = partesNome[0][0];
    const ultimaLetra = partesNome[partesNome.length - 1][0];

    return `${primeiraLetra}${ultimaLetra}`.toUpperCase();
  }

  classeConquista(cor: string): string {
    return `conquista-${cor || 'azul'}`;
  }

  valorOuPlaceholder(valor: string | number | undefined | null): string {
    if (valor === undefined || valor === null || valor === '') {
      return 'Nao informado';
    }

    return String(valor);
  }

  private criarPerfilVazio(): PerfilUsuario {
    return {
      idUsuario: undefined,
      idUsuarioEmpresa: undefined,
      nome: '',
      email: '',
      cargo: '',
      departamento: '',
      nivel: 0,
      xpAtual: 0,
      xpProximoNivel: 500,
    };
  }
}
