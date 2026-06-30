import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { AuthSessionService } from '../../core/auth-session.service';
import {
  CategoriaLoja,
  CorLoja,
  IconeLoja,
  ItemLoja,
  ItemLojaRequest,
  LojaService,
} from './loja.service';

@Component({
  selector: 'app-loja',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './loja.html',
  styleUrls: ['./loja.css'],
})
export class Loja implements OnInit {
  private lojaService = inject(LojaService);
  private authSessionService = inject(AuthSessionService);

  itens: ItemLoja[] = [];
  saldoXp = 0;
  empresaId = 0;
  usuarioEmpresaId = 0;
  carregando = false;
  salvando = false;
  filtroCategoria: CategoriaLoja | 'Todos' = 'Todos';
  mensagemErro = '';
  mensagemSucesso = '';
  mostrarModal = false;
  itemEditando: ItemLoja | null = null;

  readonly categorias: Array<CategoriaLoja | 'Todos'> = ['Todos', 'Beneficio', 'Desenvolvimento', 'Conquista'];
  readonly opcoesCategoria: CategoriaLoja[] = ['Beneficio', 'Desenvolvimento', 'Conquista'];
  readonly opcoesIcone: IconeLoja[] = ['Presente', 'Estrela', 'Medalha', 'Raio', 'Coroa', 'Trofeu'];
  readonly opcoesCor: CorLoja[] = ['Azul', 'Roxo', 'Laranja', 'Rosa', 'Verde', 'Vermelho'];

  formulario: ItemLojaRequest = this.criarFormularioVazio();

  ngOnInit(): void {
    this.empresaId = this.authSessionService.obterIdEmpresa();
    this.usuarioEmpresaId = this.authSessionService.obterIdUsuarioEmpresa();
    this.formulario = this.criarFormularioVazio();

    if (!this.empresaId) {
      this.mensagemErro = 'Nao foi possivel identificar a empresa do usuario logado.';
      return;
    }

    this.carregarLoja();
    this.carregarSaldo();
  }

  get itensFiltrados(): ItemLoja[] {
    if (this.filtroCategoria === 'Todos') {
      return this.itens;
    }

    return this.itens.filter((item) => item.categoria === this.filtroCategoria);
  }

  get proximoNivel(): number {
    return Math.max(5000, Math.ceil((this.saldoXp + 1) / 5000) * 5000);
  }

  get progressoNivel(): number {
    return Math.min(100, Math.round((this.saldoXp / this.proximoNivel) * 100));
  }

  carregarLoja(): void {
    this.carregando = true;
    this.mensagemErro = '';

    this.lojaService
      .listarItens(this.empresaId, this.usuarioEmpresaId)
      .pipe(finalize(() => (this.carregando = false)))
      .subscribe({
        next: (itens) => (this.itens = itens),
        error: () => (this.mensagemErro = 'Nao foi possivel carregar as recompensas da loja.'),
      });
  }

  carregarSaldo(): void {
    if (!this.usuarioEmpresaId) {
      this.saldoXp = 0;
      return;
    }

    this.lojaService.buscarSaldoXp(this.usuarioEmpresaId).subscribe({
      next: (saldo) => (this.saldoXp = saldo),
      error: () => (this.saldoXp = 0),
    });
  }

  abrirNovoItem(): void {
    this.itemEditando = null;
    this.formulario = this.criarFormularioVazio();
    this.mostrarModal = true;
  }

  editarItem(item: ItemLoja): void {
    this.itemEditando = item;
    this.formulario = {
      idEmpresa: this.empresaId,
      nome: item.nome,
      descricao: item.descricao,
      custoXp: item.custoXp,
      ativa: item.ativa ?? true,
      quantidadeDisponivel: item.quantidadeDisponivel ?? 0,
      categoria: item.categoria ?? 'Beneficio',
      icone: item.icone ?? 'Presente',
      cor: item.cor ?? 'Azul',
    };
    this.mostrarModal = true;
  }

  fecharModal(): void {
    this.mostrarModal = false;
    this.itemEditando = null;
    this.formulario = this.criarFormularioVazio();
  }

  salvarItem(): void {
    if (!this.formulario.nome.trim()) {
      this.mensagemErro = 'Informe o nome da recompensa.';
      return;
    }

    this.salvando = true;
    this.mensagemErro = '';

    const request: ItemLojaRequest = {
      ...this.formulario,
      idEmpresa: this.empresaId,
      custoXp: Number(this.formulario.custoXp),
      quantidadeDisponivel: Number(this.formulario.quantidadeDisponivel ?? 0),
    };

    const operacao = this.itemEditando?.id
      ? this.lojaService.editarItem(this.itemEditando.id, request)
      : this.lojaService.criarItem(request);

    operacao.pipe(finalize(() => (this.salvando = false))).subscribe({
      next: () => {
        this.mensagemSucesso = this.itemEditando ? 'Recompensa atualizada.' : 'Recompensa criada.';
        this.fecharModal();
        this.carregarLoja();
      },
      error: () => (this.mensagemErro = 'Nao foi possivel salvar a recompensa.'),
    });
  }

  excluirItem(item: ItemLoja): void {
    if (!item.id) {
      return;
    }

    this.lojaService.deletarItem(item.id, this.empresaId).subscribe({
      next: () => {
        this.mensagemSucesso = 'Recompensa removida da loja.';
        this.carregarLoja();
      },
      error: () => (this.mensagemErro = 'Nao foi possivel excluir a recompensa.'),
    });
  }

  resgatarItem(item: ItemLoja): void {
    if (!item.id || this.estaEsgotado(item)) {
      return;
    }

    if (!this.usuarioEmpresaId) {
      this.mensagemErro = 'Nao foi possivel identificar o usuario logado para resgate.';
      return;
    }

    this.lojaService
      .resgatarItem(item.id, {
        idEmpresa: this.empresaId,
        idUsuarioEmpresa: this.usuarioEmpresaId,
        quantidade: 1,
      })
      .subscribe({
        next: () => {
          this.mensagemSucesso = `Voce resgatou ${item.nome}.`;
          this.carregarLoja();
          this.carregarSaldo();
        },
        error: (erro) => {
          this.mensagemErro = erro?.error?.message ?? 'Nao foi possivel resgatar esta recompensa.';
        },
      });
  }

  estaEsgotado(item: ItemLoja): boolean {
    return item.ativa === false || (item.quantidadeDisponivel ?? 0) <= 0;
  }

  labelCategoria(categoria?: string): string {
    const labels: Record<string, string> = {
      Beneficio: 'Beneficios',
      Desenvolvimento: 'Desenvolvimento',
      Conquista: 'Conquista',
      Todos: 'Todos',
    };

    return labels[categoria ?? ''] ?? 'Beneficios';
  }

  classeCor(cor?: string): string {
    return `cor-${(cor ?? 'Azul').toLowerCase()}`;
  }

  iconeBootstrap(icone?: string): string {
    const icones: Record<string, string> = {
      Presente: 'bi-gift',
      Estrela: 'bi-stars',
      Medalha: 'bi-award',
      Raio: 'bi-lightning-charge',
      Coroa: 'bi-gem',
      Trofeu: 'bi-trophy',
    };

    return icones[icone ?? 'Presente'] ?? 'bi-gift';
  }

  private criarFormularioVazio(): ItemLojaRequest {
    return {
      idEmpresa: this.empresaId,
      nome: '',
      descricao: '',
      custoXp: 500,
      ativa: true,
      quantidadeDisponivel: 0,
      categoria: 'Beneficio',
      icone: 'Presente',
      cor: 'Azul',
    };
  }
}
