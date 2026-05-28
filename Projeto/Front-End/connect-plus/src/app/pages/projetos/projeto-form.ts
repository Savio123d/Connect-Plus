import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Sidebar } from '../../components/sidebar/sidebar';
import { Pessoa, ProjetosService } from './projetos.service';

@Component({
  selector: 'app-projeto-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Sidebar],
  templateUrl: './projeto-form.html',
  styleUrl: './projeto-form.css',
})
export class ProjetoForm implements OnInit {
  nome = '';
  descricao = '';
  prazo = '';
  liderId: number | null = null;
  participantes: Pessoa[] = [];

  constructor(
    public projetosService: ProjetosService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.projetosService.carregarUsuariosDisponiveis().subscribe({
      next: (usuarios) => {
        this.participantes = usuarios.slice(1, 5).map((usuario) => ({
          ...usuario,
          selecionado: false,
        }));
      },
      error: () => alert('Erro ao carregar usuários disponíveis.'),
    });
  }

  criarProjeto(): void {
    const membrosIds = this.participantes
      .filter((participante) => participante.selecionado)
      .map((participante) => participante.id);

    if (!this.nome || !this.descricao || !this.prazo || !this.liderId) {
      alert('Preencha o nome, descrição, líder e prazo final do projeto.');
      return;
    }

    this.projetosService
      .criarProjeto({
        nome: this.nome,
        descricao: this.descricao,
        prazo: this.prazo,
        liderId: Number(this.liderId),
        membrosIds,
      })
      .subscribe({
        next: () => this.router.navigate(['/projetos']),
        error: () => alert('Não foi possível criar o projeto.'),
      });
  }
}
