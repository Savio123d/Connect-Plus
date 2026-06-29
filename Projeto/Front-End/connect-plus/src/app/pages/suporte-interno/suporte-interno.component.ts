import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-suporte-interno',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './suporte-interno.component.html',
  styleUrl: './suporte-interno.component.css'
})
export class SuporteInternoComponent {
  perguntaAberta: number | null = null;

  perguntas = [
    {
      titulo: 'Como adicionar novos usuários à plataforma?',
      resposta: 'Acesse o menu Usuários, clique em Novo Usuário, preencha os dados solicitados e grave o cadastro.'
    },
    {
      titulo: 'Como funciona o sistema de XP e recompensas?',
      resposta: 'Os usuários ganham XP ao concluir tarefas e podem trocar seus pontos por recompensas disponíveis na Loja.'
    },
    {
      titulo: 'Como criar um novo projeto?',
      resposta: 'Entre no menu Projetos, clique em Novo Projeto, preencha as informações e clique em Gravar.'
    },
    {
      titulo: 'Como enviar feedbacks para membros da equipe?',
      resposta: 'Acesse a tela de Feedbacks, selecione o colaborador, escreva o feedback e envie.'
    },
    {
      titulo: 'Posso alterar minha senha?',
      resposta: 'Sim. A alteração pode ser feita pela tela de Perfil ou Configurações, conforme a regra do sistema.'
    },
    {
      titulo: 'Como funciona o quadro Kanban?',
      resposta: 'O Kanban organiza as tarefas por colunas de status, facilitando o acompanhamento do andamento do projeto.'
    }
  ];

  alternarPergunta(index: number): void {
    this.perguntaAberta = this.perguntaAberta === index ? null : index;
  }
}
