import { ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { Projeto, ProjetoResumo, ProjetosService } from './projetos.service';
import { Projetos } from './projetos';

describe('Projetos', () => {
  const resumo: ProjetoResumo = {
    id: 1,
    nome: 'Portal',
    descricao: 'Projeto principal',
    status: 'em_andamento',
    atrasado: false,
    prazo: '2026-08-20',
    progresso: 40,
    quantidadeMembros: 3,
    liderNome: 'Ana Silva',
    liderIniciais: 'AS',
  };

  const projetoConcluido: Projeto = {
    id: 1,
    nome: 'Portal',
    descricao: 'Projeto principal',
    status: 'concluido',
    atrasado: false,
    prioridade: 'Alta',
    prazo: '2026-08-20',
    inicio: '2026-07-01',
    progresso: 100,
    horasTrabalhadas: 20,
    horasEstimadas: 40,
    lider: {
      id: 10,
      nome: 'Ana Silva',
      cargo: 'Lider',
      email: 'ana@example.com',
      iniciais: 'AS',
    },
    membros: [
      {
        id: 10,
        nome: 'Ana Silva',
        cargo: 'Lider',
        email: 'ana@example.com',
        iniciais: 'AS',
      },
    ],
    tarefas: [],
    marcos: [],
  };

  let service: {
    listar: ReturnType<typeof vi.fn>;
    concluirProjeto: ReturnType<typeof vi.fn>;
    excluirProjeto: ReturnType<typeof vi.fn>;
    textoStatusProjeto: ReturnType<typeof vi.fn>;
    formatarData: ReturnType<typeof vi.fn>;
  };
  let component: Projetos;

  beforeEach(() => {
    service = {
      listar: vi.fn(() => of([resumo])),
      concluirProjeto: vi.fn(() => of(projetoConcluido)),
      excluirProjeto: vi.fn(() => of(void 0)),
      textoStatusProjeto: vi.fn((status: string) => status),
      formatarData: vi.fn((data: string) => data.split('-').reverse().join('/')),
    };

    component = new Projetos(
      service as unknown as ProjetosService,
      { markForCheck: vi.fn() } as unknown as ChangeDetectorRef,
      { navigate: vi.fn() } as unknown as Router,
    );
  });

  it('prepara os cards uma vez e filtra pelo texto normalizado', () => {
    component.ngOnInit();

    expect(service.listar).toHaveBeenCalledTimes(1);
    expect(component.projetosFiltrados).toHaveLength(1);
    expect(component.projetos[0].quantidadeMembros).toBe(3);
    expect(component.projetos[0].prazoFormatado).toBe('20/08/2026');

    component.termoBusca = 'ana';
    component.filtrarProjetos();
    expect(component.projetosFiltrados.map((projeto) => projeto.id)).toEqual([1]);

    component.termoBusca = 'inexistente';
    component.filtrarProjetos();
    expect(component.projetosFiltrados).toEqual([]);
  });

  it('atualiza o card concluido sem baixar toda a lista novamente', () => {
    component.ngOnInit();
    const event = { stopPropagation: vi.fn() } as unknown as MouseEvent;

    component.concluirProjeto(event, 1);

    expect(event.stopPropagation).toHaveBeenCalled();
    expect(service.concluirProjeto).toHaveBeenCalledWith(1);
    expect(service.listar).toHaveBeenCalledTimes(1);
    expect(component.projetos[0].status).toBe('concluido');
    expect(component.projetos[0].progresso).toBe(100);
  });
});
