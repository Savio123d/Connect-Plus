import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { catchError, finalize, of } from 'rxjs';
import { CdkDragDrop, DragDropModule } from '@angular/cdk/drag-drop';

import { Sidebar } from '../../components/sidebar/sidebar';

type StatusTarefa =
  | 'pendente'
  | 'em_andamento'
  | 'em_revisao'
  | 'concluida'
  | 'cancelada'
  | 'arquivada';

type PrioridadeTarefa = 'baixa' | 'media' | 'alta';
type DificuldadeTarefa = 'facil' | 'medio' | 'dificil';

interface TarefaResponseDTO {
  idTarefa: number;
  idEmpresa: number;
  idProjeto: number;
  idResponsavelUsuarioEmpresa: number | null;
  titulo: string;
  descricao: string;
  prioridade: PrioridadeTarefa;
  dificuldade: DificuldadeTarefa;
  status: StatusTarefa;
  horasEstimadas: number | null;
  xpRecompensa: number;
  dataCriacao: string;
  prazo: string | null;
  concluidaEm: string | null;
  dataAtualizacao: string;
}

interface TarefaForm {
  idEmpresa: number;
  idProjeto: number | null;
  idResponsavelUsuarioEmpresa: number | null;
  titulo: string;
  descricao: string;
  prioridade: PrioridadeTarefa;
  dificuldade: DificuldadeTarefa;
  horasEstimadas: number | null;
  prazo: string;
}

interface ProjetoOption {
  id: number;
  nome: string;
  idEmpresa?: number;
}

interface ResponsavelOption {
  id: number;
  nome: string;
}

@Component({
  selector: 'app-tarefas',
  standalone: true,
  imports: [CommonModule, FormsModule, DragDropModule, Sidebar],
  templateUrl: './tarefas.html',
  styleUrl: './tarefas.css',
})
export class Tarefas implements OnInit {
  private readonly apiTarefas = 'http://localhost:8080/api/tarefas';
  private readonly apiProjetos = 'http://localhost:8080/api/projetos';

  private readonly apiResponsaveis = 'http://localhost:8080/api/usuario-empresa';
  private readonly apiUsuarios = 'http://localhost:8080/api/usuarios';

  telaAtual: 'kanban' | 'formulario' | 'detalhes' = 'kanban';
  modoFormulario: 'criar' | 'editar' = 'criar';

  carregando = false;
  salvando = false;
  mensagemErro = '';

  empresaPadraoId = 1;

  projetoPadrao: ProjetoOption = {
    id: 1,
    nome: 'Projeto padrão Connect+',
    idEmpresa: 1,
  };

  tarefas: TarefaResponseDTO[] = [];
  tarefaSelecionada: TarefaResponseDTO | null = null;

  projetos: ProjetoOption[] = [this.projetoPadrao];
  responsaveis: ResponsavelOption[] = [];

  formTarefa: TarefaForm = this.novoFormulario();

  idsColunas = ['coluna-pendente', 'coluna-em-andamento', 'coluna-em-revisao', 'coluna-concluida'];

  colunas: Array<{
    id: string;
    titulo: string;
    subtitulo: string;
    status: StatusTarefa;
    classe: string;
  }> = [
    {
      id: 'coluna-pendente',
      titulo: 'Backlog',
      subtitulo: 'Tarefas aguardando início',
      status: 'pendente',
      classe: 'gradiente-backlog',
    },
    {
      id: 'coluna-em-andamento',
      titulo: 'Fazendo',
      subtitulo: 'Tarefas em desenvolvimento',
      status: 'em_andamento',
      classe: 'gradiente-andamento',
    },
    {
      id: 'coluna-em-revisao',
      titulo: 'Em revisão',
      subtitulo: 'Tarefas em validação',
      status: 'em_revisao',
      classe: 'gradiente-revisao',
    },
    {
      id: 'coluna-concluida',
      titulo: 'Concluída',
      subtitulo: 'Tarefas finalizadas',
      status: 'concluida',
      classe: 'gradiente-concluida',
    },
  ];

  prioridades = [
    {
      valor: 'baixa' as PrioridadeTarefa,
      titulo: 'Baixa',
      subtitulo: 'Sem urgência',
    },
    {
      valor: 'media' as PrioridadeTarefa,
      titulo: 'Média',
      subtitulo: 'Prazo normal',
    },
    {
      valor: 'alta' as PrioridadeTarefa,
      titulo: 'Alta',
      subtitulo: 'Urgente',
    },
  ];

  dificuldades = [
    {
      valor: 'facil' as DificuldadeTarefa,
      titulo: 'Fácil',
      xp: '+10 XP',
    },
    {
      valor: 'medio' as DificuldadeTarefa,
      titulo: 'Média',
      xp: '+20 XP',
    },
    {
      valor: 'dificil' as DificuldadeTarefa,
      titulo: 'Difícil',
      xp: '+50 XP',
    },
  ];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.carregarDadosIniciais();
  }

  carregarDadosIniciais(): void {
    this.carregarTarefas();
    this.carregarProjetos();
    this.carregarResponsaveis();
  }

  carregarTarefas(): void {
    this.carregando = true;
    this.mensagemErro = '';

    this.http.get<TarefaResponseDTO[]>(this.apiTarefas).subscribe({
      next: (tarefas) => {
        this.tarefas = tarefas;
        this.carregando = false;
      },
      error: (erro) => {
        console.error('Erro ao carregar tarefas:', erro);
        this.mensagemErro = 'Não foi possível carregar as tarefas.';
        this.carregando = false;
      },
    });
  }

  carregarProjetos(): void {
    this.http
      .get<any[]>(this.apiProjetos)
      .pipe(catchError(() => of([])))
      .subscribe((projetos) => {
        const projetosMapeados: ProjetoOption[] = projetos.map((projeto) => ({
          id: projeto.idProjeto ?? projeto.id,
          nome: projeto.nome ?? projeto.titulo ?? `Projeto #${projeto.idProjeto ?? projeto.id}`,
          idEmpresa: projeto.idEmpresa,
        }));

        if (projetosMapeados.length > 0) {
          this.projetos = projetosMapeados;

          const projetoAtualExiste = this.projetos.some(
            (projeto) => projeto.id === this.formTarefa.idProjeto,
          );

          if (!projetoAtualExiste) {
            this.formTarefa.idProjeto = this.projetos[0].id;
          }

          return;
        }

        this.projetos = [this.projetoPadrao];
        this.formTarefa.idProjeto = this.projetoPadrao.id;
      });
  }

  carregarResponsaveis(): void {
    this.http
      .get<any[]>(this.apiResponsaveis)
      .pipe(
        catchError(() => this.http.get<any[]>(this.apiUsuarios)),
        catchError(() => of([])),
      )
      .subscribe((responsaveis) => {
        this.responsaveis = responsaveis
          .map((responsavel) => {
            const id =
              responsavel.idUsuarioEmpresa ??
              responsavel.idResponsavelUsuarioEmpresa ??
              responsavel.idUsuario ??
              responsavel.id;

            const nome =
              responsavel.nome ??
              responsavel.nomeUsuario ??
              responsavel.usuario?.nome ??
              `Responsável #${id}`;

            return { id, nome };
          })
          .filter((responsavel) => responsavel.id !== undefined && responsavel.id !== null);
      });
  }

  tarefasPorStatus(status: StatusTarefa): TarefaResponseDTO[] {
    return this.tarefas.filter((tarefa) => tarefa.status === status);
  }

  abrirCriacao(): void {
    this.modoFormulario = 'criar';
    this.formTarefa = this.novoFormulario();
    this.tarefaSelecionada = null;
    this.mensagemErro = '';
    this.telaAtual = 'formulario';
  }

  abrirEdicao(tarefa: TarefaResponseDTO): void {
    this.modoFormulario = 'editar';
    this.tarefaSelecionada = tarefa;

    this.formTarefa = {
      idEmpresa: tarefa.idEmpresa ?? this.empresaPadraoId,
      idProjeto: tarefa.idProjeto ?? this.projetoPadrao.id,
      idResponsavelUsuarioEmpresa: tarefa.idResponsavelUsuarioEmpresa,
      titulo: tarefa.titulo,
      descricao: tarefa.descricao,
      prioridade: tarefa.prioridade ?? 'media',
      dificuldade: tarefa.dificuldade ?? 'medio',
      horasEstimadas: tarefa.horasEstimadas,
      prazo: this.converterDataParaInput(tarefa.prazo),
    };

    this.mensagemErro = '';
    this.telaAtual = 'formulario';
  }

  abrirDetalhes(tarefa: TarefaResponseDTO): void {
    this.http.get<TarefaResponseDTO>(`${this.apiTarefas}/${tarefa.idTarefa}`).subscribe({
      next: (tarefaAtualizada) => {
        this.tarefaSelecionada = tarefaAtualizada;
        this.telaAtual = 'detalhes';
      },
      error: () => {
        this.tarefaSelecionada = tarefa;
        this.telaAtual = 'detalhes';
      },
    });
  }

  voltarParaKanban(): void {
    this.telaAtual = 'kanban';
    this.tarefaSelecionada = null;
    this.mensagemErro = '';
  }

  salvarTarefa(): void {
    if (!this.formularioValido()) {
      return;
    }

    this.salvando = true;
    this.mensagemErro = '';

    const payload = this.montarPayload();

    this.telaAtual = 'kanban';

    if (this.modoFormulario === 'criar') {
      this.tarefaSelecionada = null;
      this.formTarefa = this.novoFormulario();

      this.http.post<TarefaResponseDTO>(this.apiTarefas, payload).subscribe({
        next: (novaTarefa) => {
          this.tarefas = [...this.tarefas, novaTarefa];
          this.salvando = false;
        },
        error: (erro) => {
          console.error('Erro ao criar tarefa:', erro);

          this.mensagemErro =
            erro?.error?.message || erro?.message || 'Não foi possível criar a tarefa.';

          this.salvando = false;
        },
      });

      return;
    }

    if (!this.tarefaSelecionada) {
      this.mensagemErro = 'Nenhuma tarefa selecionada para edição.';
      this.salvando = false;
      return;
    }

    const idTarefa = this.tarefaSelecionada.idTarefa;

    this.tarefaSelecionada = null;
    this.formTarefa = this.novoFormulario();

    this.http.put<TarefaResponseDTO>(`${this.apiTarefas}/${idTarefa}`, payload).subscribe({
      next: (tarefaAtualizada) => {
        this.tarefas = this.tarefas.map((tarefa) =>
          tarefa.idTarefa === tarefaAtualizada.idTarefa ? tarefaAtualizada : tarefa,
        );

        this.salvando = false;
      },
      error: (erro) => {
        console.error('Erro ao atualizar tarefa:', erro);

        this.mensagemErro =
          erro?.error?.message || erro?.message || 'Não foi possível salvar as alterações.';

        this.salvando = false;
      },
    });
  }
  moverTarefa(event: CdkDragDrop<TarefaResponseDTO[]>, novoStatus: StatusTarefa): void {
    const tarefa = event.item.data as TarefaResponseDTO;

    if (!tarefa || tarefa.status === novoStatus) {
      return;
    }

    const statusAnterior = tarefa.status;

    tarefa.status = novoStatus;

    this.http
      .patch<TarefaResponseDTO>(`${this.apiTarefas}/${tarefa.idTarefa}/status`, {
        status: novoStatus,
      })
      .subscribe({
        next: (tarefaAtualizada) => {
          this.tarefas = this.tarefas.map((item) =>
            item.idTarefa === tarefaAtualizada.idTarefa ? tarefaAtualizada : item,
          );
        },
        error: (erro) => {
          console.error('Erro ao atualizar status:', erro);
          tarefa.status = statusAnterior;
          this.mensagemErro = 'Não foi possível atualizar o status da tarefa.';
        },
      });
  }

  deletarTarefa(tarefa: TarefaResponseDTO | null = this.tarefaSelecionada): void {
    if (!tarefa) {
      return;
    }

    const confirmar = confirm(`Deseja realmente excluir a tarefa "${tarefa.titulo}"?`);

    if (!confirmar) {
      return;
    }

    const idTarefa = tarefa.idTarefa;
    this.telaAtual = 'kanban';
    this.tarefaSelecionada = null;
    this.mensagemErro = '';

    const tarefasAntesDeExcluir = [...this.tarefas];

    this.tarefas = this.tarefas.filter((item) => item.idTarefa !== idTarefa);

    this.http.delete<void>(`${this.apiTarefas}/${idTarefa}`).subscribe({
      next: () => {
      },
      error: (erro) => {
        console.error('Erro ao excluir tarefa:', erro);

        this.tarefas = tarefasAntesDeExcluir;

        this.mensagemErro =
          erro?.error?.message || erro?.message || 'Não foi possível excluir a tarefa.';
      },
    });
  }

  selecionarPrioridade(prioridade: PrioridadeTarefa): void {
    this.formTarefa.prioridade = prioridade;
  }

  selecionarDificuldade(dificuldade: DificuldadeTarefa): void {
    this.formTarefa.dificuldade = dificuldade;
  }

  buscarNomeProjeto(idProjeto: number | null): string {
    if (!idProjeto) {
      return this.projetoPadrao.nome;
    }

    return (
      this.projetos.find((projeto) => projeto.id === idProjeto)?.nome ?? `Projeto #${idProjeto}`
    );
  }

  buscarNomeResponsavel(idResponsavel: number | null): string {
    if (!idResponsavel) {
      return 'Não atribuído';
    }

    return (
      this.responsaveis.find((responsavel) => responsavel.id === idResponsavel)?.nome ??
      `Responsável #${idResponsavel}`
    );
  }

  labelPrioridade(prioridade: PrioridadeTarefa): string {
    const labels: Record<PrioridadeTarefa, string> = {
      baixa: 'Baixa',
      media: 'Média',
      alta: 'Alta',
    };

    return labels[prioridade] ?? prioridade;
  }

  labelDificuldade(dificuldade: DificuldadeTarefa): string {
    const labels: Record<DificuldadeTarefa, string> = {
      facil: 'Fácil',
      medio: 'Média',
      dificil: 'Difícil',
    };

    return labels[dificuldade] ?? dificuldade;
  }

  labelStatus(status: StatusTarefa): string {
    const labels: Record<StatusTarefa, string> = {
      pendente: 'Backlog',
      em_andamento: 'Em andamento',
      em_revisao: 'Em revisão',
      concluida: 'Concluída',
      cancelada: 'Cancelada',
      arquivada: 'Arquivada',
    };

    return labels[status] ?? status;
  }

  formatarData(data: string | null): string {
    if (!data) {
      return 'Sem prazo';
    }

    const somenteData = data.split('T')[0];
    const [ano, mes, dia] = somenteData.split('-');

    if (!ano || !mes || !dia) {
      return data;
    }

    return `${dia}/${mes}/${ano}`;
  }

  formatarPrazoDigitado(event: Event): void {
    const input = event.target as HTMLInputElement;
    const numeros = input.value.replace(/\D/g, '').slice(0, 8);

    let valorFormatado = numeros;

    if (numeros.length > 2 && numeros.length <= 4) {
      valorFormatado = `${numeros.slice(0, 2)}/${numeros.slice(2)}`;
    }

    if (numeros.length > 4) {
      valorFormatado = `${numeros.slice(0, 2)}/${numeros.slice(2, 4)}/${numeros.slice(4)}`;
    }

    this.formTarefa.prazo = valorFormatado;
    input.value = valorFormatado;
  }

  badgePrioridadeClasse(prioridade: PrioridadeTarefa): string {
    return {
      baixa: 'badge-baixa',
      media: 'badge-media',
      alta: 'badge-alta',
    }[prioridade];
  }

  private novoFormulario(): TarefaForm {
    return {
      idEmpresa: this.empresaPadraoId,
      idProjeto: this.projetoPadrao.id,
      idResponsavelUsuarioEmpresa: null,
      titulo: '',
      descricao: '',
      prioridade: 'media',
      dificuldade: 'medio',
      horasEstimadas: 8,
      prazo: '',
    };
  }

  private formularioValido(): boolean {
    if (!this.formTarefa.titulo.trim()) {
      this.mensagemErro = 'Informe o nome da tarefa.';
      return false;
    }

    if (!this.formTarefa.idProjeto) {
      this.mensagemErro = 'Selecione o projeto.';
      return false;
    }

    if (!this.formTarefa.horasEstimadas || this.formTarefa.horasEstimadas <= 0) {
      this.mensagemErro = 'Informe uma quantidade válida de horas estimadas.';
      return false;
    }

    return true;
  }

  private montarPayload() {
    const projetoSelecionado =
      this.projetos.find((projeto) => projeto.id === this.formTarefa.idProjeto) ??
      this.projetoPadrao;

    return {
      idEmpresa: projetoSelecionado.idEmpresa ?? this.empresaPadraoId,
      idProjeto: this.formTarefa.idProjeto ?? this.projetoPadrao.id,
      idResponsavelUsuarioEmpresa: this.formTarefa.idResponsavelUsuarioEmpresa,
      titulo: this.formTarefa.titulo.trim(),
      descricao: this.formTarefa.descricao.trim(),
      prioridade: this.formTarefa.prioridade,
      dificuldade: this.formTarefa.dificuldade,
      horasEstimadas: this.formTarefa.horasEstimadas,
      prazo: this.converterPrazoParaBack(this.formTarefa.prazo),
    };
  }

  private converterPrazoParaBack(data: string): string | null {
    if (!data) {
      return null;
    }

    const partes = data.split('/');

    if (partes.length !== 3) {
      return null;
    }

    const [dia, mes, ano] = partes;

    if (!dia || !mes || !ano || ano.length !== 4) {
      return null;
    }

    return `${ano}-${mes.padStart(2, '0')}-${dia.padStart(2, '0')}T00:00:00`;
  }

  private converterDataParaInput(data: string | null): string {
    if (!data) {
      return '';
    }

    const somenteData = data.split('T')[0];
    const [ano, mes, dia] = somenteData.split('-');

    if (!ano || !mes || !dia) {
      return '';
    }

    return `${dia}/${mes}/${ano}`;
  }
}
