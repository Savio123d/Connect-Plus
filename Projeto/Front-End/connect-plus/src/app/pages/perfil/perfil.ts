import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './perfil.html',
  styleUrls: ['./perfil.css'],
})
export class Perfil {
  usuario = {
    nome: 'João Silva',
    cargo: 'Desenvolvedor Full Stack',
    email: 'joaosilva@empresa.com',
    departamento: 'Tecnologia',
    nivel: 7,
    xpAtual: 3560,
    xpProximoNivel: 5000,
  };

  conquistas = [
    {
      titulo: 'Primeira Tarefa',
      icone: '◎',
      cor: 'verde',
    },
    {
      titulo: '10 Projetos',
      icone: '🏆',
      cor: 'laranja',
    },
    {
      titulo: '100 Tarefas',
      icone: '☆',
      cor: 'azul',
    },
    {
      titulo: 'MVP do Mês',
      icone: '♙',
      cor: 'roxo',
    },
  ];

  historico = [
    {
      mes: 'Janeiro',
      tarefasConcluidas: 42,
      xpGanho: 840,
    },
    {
      mes: 'Fevereiro',
      tarefasConcluidas: 38,
      xpGanho: 760,
    },
    {
      mes: 'Março',
      tarefasConcluidas: 51,
      xpGanho: 1020,
    },
    {
      mes: 'Abril',
      tarefasConcluidas: 47,
      xpGanho: 940,
    },
  ];

  progressoXp(): number {
    return Math.min(
      (this.usuario.xpAtual / this.usuario.xpProximoNivel) * 100,
      100
    );
  }

  xpRestante(): number {
    return this.usuario.xpProximoNivel - this.usuario.xpAtual;
  }

  iniciaisUsuario(): string {
    const partesNome = this.usuario.nome.trim().split(' ');

    if (partesNome.length === 1) {
      return partesNome[0].substring(0, 2).toUpperCase();
    }

    const primeiraLetra = partesNome[0][0];
    const ultimaLetra = partesNome[partesNome.length - 1][0];

    return `${primeiraLetra}${ultimaLetra}`.toUpperCase();
  }

  classeConquista(cor: string): string {
    return `conquista-${cor}`;
  }
}
