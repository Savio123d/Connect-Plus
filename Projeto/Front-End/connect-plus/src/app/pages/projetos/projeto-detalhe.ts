import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {
  MarcoStatus,
  Pessoa,
  PrioridadeProjeto,
  Projeto,
  ProjetosService,
  ProjetoStatus,
  TarefaStatus,
} from './projetos.service';

type AbaProjeto = 'equipe' | 'tarefas' | 'marcos' | 'horas';

@Component({
  selector: 'app-projeto-detalhe',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './projeto-detalhe.html',
  styleUrl: './projeto-detalhe.css',
})
export class ProjetoDetalhe implements OnInit {
  projeto?: Projeto;
  abaAtiva: AbaProjeto = 'equipe';

  modalMembroAberto = false;
  usuarioSelecionadoId: number | null = null;

  modalMarcoAberto = false;
  novoMarcoTitulo = '';
  novoMarcoData = '';
  novoMarcoStatus: MarcoStatus = 'Pendente';

  modalTarefaAberto = false;
  novaTarefaTitulo = '';
  novaTarefaResponsavelId: number | null = null;
  novaTarefaPrioridade: PrioridadeProjeto = 'Média';
  novaTarefaStatus: TarefaStatus = 'A Fazer';

  constructor(
    public projetosService: ProjetosService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.projetosService.carregarUsuariosDisponiveis().subscribe();
    this.carregarProjeto();
  }

  carregarProjeto(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.projetosService.buscarPorId(id).subscribe({
      next: (projeto) => (this.projeto = projeto),
      error: () => this.router.navigate(['/projetos']),
    });
  }

  get horasRestantes(): number {
    if (!this.projeto) {
      return 0;
    }

    return Math.max(this.projeto.horasEstimadas - this.projeto.horasTrabalhadas, 0);
  }

  get tarefasConcluidas(): number {
    return this.projeto?.tarefas.filter((tarefa) => tarefa.status === 'Concluído').length ?? 0;
  }

  get marcosConcluidos(): number {
    return this.projeto?.marcos.filter((marco) => marco.status === 'Concluído').length ?? 0;
  }

  get usuariosForaDoProjeto(): Pessoa[] {
    if (!this.projeto) {
      return [];
    }

    return this.projetosService.usuariosForaDoProjeto(this.projeto);
  }

  voltar(): void {
    this.router.navigate(['/projetos']);
  }

  trocarAba(aba: AbaProjeto): void {
    this.abaAtiva = aba;
  }

  atualizarStatus(): void {
    if (!this.projeto) {
      return;
    }

    this.projetosService.atualizarStatus(this.projeto.id, this.projeto.status).subscribe({
      next: (projetoAtualizado) => (this.projeto = projetoAtualizado),
      error: () => alert('Não foi possível atualizar o status.'),
    });
  }

  abrirModalMembro(): void {
    this.usuarioSelecionadoId = null;
    this.modalMembroAberto = true;
  }

  fecharModalMembro(): void {
    this.modalMembroAberto = false;
  }

  adicionarMembro(): void {
    if (!this.projeto || !this.usuarioSelecionadoId) {
      return;
    }

    this.projetosService.adicionarMembro(this.projeto.id, Number(this.usuarioSelecionadoId)).subscribe({
      next: (projetoAtualizado) => {
        this.projeto = projetoAtualizado;
        this.fecharModalMembro();
      },
      error: () => alert('Não foi possível adicionar o membro.'),
    });
  }

  abrirModalMarco(): void {
    this.novoMarcoTitulo = '';
    this.novoMarcoData = '';
    this.novoMarcoStatus = 'Pendente';
    this.modalMarcoAberto = true;
  }

  fecharModalMarco(): void {
    this.modalMarcoAberto = false;
  }

  adicionarMarco(): void {
    if (!this.projeto || !this.novoMarcoTitulo || !this.novoMarcoData) {
      alert('Preencha o nome e a data do marco.');
      return;
    }

    this.projetosService
      .adicionarMarco(this.projeto.id, {
        titulo: this.novoMarcoTitulo,
        data: this.novoMarcoData,
        status: this.novoMarcoStatus,
      })
      .subscribe({
        next: (projetoAtualizado) => {
          this.projeto = projetoAtualizado;
          this.fecharModalMarco();
        },
        error: () => alert('Não foi possível adicionar o marco.'),
      });
  }

  abrirModalTarefa(): void {
    this.novaTarefaTitulo = '';
    this.novaTarefaResponsavelId = this.projeto?.membros[0]?.id ?? null;
    this.novaTarefaPrioridade = 'Média';
    this.novaTarefaStatus = 'A Fazer';
    this.modalTarefaAberto = true;
  }

  fecharModalTarefa(): void {
    this.modalTarefaAberto = false;
  }

  adicionarTarefa(): void {
    if (!this.projeto || !this.novaTarefaTitulo || !this.novaTarefaResponsavelId) {
      alert('Preencha o título e o responsável da tarefa.');
      return;
    }

    const responsavel = this.projeto.membros.find(
      (membro) => membro.id === Number(this.novaTarefaResponsavelId),
    );

    this.projetosService
      .adicionarTarefa(this.projeto.id, {
        titulo: this.novaTarefaTitulo,
        responsavel: responsavel?.nome ?? '',
        responsavelId: Number(this.novaTarefaResponsavelId),
        idResponsavelUsuarioEmpresa: Number(this.novaTarefaResponsavelId),
        prioridade: this.novaTarefaPrioridade,
        status: this.novaTarefaStatus,
        horasEstimadas: 8,
      })
      .subscribe({
        next: (projetoAtualizado) => {
          this.projeto = projetoAtualizado;
          this.fecharModalTarefa();
        },
        error: (erro) => {
          console.error('Erro ao adicionar tarefa:', erro);

          const mensagem =
            erro?.error?.erro ||
            erro?.error?.message ||
            erro?.error ||
            erro?.message ||
            'Não foi possível adicionar a tarefa.';

          alert(mensagem);
        },
      });
  }

  dataFormatada(data: string): string {
    return this.projetosService.formatarData(data);
  }

  textoStatus(status: ProjetoStatus): string {
    return this.projetosService.textoStatusProjeto(status);
  }

  classeStatusProjeto(status: ProjetoStatus): string {
    if (status === 'concluido') {
      return 'concluido';
    }

    if (status === 'planejamento') {
      return 'planejamento';
    }

    if (status === 'cancelado') {
      return 'cancelado';
    }

    return 'andamento';
  }

  classePrioridade(prioridade: PrioridadeProjeto): string {
    return prioridade.toLowerCase().replace('é', 'e');
  }

  classeStatusTarefa(status: TarefaStatus): string {
    if (status === 'Concluído') {
      return 'concluido';
    }

    if (status === 'Em Andamento') {
      return 'andamento';
    }

    return 'pendente';
  }

  classeStatusMarco(status: MarcoStatus): string {
    if (status === 'Concluído') {
      return 'concluido';
    }

    if (status === 'Em Andamento') {
      return 'andamento';
    }

    return 'pendente';
  }
}
