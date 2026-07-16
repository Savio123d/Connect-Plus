import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Projeto, ProjetoResumo, ProjetosService } from './projetos.service';

interface ProjetoCard extends ProjetoResumo {
  termoPesquisa: string;
  statusTexto: string;
  statusClasse: string;
  prazoFormatado: string;
}

@Component({
  selector: 'app-projetos',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './projetos.html',
  styleUrl: './projetos.css',
})
export class Projetos implements OnInit {
  projetos: ProjetoCard[] = [];
  projetosFiltrados: ProjetoCard[] = [];
  termoBusca = '';
  carregando = false;

  constructor(
    private projetosService: ProjetosService,
    private cdr: ChangeDetectorRef,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.carregarProjetos();
  }

  filtrarProjetos(): void {
    const termo = this.termoBusca.trim().toLowerCase();

    if (!termo) {
      this.projetosFiltrados = this.projetos;
      return;
    }

    this.projetosFiltrados = this.projetos.filter((projeto) =>
      projeto.termoPesquisa.includes(termo),
    );
  }

  carregarProjetos(): void {
    this.carregando = true;

    this.projetosService.listar().subscribe({
      next: (projetos) => {
        this.projetos = projetos.map((projeto) => this.criarCard(projeto));
        this.filtrarProjetos();
        this.carregando = false;
        this.cdr.markForCheck();
      },
      error: () => {
        alert('Erro ao carregar projetos. Confira se o backend está rodando.');
        this.carregando = false;
        this.cdr.markForCheck();
      },
    });
  }

  novoProjeto(): void {
    this.router.navigate(['/projetos/novo']);
  }

  abrirProjeto(id: number): void {
    this.router.navigate(['/projetos', id]);
  }
  trackByProjeto(_index: number, projeto: ProjetoCard): number {
    return projeto.id;
  }

  concluirProjeto(event: MouseEvent, id: number): void {
    event.stopPropagation();

    this.projetosService.concluirProjeto(id).subscribe({
      next: (projetoAtualizado) => this.atualizarCard(projetoAtualizado),
      error: () => alert('Não foi possível concluir o projeto.'),
    });
  }

  excluirProjeto(event: MouseEvent, id: number): void {
    event.stopPropagation();

    if (!confirm('Deseja excluir este projeto?')) {
      return;
    }

    this.projetosService.excluirProjeto(id).subscribe({
      next: () => {
        this.projetos = this.projetos.filter((projeto) => projeto.id !== id);
        this.filtrarProjetos();
        this.cdr.markForCheck();
      },
      error: () => alert('Não foi possível excluir o projeto.'),
    });
  }

  statusTexto(projeto: ProjetoResumo): string {
    return this.projetosService.textoStatusProjeto(projeto.status);
  }

  statusClasse(projeto: ProjetoResumo): string {
    if (projeto.status === 'concluido') {
      return 'concluido';
    }

    if (projeto.status === 'planejamento') {
      return 'planejamento';
    }

    if (projeto.status === 'cancelado') {
      return 'cancelado';
    }

    return 'andamento';
  }

  dataFormatada(data: string): string {
    return this.projetosService.formatarData(data);
  }

  private criarCard(projeto: ProjetoResumo): ProjetoCard {
    return {
      ...projeto,
      termoPesquisa: [projeto.nome, projeto.descricao, projeto.liderNome].join(' ').toLowerCase(),
      statusTexto: this.statusTexto(projeto),
      statusClasse: this.statusClasse(projeto),
      prazoFormatado: this.dataFormatada(projeto.prazo),
    };
  }

  private atualizarCard(projeto: Projeto): void {
    const card = this.criarCard({
      id: projeto.id,
      nome: projeto.nome,
      descricao: projeto.descricao,
      status: projeto.status,
      atrasado: projeto.atrasado,
      prazo: projeto.prazo,
      progresso: projeto.progresso,
      quantidadeMembros: projeto.membros.length,
      liderNome: projeto.lider.nome,
      liderIniciais: projeto.lider.iniciais,
    });

    this.projetos = this.projetos.map((projetoAtual) =>
      projetoAtual.id === card.id ? card : projetoAtual,
    );
    this.filtrarProjetos();
    this.cdr.markForCheck();
  }
}
