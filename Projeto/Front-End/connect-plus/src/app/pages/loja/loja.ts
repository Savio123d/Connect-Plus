import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import {
  CategoriaLoja,
  CorLoja,
  IconeLoja,
  ItemLoja,
  ItemLojaRequest,
  LojaService,
} from './loja.service';

import { SidebarComponent } from '../../components/sidebar/sidebar.component';

@Component({
  selector: 'app-loja',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, SidebarComponent],
  templateUrl: './loja.html',
  styleUrls: ['./loja.css'],
})
export class Loja implements OnInit {
  itensLoja: ItemLoja[] = [];

  empresaId = 1;
  usuarioEmpresaId = 1;

  saldoXp = 0;
  proximoNivelXp = 5000;

  carregando = false;
  salvando = false;
  mensagemErro = '';

  modalAberto = false;
  itemEditando: ItemLoja | null = null;

  filtroCategoria = 'Todas';
  busca = '';

  categorias: Array<'Todas' | CategoriaLoja> = [
    'Todas',
    'Benefício',
    'Desenvolvimento',
    'Conquista',
  ];

  icones: IconeLoja[] = ['Presente', 'Medalha', 'Raio', 'Coroa', 'Estrela', 'Troféu'];

  cores: CorLoja[] = ['Azul', 'Roxo', 'Laranja', 'Rosa', 'Verde', 'Vermelho'];

  formLoja!: FormGroup;

  constructor(
    private lojaService: LojaService,
    private formBuilder: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.criarFormulario();
    this.carregarTela();
  }

  criarFormulario(): void {
    this.formLoja = this.formBuilder.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      custoXp: [null, [Validators.required, Validators.min(1)]],
      quantidadeDisponivel: [1, [Validators.required, Validators.min(0)]],
      descricao: ['', [Validators.required, Validators.minLength(5)]],
      categoria: ['Benefício', Validators.required],
      icone: ['Presente', Validators.required],
      cor: ['Azul', Validators.required],
      ativa: [true],
    });
  }

  carregarTela(): void {
    this.carregarItens();
    this.carregarSaldoXp();
  }

  carregarItens(): void {
    this.carregando = true;
    this.mensagemErro = '';

    this.lojaService.listarItens(this.empresaId).subscribe({
      next: (itens) => {
        this.itensLoja = itens.map((item) => this.normalizarItem(item));
        this.carregando = false;
      },
      error: (erro) => {
        console.error('Erro ao carregar loja:', erro);
        this.mensagemErro = 'Não foi possível carregar os itens da loja.';
        this.carregando = false;
      },
    });
  }

  carregarSaldoXp(): void {
    this.lojaService.buscarSaldoXp(this.usuarioEmpresaId).subscribe({
      next: (saldo) => {
        this.saldoXp = saldo;
      },
      error: (erro) => {
        console.error('Erro ao carregar saldo XP:', erro);
        this.saldoXp = 0;
      },
    });
  }

  abrirModalCriacao(): void {
    this.itemEditando = null;

    this.formLoja.reset({
      nome: '',
      custoXp: null,
      quantidadeDisponivel: 1,
      descricao: '',
      categoria: 'Benefício',
      icone: 'Presente',
      cor: 'Azul',
      ativa: true,
    });

    this.modalAberto = true;
  }

  abrirModalEdicao(item: ItemLoja): void {
    this.itemEditando = item;

    this.formLoja.patchValue({
      nome: item.nome,
      custoXp: item.custoXp,
      quantidadeDisponivel: item.quantidadeDisponivel ?? 1,
      descricao: item.descricao,
      categoria: item.categoria ?? 'Benefício',
      icone: item.icone ?? 'Presente',
      cor: item.cor ?? 'Azul',
      ativa: item.ativa ?? true,
    });

    this.modalAberto = true;
  }

  fecharModal(): void {
    this.modalAberto = false;
    this.itemEditando = null;
    this.formLoja.reset();
  }

  salvarItem(): void {
    if (this.formLoja.invalid) {
      this.formLoja.markAllAsTouched();
      return;
    }

    const form = this.formLoja.getRawValue();

    const itemRequest: ItemLojaRequest = {
      idEmpresa: this.empresaId,
      nome: form.nome,
      descricao: form.descricao,
      custoXp: Number(form.custoXp),
      ativa: form.ativa ?? true,
      quantidadeDisponivel: Number(form.quantidadeDisponivel),
      categoria: form.categoria,
      icone: form.icone,
      cor: form.cor,
    };

    this.salvando = true;

    if (this.itemEditando) {
      const id = this.pegarId(this.itemEditando);

      this.lojaService.editarItem(id, itemRequest).subscribe({
        next: () => {
          this.salvando = false;
          this.fecharModal();
          this.carregarItens();
        },
        error: (erro) => {
          console.error('Erro ao editar item:', erro);
          this.mensagemErro = 'Não foi possível salvar as alterações.';
          this.salvando = false;
        },
      });

      return;
    }

    this.lojaService.criarItem(itemRequest).subscribe({
      next: () => {
        this.salvando = false;
        this.fecharModal();
        this.carregarItens();
      },
      error: (erro) => {
        console.error('Erro ao criar item:', erro);
        this.mensagemErro = 'Não foi possível criar o item da loja.';
        this.salvando = false;
      },
    });
  }

  deletarItem(item: ItemLoja): void {
    const confirmar = confirm(`Deseja excluir "${item.nome}" da loja?`);

    if (!confirmar) {
      return;
    }

    const id = this.pegarId(item);

    this.lojaService.deletarItem(id).subscribe({
      next: () => {
        this.carregarItens();
      },
      error: (erro) => {
        console.error('Erro ao deletar item:', erro);
        this.mensagemErro = 'Não foi possível deletar o item.';
      },
    });
  }

  esgotarItem(item: ItemLoja): void {
    const id = this.pegarId(item);

    this.lojaService.esgotarItem(id).subscribe({
      next: () => {
        this.carregarItens();
      },
      error: (erro) => {
        console.error('Erro ao esgotar item:', erro);
        this.mensagemErro = 'Não foi possível esgotar o item.';
      },
    });
  }

  reporItem(item: ItemLoja): void {
    const quantidade = Number(prompt('Digite a quantidade para repor:', '1'));

    if (!quantidade || quantidade < 1) {
      return;
    }

    const id = this.pegarId(item);

    this.lojaService.reporItem(id, quantidade).subscribe({
      next: () => {
        this.carregarItens();
      },
      error: (erro) => {
        console.error('Erro ao repor item:', erro);
        this.mensagemErro = 'Não foi possível repor o item.';
      },
    });
  }

  resgatarItem(item: ItemLoja): void {
    if (!this.podeResgatar(item)) {
      return;
    }

    const id = this.pegarId(item);

    this.lojaService.resgatarItem(id).subscribe({
      next: () => {
        this.carregarTela();
      },
      error: (erro) => {
        console.error('Erro ao resgatar item:', erro);
        this.mensagemErro = 'Não foi possível resgatar este item.';
      },
    });
  }

  get itensFiltrados(): ItemLoja[] {
    return this.itensLoja.filter((item) => {
      const categoriaItem = item.categoria ?? 'Benefício';

      const categoriaValida =
        this.filtroCategoria === 'Todas' || categoriaItem === this.filtroCategoria;

      const buscaFormatada = this.busca.trim().toLowerCase();

      const buscaValida =
        !buscaFormatada ||
        item.nome.toLowerCase().includes(buscaFormatada) ||
        item.descricao?.toLowerCase().includes(buscaFormatada);

      return categoriaValida && buscaValida;
    });
  }

  pegarId(item: ItemLoja): number {
    return item.idLoja ?? item.id ?? 0;
  }

  estaEsgotado(item: ItemLoja): boolean {
    return !item.ativa || (item.quantidadeDisponivel ?? 0) <= 0;
  }

  podeResgatar(item: ItemLoja): boolean {
    return (
      this.saldoXp >= item.custoXp &&
      Boolean(item.ativa) &&
      (item.quantidadeDisponivel ?? 0) > 0 &&
      !item.resgatada
    );
  }

  progressoXp(): number {
    return Math.min((this.saldoXp / this.proximoNivelXp) * 100, 100);
  }

  textoBotaoResgate(item: ItemLoja): string {
    if (item.resgatada) {
      return 'Resgatada';
    }

    if (this.estaEsgotado(item)) {
      return 'Esgotada';
    }

    if (this.saldoXp < item.custoXp) {
      return 'XP insuficiente';
    }

    return 'Resgatar';
  }

  classeCor(cor?: CorLoja): string {
    return `cor-${(cor ?? 'Azul').toLowerCase()}`;
  }

  iconeVisual(icone?: IconeLoja): string {
    const mapaIcones: Record<IconeLoja, string> = {
      Presente: '🎁',
      Medalha: '🏅',
      Raio: '⚡',
      Coroa: '👑',
      Estrela: '☆',
      Troféu: '🏆',
    };

    return mapaIcones[icone ?? 'Presente'];
  }

  campoInvalido(campo: string): boolean {
    const controle = this.formLoja.get(campo);
    return !!controle && controle.invalid && controle.touched;
  }

  normalizarItem(item: ItemLoja): ItemLoja {
    return {
      ...item,
      quantidadeDisponivel: item.quantidadeDisponivel ?? (item.ativa ? 1 : 0),
      categoria: item.categoria ?? 'Benefício',
      icone: item.icone ?? 'Presente',
      cor: item.cor ?? 'Azul',
      resgatada: item.resgatada ?? false,
    };
  }
}
