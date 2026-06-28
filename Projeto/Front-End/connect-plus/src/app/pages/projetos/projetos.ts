import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Projeto, ProjetosService } from './projetos.service';

@Component({
  selector: 'app-projetos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './projetos.html',
  styleUrl: './projetos.css',
})
export class Projetos implements OnInit {
  projetos: Projeto[] = [];
  termoBusca = '';
  carregando = false;

  constructor(
    private projetosService: ProjetosService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.carregarProjetos();
  }

  get projetosFiltrados(): Projeto[] {
    const termo = this.termoBusca.trim().toLowerCase();

    if (!termo) {
      return this.projetos;
    }

    return this.projetos.filter(
      (projeto) =>
        projeto.nome.toLowerCase().includes(termo) ||
        projeto.descricao.toLowerCase().includes(termo) ||
        projeto.lider.nome.toLowerCase().includes(termo),
    );
  }

  carregarProjetos(): void {
    this.carregando = true;

    this.projetosService.listar().subscribe({
      next: (projetos) => {
        this.projetos = projetos;
        this.carregando = false;
      },
      error: () => {
        alert('Erro ao carregar projetos. Confira se o backend está rodando.');
        this.carregando = false;
      },
    });
  }

  novoProjeto(): void {
    this.router.navigate(['/projetos/novo']);
  }

  abrirProjeto(id: number): void {
    this.router.navigate(['/projetos', id]);
  }

  concluirProjeto(event: MouseEvent, id: number): void {
    event.stopPropagation();

    this.projetosService.concluirProjeto(id).subscribe({
      next: () => this.carregarProjetos(),
      error: () => alert('Não foi possível concluir o projeto.'),
    });
  }

  excluirProjeto(event: MouseEvent, id: number): void {
    event.stopPropagation();

    if (!confirm('Deseja excluir este projeto?')) {
      return;
    }

    this.projetosService.excluirProjeto(id).subscribe({
      next: () => this.carregarProjetos(),
      error: () => alert('Não foi possível excluir o projeto.'),
    });
  }

  statusTexto(projeto: Projeto): string {
    return this.projetosService.textoStatusProjeto(projeto.status);
  }

  statusClasse(projeto: Projeto): string {
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
}
