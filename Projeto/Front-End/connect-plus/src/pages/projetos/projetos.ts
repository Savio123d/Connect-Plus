import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Sidebar } from '../../components/sidebar/sidebar';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

interface Projeto {
  id: number;
  nome: string;
  descricao: string;
  status: string;
  atraso?: boolean;
  progresso: number;
  prazo: string;
  membros: number;
  lider: string;
  iniciais: string;
}

@Component({
  selector: 'app-projetos',
  standalone: true,
  imports: [CommonModule, Sidebar,FormsModule],
  templateUrl: './projetos.html',
  styleUrls: ['./projetos.css']
})
export class ProjetosComponent {
  projetos: Projeto[] = [
    {
      id: 1,
      nome: 'Plataforma Connect+',
      descricao: 'Sistema de gestão de equipes e projetos',
      status: 'Em Andamento',
      progresso: 65,
      prazo: '30/06/2026',
      membros: 8,
      lider: 'João Silva',
      iniciais: 'JS'
    },
    {
      id: 2,
      nome: 'Dashboard Analytics',
      descricao: 'Painel de análise de dados e métricas',
      status: 'Planejamento',
      atraso: true,
      progresso: 25,
      prazo: '15/05/2026',
      membros: 5,
      lider: 'Maria Santos',
      iniciais: 'MS'
    },
    {
      id: 3,
      nome: 'API REST v2',
      descricao: 'Refatoração completa da API principal',
      status: 'Em Andamento',
      progresso: 80,
      prazo: '20/07/2026',
      membros: 6,
      lider: 'Carlos Lima',
      iniciais: 'CL'
    },
    {
      id: 4,
      nome: 'Mobile App',
      descricao: 'Aplicativo mobile React Native',
      status: 'Planejamento',
      progresso: 15,
      prazo: '10/08/2026',
      membros: 4,
      lider: 'Ana Souza',
      iniciais: 'AS'
    }
  ];


  currentProject: Projeto = { id: 0, nome: '', descricao: '', status: '', progresso: 0, prazo: '', membros: 0, lider: '', iniciais: '' };
  isEditing: boolean = false;
  isCreating: boolean = false;


  onEdit(projeto: Projeto): void {
    this.isEditing = true;
    this.isCreating = false;
    this.currentProject = { ...projeto };
  }


  onCreate(): void {
    this.isCreating = true;
    this.isEditing = false;
    this.currentProject = { id: 0, nome: '', descricao: '', status: '', progresso: 0, prazo: '', membros: 0, lider: '', iniciais: '' };
  }


  onSubmit(): void {
    if (this.isEditing) {
      const index = this.projetos.findIndex(p => p.id === this.currentProject.id);
      if (index !== -1) {
        this.projetos[index] = { ...this.currentProject };
      }
      alert(`Projeto ${this.currentProject.nome} atualizado!`);
    } else if (this.isCreating) {
      this.currentProject.id = this.projetos.length + 1;
      this.projetos.push({ ...this.currentProject });
      alert(`Projeto ${this.currentProject.nome} criado!`);
    }
  }

  // Função para excluir um projeto
  excluirProjeto(projeto: Projeto): void {
    const index = this.projetos.findIndex(p => p.id === projeto.id);
    if (index !== -1) {
      this.projetos.splice(index, 1);
      alert(`Projeto ${projeto.nome} excluído!`);
    }
  }

  // Função para concluir um projeto
  concluirProjeto(projeto: Projeto): void {
    alert(`Projeto ${projeto.nome} concluído!`);
  }
}
