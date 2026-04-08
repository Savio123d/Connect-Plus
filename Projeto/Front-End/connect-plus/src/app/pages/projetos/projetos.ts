import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import{Sidebar} from '../../components/sidebar/sidebar';


interface Projeto {
  id: number;
  nome: string;
  cor: string;
}

@Component({
  selector: 'app-projetos',
  standalone: true,
  imports: [Sidebar, CommonModule],
  templateUrl: './projetos.html',
  styleUrl: './projetos.css'
})
export class projetos {
  projetos: Projeto[] = [
    { id: 1, nome: 'Connect+', cor: '#7c3aed' },
    { id: 2, nome: 'Dashboard', cor: '#2563eb' },
    { id: 3, nome: 'Kanban', cor: '#f97316' }
  ];

  projetoSelecionado: Projeto | null = null;

  selecionarProjeto(projeto: Projeto): void {
    this.projetoSelecionado = projeto;
  }

  criarProjeto(): void {
    alert('Aqui você pode abrir a tela ou modal de criar projeto.');
  }

  editarProjeto(): void {
    if (!this.projetoSelecionado) {
      alert('Selecione um projeto para editar.');
      return;
    }

    alert(`Editando projeto: ${this.projetoSelecionado.nome}`);
  }
}
