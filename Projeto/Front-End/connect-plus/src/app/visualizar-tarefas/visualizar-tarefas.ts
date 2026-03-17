import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

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
  imports: [CommonModule],
  templateUrl: './visualizar-tarefas.html',
  styleUrl: './visualizar-tarefas.css',
})
export class VisualizarTarefasComponent {
  menuItems = ['Home', 'Feedback', 'Perfil', 'Quadro de tarefas', 'Chat', 'Projetos'];

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

  get tarefasHoje(): number {
    return this.backlog.length + this.aFazer.length + this.fazendo.length + this.concluida.length;
  }
}
