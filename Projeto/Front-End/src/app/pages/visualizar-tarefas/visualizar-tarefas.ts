import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  DragDropModule,
  CdkDragDrop,
  moveItemInArray,
  transferArrayItem,
} from '@angular/cdk/drag-drop';

interface Tarefa {
  id: number;
  titulo: string;
  descricao: string;
  responsavel: string;
  prioridade: 'Baixa' | 'Média' | 'Alta';
  prazo: string;
}

@Component({
  selector: 'app-visualizar-tarefas',
  standalone: true,
  imports: [CommonModule, FormsModule, DragDropModule],
  templateUrl: './visualizar-tarefas.html',
  styleUrl: './visualizar-tarefas.css',
})
export class VisualizarTarefasComponent {
  menuItems = ['Home', 'Feedback', 'Perfil', 'Quadro de tarefas', 'Chat', 'Projetos'];

  modalAberto = false;
  modoModal: 'criar' | 'editar' = 'criar';
  tarefaEditandoId: number | null = null;

  formTarefa: Tarefa = this.criarFormularioVazio();

  backlog: Tarefa[] = [
    {
      id: 1,
      titulo: 'Definir campos do formulário',
      descricao: 'Organizar os campos principais para criação de tarefas.',
      responsavel: 'Gustavo',
      prioridade: 'Alta',
      prazo: '18/03/2026',
    },
  ];

  aFazer: Tarefa[] = [
    {
      id: 2,
      titulo: 'Criar tela de login',
      descricao: 'Montar interface inicial de autenticação.',
      responsavel: 'Ana',
      prioridade: 'Alta',
      prazo: '20/03/2026',
    },
  ];

  fazendo: Tarefa[] = [
    {
      id: 3,
      titulo: 'Montar layout do kanban',
      descricao: 'Estruturar colunas e cards da tela principal.',
      responsavel: 'João',
      prioridade: 'Média',
      prazo: '21/03/2026',
    },
  ];

  concluida: Tarefa[] = [
    {
      id: 4,
      titulo: 'Ajustar dashboard',
      descricao: 'Refinar visual e blocos de informações.',
      responsavel: 'Maria',
      prioridade: 'Baixa',
      prazo: '22/03/2026',
    },
  ];

  criarFormularioVazio(): Tarefa {
    return {
      id: 0,
      titulo: '',
      descricao: '',
      responsavel: '',
      prioridade: 'Média',
      prazo: '',
    };
  }

  get tarefasHoje(): number {
    return this.backlog.length + this.aFazer.length + this.fazendo.length + this.concluida.length;
  }

  abrirCriacao(): void {
    this.modoModal = 'criar';
    this.tarefaEditandoId = null;
    this.formTarefa = this.criarFormularioVazio();
    this.modalAberto = true;
  }

  abrirEdicao(tarefa: Tarefa): void {
    this.modoModal = 'editar';
    this.tarefaEditandoId = tarefa.id;
    this.formTarefa = { ...tarefa };
    this.modalAberto = true;
  }

  fecharModal(): void {
    this.modalAberto = false;
    this.tarefaEditandoId = null;
    this.formTarefa = this.criarFormularioVazio();
  }

  deletarTarefa(): void {
    if (this.tarefaEditandoId === null) {
      return;
    }

    const confirmar = confirm('Tem certeza que deseja excluir esta tarefa?');

    if (!confirmar) {
      return;
    }

    const listas = [this.backlog, this.aFazer, this.fazendo, this.concluida];

    for (const lista of listas) {
      const index = lista.findIndex((t) => t.id === this.tarefaEditandoId);

      if (index !== -1) {
        lista.splice(index, 1);
        this.fecharModal();
        return;
      }
    }
  }

  salvarTarefa(): void {
    if (
      !this.formTarefa.titulo.trim() ||
      !this.formTarefa.descricao.trim() ||
      !this.formTarefa.responsavel.trim() ||
      !this.formTarefa.prazo.trim()
    ) {
      alert('Preencha título, descrição, responsável e prazo.');
      return;
    }

    if (this.modoModal === 'criar') {
      const novaTarefa: Tarefa = {
        ...this.formTarefa,
        id: this.gerarNovoId(),
      };

      this.backlog.push(novaTarefa);
    } else {
      this.atualizarTarefaExistente();
    }

    this.fecharModal();
  }

  gerarNovoId(): number {
    const todas = [...this.backlog, ...this.aFazer, ...this.fazendo, ...this.concluida];
    if (todas.length === 0) {
      return 1;
    }

    return Math.max(...todas.map((t) => t.id)) + 1;
  }

  atualizarTarefaExistente(): void {
    if (this.tarefaEditandoId === null) {
      return;
    }

    const listas = [this.backlog, this.aFazer, this.fazendo, this.concluida];

    for (const lista of listas) {
      const index = lista.findIndex((t) => t.id === this.tarefaEditandoId);

      if (index !== -1) {
        lista[index] = {
          ...lista[index],
          titulo: this.formTarefa.titulo,
          descricao: this.formTarefa.descricao,
          responsavel: this.formTarefa.responsavel,
          prioridade: this.formTarefa.prioridade,
          prazo: this.formTarefa.prazo,
        };
        return;
      }
    }
  }

  drop(event: CdkDragDrop<Tarefa[]>): void {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );
    }
  }
}
