import { ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { Projeto, ProjetosService } from './projetos.service';
import { ProjetoDetalhe } from './projeto-detalhe';

describe('ProjetoDetalhe', () => {
  const projeto: Projeto = {
    id: 7,
    nome: 'Portal',
    descricao: 'Projeto principal',
    status: 'em_andamento',
    atrasado: false,
    prioridade: 'Alta',
    prazo: '2026-08-20',
    inicio: '2026-07-01',
    progresso: 40,
    horasTrabalhadas: 20,
    horasEstimadas: 40,
    lider: {
      id: 10,
      nome: 'Ana Silva',
      cargo: 'Lider',
      email: 'ana@example.com',
      iniciais: 'AS',
    },
    membros: [],
    tarefas: [],
    marcos: [],
  };

  let service: {
    usuariosDisponiveis: never[];
    buscarPorId: ReturnType<typeof vi.fn>;
    carregarUsuariosDisponiveis: ReturnType<typeof vi.fn>;
    usuariosForaDoProjeto: ReturnType<typeof vi.fn>;
  };
  let component: ProjetoDetalhe;

  beforeEach(() => {
    service = {
      usuariosDisponiveis: [],
      buscarPorId: vi.fn(() => of(projeto)),
      carregarUsuariosDisponiveis: vi.fn(() => of([])),
      usuariosForaDoProjeto: vi.fn(() => []),
    };

    const route = {
      snapshot: {
        paramMap: { get: vi.fn(() => '7') },
      },
    };

    component = new ProjetoDetalhe(
      service as unknown as ProjetosService,
      { markForCheck: vi.fn() } as unknown as ChangeDetectorRef,
      route as unknown as ActivatedRoute,
      { navigate: vi.fn() } as unknown as Router,
    );
  });

  it('carrega somente o projeto ao entrar na tela', () => {
    component.ngOnInit();

    expect(service.buscarPorId).toHaveBeenCalledWith(7);
    expect(service.carregarUsuariosDisponiveis).not.toHaveBeenCalled();
    expect(component.projeto).toBe(projeto);
  });

  it('carrega usuarios somente na primeira abertura do modal', () => {
    component.ngOnInit();

    component.abrirModalMembro();
    component.fecharModalMembro();
    component.abrirModalMembro();

    expect(service.carregarUsuariosDisponiveis).toHaveBeenCalledTimes(1);
    expect(component.usuariosDisponiveisCarregados).toBe(true);
    expect(component.modalMembroAberto).toBe(true);
  });
});
