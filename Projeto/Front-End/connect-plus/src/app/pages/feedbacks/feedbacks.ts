import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import {
  AbaFeedback,
  ColaboradorFeedback,
  Feedback360GestorItem,
  Feedback360Pendente,
  Feedback360ProjetoGestor,
  Feedback360Status,
  Feedback360UsuarioCard,
  FeedbackClassificacao,
  FeedbackFiltro,
  FeedbackItem,
  FeedbackResumo,
  FeedbacksService,
  ProjetoFeedback,
} from './feedbacks.service';

type ModalFeedback = 'novo' | 'avaliacao360' | 'observacao360' | 'obrigatoriedade' | null;

interface OpcaoClassificacao {
  label: string;
  value: FeedbackClassificacao;
}

interface FeedbackForm {
  destinatarioUsuarioEmpresaId: number | null;
  classificacao: FeedbackClassificacao;
  categoria: string;
  projetoId: number | null;
  comentario: string;
}

interface Feedback360Form {
  assiduidade: number;
  nivelEntregas: number;
  comunicacao: number;
  colaboracao: number;
  comprometimento: number;
  comentario: string;
}

interface ObrigatoriedadeForm {
  projetoId: number | null;
  obrigatoria: boolean;
}

interface Criterio360 {
  campo: keyof Pick<Feedback360Form, 'assiduidade' | 'nivelEntregas' | 'comunicacao' | 'colaboracao' | 'comprometimento'>;
  label: string;
}

@Component({
  selector: 'app-feedbacks',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './feedbacks.html',
  styleUrl: './feedbacks.css',
})
export class Feedbacks implements OnInit {
  feedbacks: FeedbackItem[] = [];
  colaboradores: ColaboradorFeedback[] = [];
  projetos: ProjetoFeedback[] = [];
  pendencias360: Feedback360Pendente[] = [];
  cardsUsuario360: Feedback360UsuarioCard[] = [];
  resumoGestor360: Feedback360ProjetoGestor[] = [];

  status360: Feedback360Status = this.status360Vazio();
  pendenciaSelecionada: Feedback360Pendente | null = null;
  indicePendenciaObrigatoria = 0;

  resumo: FeedbackResumo = {
    positivos: 0,
    medianos: 0,
    negativos: 0,
  };

  abaAtiva: AbaFeedback = 'projetos';
  filtroAtivo: FeedbackFiltro = 'todos';
  modal: ModalFeedback = null;

  carregando = false;
  carregando360 = false;
  carregandoGestor = false;
  salvando = false;
  erro = '';
  mensagemSucesso = '';
  termoBusca = '';
  filtroProjetoId: number | null = null;
  mostrarGestor = false;

  formFeedback: FeedbackForm = this.criarFormularioVazio();
  form360: Feedback360Form = this.criarFormulario360Vazio();
  formObrigatoriedade: ObrigatoriedadeForm = { projetoId: null, obrigatoria: true };
  observacaoProjeto = '';

  readonly notas360 = [1, 2, 3, 4, 5];
  readonly criterios360: Criterio360[] = [
    { campo: 'assiduidade', label: 'Assiduidade' },
    { campo: 'nivelEntregas', label: 'Nível de Entregas' },
    { campo: 'comunicacao', label: 'Comunicação' },
    { campo: 'colaboracao', label: 'Colaboração em Equipe' },
    { campo: 'comprometimento', label: 'Comprometimento' },
  ];

  readonly classificacoes: OpcaoClassificacao[] = [
    { label: 'Positivo', value: 'POSITIVO' },
    { label: 'Mediano', value: 'MEDIANO' },
    { label: 'Negativo', value: 'NEGATIVO' },
  ];

  readonly categorias: string[] = [
    'Trabalho em Equipe',
    'Qualidade Técnica',
    'Desenvolvimento',
    'Comunicação',
    'Liderança',
    'Produtividade',
    'Geral',
  ];

  constructor(
    private feedbacksService: FeedbacksService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      const aba = params.get('aba') || params.get('filtro');

      if (aba === 'avaliacao360' || aba === '360') {
        this.abaAtiva = '360';
        this.filtroAtivo = 'avaliacao360';
      }

      this.carregarTela();
    });
  }

  get usuarioEhGestor(): boolean {
    const usuario = this.feedbacksService.getUsuarioLogado();
    const cargo = `${usuario?.cargo ?? ''}`.toLowerCase();
    return cargo.includes('gestor') || cargo.includes('admin');
  }

  get nomeProjetoObrigatorio(): string {
    return this.status360.projetoNome || this.pendenciaSelecionada?.projetoNome || 'Projeto';
  }

  get pendenciasObrigatorias(): Feedback360Pendente[] {
    return this.pendencias360.filter((pendencia) => Boolean(pendencia.obrigatoria));
  }

  get pendenciaObrigatoriaAtual(): Feedback360Pendente | null {
    const pendencias = this.pendenciasObrigatorias;
    return pendencias[this.indicePendenciaObrigatoria] ?? null;
  }

  get totalPendenciasObrigatorias(): number {
    return this.pendenciasObrigatorias.length;
  }

  get etapaObrigatoriaAtual(): number {
    return Math.min(this.indicePendenciaObrigatoria + 1, Math.max(this.totalPendenciasObrigatorias, 1));
  }

  get exibirBloqueioObrigatorio(): boolean {
    return this.status360.bloqueiaSistema && this.totalPendenciasObrigatorias > 0 && this.modal !== 'observacao360';
  }

  get feedbacksFiltrados(): FeedbackItem[] {
    const termo = this.termoBusca.trim().toLowerCase();

    return this.feedbacks.filter((feedback) => {
      const eh360 = Boolean(feedback.avaliacao360);

      if (this.abaAtiva === 'projetos' && eh360) {
        return false;
      }

      if (this.abaAtiva === '360' && !eh360) {
        return false;
      }

      if (this.filtroProjetoId && feedback.projetoId !== this.filtroProjetoId) {
        return false;
      }

      if (!termo) {
        return true;
      }

      const texto = [
        feedback.autorNome,
        feedback.destinatarioNome,
        feedback.categoria,
        feedback.comentario,
        feedback.projetoNome,
      ]
        .join(' ')
        .toLowerCase();

      return texto.includes(termo);
    });
  }

  get feedbacksProjeto(): FeedbackItem[] {
    return this.feedbacks.filter((feedback) => !feedback.avaliacao360);
  }

  get feedbacks360(): FeedbackItem[] {
    return this.feedbacks.filter((feedback) => feedback.avaliacao360);
  }

  get totalAvaliacoes360(): number {
    return this.feedbacks360.length || this.cardsUsuario360.length;
  }

  get mediaGeral360(): string {
    const medias = [
      ...this.feedbacks360.map((feedback) => this.numero(feedback.media360 ?? feedback.nota)),
      ...this.resumoGestor360.map((projeto) => this.numero(projeto.mediaGeral)),
    ].filter((valor) => valor > 0);

    if (!medias.length) {
      return '0.0';
    }

    const media = medias.reduce((total, valor) => total + valor, 0) / medias.length;
    return media.toFixed(1);
  }

  carregarTela(): void {
    this.carregarProjetos();
    this.carregarColaboradores();
    this.carregarResumo();
    this.carregarFeedbacks();
    this.carregarStatus360();
    this.carregarPendencias360();
    this.carregarCardsUsuario360();

    if (this.usuarioEhGestor || this.mostrarGestor) {
      this.carregarResumoGestor360();
    }
  }

  carregarFeedbacks(): void {
    this.carregando = true;
    this.erro = '';

    this.feedbacksService.listar('todos').subscribe({
      next: (feedbacks) => {
        this.feedbacks = feedbacks;
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: (erro) => {
        console.error('Erro ao carregar feedbacks:', erro);
        this.erro = 'Não foi possível carregar os feedbacks.';
        this.carregando = false;
        this.cdr.detectChanges();
      },
    });
  }

  carregarResumo(): void {
    this.feedbacksService.buscarResumo().subscribe({
      next: (resumo) => {
        this.resumo = resumo;
        this.cdr.detectChanges();
      },
      error: (erro) => console.error('Erro ao carregar resumo dos feedbacks:', erro),
    });
  }

  carregarColaboradores(): void {
    this.feedbacksService.listarColaboradores().subscribe({
      next: (colaboradores) => {
        this.colaboradores = colaboradores;
        this.cdr.detectChanges();
      },
      error: (erro) => console.error('Erro ao carregar colaboradores:', erro),
    });
  }

  carregarProjetos(): void {
    this.feedbacksService.listarProjetos().subscribe({
      next: (projetos) => {
        this.projetos = projetos;
        this.cdr.detectChanges();
      },
      error: (erro) => console.error('Erro ao carregar projetos:', erro),
    });
  }

  carregarStatus360(): void {
    this.feedbacksService.buscarStatus360().subscribe({
      next: (status) => {
        this.status360 = status;
        this.cdr.detectChanges();
      },
      error: (erro) => console.error('Erro ao consultar status 360°:', erro),
    });
  }

  carregarPendencias360(): void {
    this.carregando360 = true;

    this.feedbacksService.listarPendentes360().subscribe({
      next: (pendencias) => {
        this.pendencias360 = pendencias ?? [];
        this.carregando360 = false;

        const atual = this.pendenciaObrigatoriaAtual;
        if (this.status360.bloqueiaSistema && atual) {
          this.pendenciaSelecionada = atual;
        }

        this.cdr.detectChanges();
      },
      error: (erro) => {
        console.error('Erro ao carregar avaliações 360° pendentes:', erro);
        this.carregando360 = false;
        this.cdr.detectChanges();
      },
    });
  }

  carregarCardsUsuario360(): void {
    this.feedbacksService.listarCardsUsuario360().subscribe({
      next: (cards) => {
        this.cardsUsuario360 = cards ?? [];
        this.cdr.detectChanges();
      },
      error: (erro) => console.error('Erro ao carregar cards 360° do usuário:', erro),
    });
  }

  carregarResumoGestor360(): void {
    this.carregandoGestor = true;
    this.mostrarGestor = true;

    this.feedbacksService.listarResumoGestor360().subscribe({
      next: (resumo) => {
        this.resumoGestor360 = resumo ?? [];
        this.carregandoGestor = false;
        this.cdr.detectChanges();
      },
      error: (erro) => {
        console.error('Erro ao carregar visão do gestor:', erro);
        this.carregandoGestor = false;
        this.cdr.detectChanges();
      },
    });
  }

  alternarAba(aba: AbaFeedback): void {
    this.abaAtiva = aba;
    this.filtroAtivo = aba === '360' ? 'avaliacao360' : 'todos';

    if (aba === '360') {
      this.carregarPendencias360();
      this.carregarCardsUsuario360();
    }
  }

  abrirNovoFeedback(): void {
    this.formFeedback = this.criarFormularioVazio();
    this.erro = '';
    this.mensagemSucesso = '';
    this.modal = 'novo';
  }

  abrirModalObrigatoriedade(): void {
    this.formObrigatoriedade = {
      projetoId: this.status360.projetoId ?? this.projetos[0]?.id ?? null,
      obrigatoria: true,
    };
    this.erro = '';
    this.modal = 'obrigatoriedade';
  }

  abrirAvaliacao360(pendencia: Feedback360Pendente): void {
    if (pendencia.vencido) {
      this.erro = 'O prazo desta avaliação 360° já foi encerrado.';
      return;
    }

    this.pendenciaSelecionada = pendencia;
    this.form360 = this.criarFormulario360Vazio();
    this.erro = '';
    this.mensagemSucesso = '';
    this.modal = 'avaliacao360';
  }

  fecharModal(): void {
    if (this.exibirBloqueioObrigatorio) {
      return;
    }

    this.modal = null;
    this.pendenciaSelecionada = this.pendenciaObrigatoriaAtual;
    this.formFeedback = this.criarFormularioVazio();
    this.form360 = this.criarFormulario360Vazio();
    this.observacaoProjeto = '';
    this.erro = '';
    this.salvando = false;
  }

  salvarFeedback(): void {
    if (!this.formFeedback.destinatarioUsuarioEmpresaId) {
      this.erro = 'Selecione o destinatário do feedback.';
      return;
    }

    if (!this.formFeedback.categoria.trim()) {
      this.erro = 'Informe a categoria do feedback.';
      return;
    }

    if (!this.formFeedback.comentario.trim()) {
      this.erro = 'Escreva a mensagem do feedback.';
      return;
    }

    this.salvando = true;
    this.erro = '';
    this.mensagemSucesso = '';

    this.feedbacksService
      .criar({
        destinatarioUsuarioEmpresaId: this.formFeedback.destinatarioUsuarioEmpresaId,
        classificacao: this.formFeedback.classificacao,
        categoria: this.formFeedback.categoria.trim(),
        comentario: this.formFeedback.comentario.trim(),
        avaliacao360: false,
        projetoId: this.formFeedback.projetoId,
      })
      .subscribe({
        next: () => {
          this.mensagemSucesso = 'Feedback cadastrado com sucesso.';
          this.modal = null;
          this.salvando = false;
          this.carregarResumo();
          this.carregarFeedbacks();
          this.cdr.detectChanges();
        },
        error: (erro) => {
          console.error('Erro ao salvar feedback:', erro);
          this.erro = this.mensagemErro(erro, 'Não foi possível salvar o feedback.');
          this.salvando = false;
          this.cdr.detectChanges();
        },
      });
  }

  salvarAvaliacao360Obrigatoria(): void {
    const pendencia = this.pendenciaObrigatoriaAtual;

    if (!pendencia) {
      this.abrirObservacao360();
      return;
    }

    this.pendenciaSelecionada = pendencia;
    this.salvarAvaliacao360(true);
  }

  salvarAvaliacao360(obrigatoria = false): void {
    if (!this.pendenciaSelecionada) {
      this.erro = 'Selecione uma avaliação 360° pendente.';
      return;
    }

    for (const criterio of this.criterios360) {
      if (!this.notaValida(this.form360[criterio.campo])) {
        this.erro = `Avalie ${criterio.label} de 1 a 5.`;
        return;
      }
    }

    this.salvando = true;
    this.erro = '';

    this.feedbacksService
      .criarAvaliacao360({
        avaliacaoId: this.pendenciaSelecionada.avaliacaoId,
        projetoId: this.pendenciaSelecionada.projetoId,
        destinatarioUsuarioEmpresaId: this.pendenciaSelecionada.destinatarioUsuarioEmpresaId,
        assiduidade: Number(this.form360.assiduidade),
        nivelEntregas: Number(this.form360.nivelEntregas),
        comunicacao: Number(this.form360.comunicacao),
        colaboracao: Number(this.form360.colaboracao),
        comprometimento: Number(this.form360.comprometimento),
        comentario: this.form360.comentario.trim(),
      })
      .subscribe({
        next: () => {
          this.salvando = false;
          this.mensagemSucesso = 'Avaliação 360° enviada com sucesso.';

          if (obrigatoria) {
            this.indicePendenciaObrigatoria++;
            this.form360 = this.criarFormulario360Vazio();

            if (this.indicePendenciaObrigatoria >= this.totalPendenciasObrigatorias) {
              this.abrirObservacao360();
            } else {
              this.pendenciaSelecionada = this.pendenciaObrigatoriaAtual;
            }
          } else {
            this.modal = null;
            this.pendenciaSelecionada = null;
          }

          this.carregarResumo();
          this.carregarFeedbacks();
          this.carregarPendencias360();
          this.carregarStatus360();
          this.cdr.detectChanges();
        },
        error: (erro) => {
          console.error('Erro ao salvar avaliação 360°:', erro);
          this.erro = this.mensagemErro(erro, 'Não foi possível salvar a avaliação 360°.');
          this.salvando = false;
          this.cdr.detectChanges();
        },
      });
  }

  abrirObservacao360(): void {
    this.modal = 'observacao360';
    this.observacaoProjeto = '';
    this.erro = '';
    this.cdr.detectChanges();
  }

  salvarObservacao360(): void {
    const rodadaId = this.status360.rodadaId ?? this.pendenciasObrigatorias[0]?.rodadaId ?? this.pendenciaSelecionada?.rodadaId;

    if (!rodadaId) {
      this.erro = 'Rodada 360° não encontrada.';
      return;
    }

    if (!this.observacaoProjeto.trim()) {
      this.erro = 'Escreva uma observação geral sobre o projeto.';
      return;
    }

    this.salvando = true;
    this.erro = '';

    this.feedbacksService.salvarObservacaoProjeto360(rodadaId, this.observacaoProjeto.trim()).subscribe({
      next: () => {
        this.mensagemSucesso = 'Avaliação 360° concluída. Acesso liberado.';
        this.status360 = this.status360Vazio();
        this.indicePendenciaObrigatoria = 0;
        this.pendenciaSelecionada = null;
        this.modal = null;
        this.salvando = false;
        this.observacaoProjeto = '';
        this.carregarTela();
        this.cdr.detectChanges();
      },
      error: (erro) => {
        console.error('Erro ao salvar observação do projeto:', erro);
        this.erro = this.mensagemErro(erro, 'Não foi possível salvar a observação do projeto.');
        this.salvando = false;
        this.cdr.detectChanges();
      },
    });
  }

  salvarObrigatoriedade(): void {
    if (!this.formObrigatoriedade.projetoId) {
      this.erro = 'Selecione um projeto.';
      return;
    }

    this.salvando = true;
    this.erro = '';

    this.feedbacksService
      .definirObrigatoriedadeProjeto360(this.formObrigatoriedade.projetoId, this.formObrigatoriedade.obrigatoria)
      .subscribe({
        next: () => {
          this.mensagemSucesso = this.formObrigatoriedade.obrigatoria
            ? 'Avaliação 360° obrigatória ativada para o projeto.'
            : 'Avaliação 360° obrigatória desativada para o projeto.';
          this.salvando = false;
          this.modal = null;
          this.carregarProjetos();
          this.carregarStatus360();
          this.cdr.detectChanges();
        },
        error: (erro) => {
          console.error('Erro ao definir obrigatoriedade:', erro);
          this.erro = this.mensagemErro(erro, 'Não foi possível alterar a obrigatoriedade.');
          this.salvando = false;
          this.cdr.detectChanges();
        },
      });
  }

  alternarProjetoGestor(projeto: Feedback360ProjetoGestor): void {
    projeto.aberto = !projeto.aberto;
  }

  selecionarNota(campo: Criterio360['campo'], nota: number): void {
    this.form360[campo] = nota;
  }

  textoNivel(nota: number): string {
    if (!nota) return 'Selecione uma nota';
    if (nota <= 1) return 'Muito baixo';
    if (nota === 2) return 'Baixo';
    if (nota === 3) return 'Regular';
    if (nota === 4) return 'Bom';
    return 'Excelente';
  }

  textoClassificacao(classificacao: FeedbackClassificacao | null): string {
    const textos: Record<FeedbackClassificacao, string> = {
      POSITIVO: 'Positivo',
      MEDIANO: 'Mediano',
      NEGATIVO: 'Negativo',
    };

    return classificacao ? textos[classificacao] ?? classificacao : 'Avaliação';
  }

  classeClassificacao(classificacao: FeedbackClassificacao | null): string {
    const classes: Record<FeedbackClassificacao, string> = {
      POSITIVO: 'positivo',
      MEDIANO: 'mediano',
      NEGATIVO: 'negativo',
    };

    return classificacao ? classes[classificacao] ?? 'mediano' : 'mediano';
  }

  iconeClassificacao(classificacao: FeedbackClassificacao | null): string {
    const icones: Record<FeedbackClassificacao, string> = {
      POSITIVO: 'thumb_up',
      MEDIANO: 'drag_handle',
      NEGATIVO: 'thumb_down',
    };

    return classificacao ? icones[classificacao] ?? 'feedback' : 'feedback';
  }

  media360(feedback: FeedbackItem): string {
    return this.formatarNumero(feedback.media360 ?? feedback.nota ?? 0);
  }

  estrelas(valor: number | null | undefined): number[] {
    const nota = Math.round(Number(valor ?? 0));
    return this.notas360.map((item) => (item <= nota ? 1 : 0));
  }

  nomeProjeto(idProjeto: number | null): string {
    if (!idProjeto) return 'Projeto relacionado';
    return this.projetos.find((projeto) => projeto.id === idProjeto)?.nome ?? 'Projeto relacionado';
  }

  iniciais(nome?: string | null): string {
    return this.feedbacksService.gerarIniciaisPublico(nome ?? 'Usuário');
  }

  trackByFeedback(_: number, feedback: FeedbackItem): number {
    return feedback.idFeedback;
  }

  trackByPendencia(_: number, pendencia: Feedback360Pendente): number {
    return pendencia.avaliacaoId;
  }

  trackByProjeto(_: number, projeto: Feedback360ProjetoGestor): number {
    return projeto.projetoId;
  }

  trackByAvaliado(_: number, avaliado: Feedback360GestorItem): number {
    return avaliado.avaliadoId;
  }

  private notaValida(nota: number | null): boolean {
    return nota !== null && Number(nota) >= 1 && Number(nota) <= 5;
  }

  private criarFormularioVazio(): FeedbackForm {
    return {
      destinatarioUsuarioEmpresaId: null,
      classificacao: 'POSITIVO',
      categoria: 'Trabalho em Equipe',
      projetoId: null,
      comentario: '',
    };
  }

  private criarFormulario360Vazio(): Feedback360Form {
    return {
      assiduidade: 0,
      nivelEntregas: 0,
      comunicacao: 0,
      colaboracao: 0,
      comprometimento: 0,
      comentario: '',
    };
  }

  private status360Vazio(): Feedback360Status {
    return {
      bloqueiaSistema: false,
      rodadaId: null,
      projetoId: null,
      projetoNome: null,
      obrigatoria: false,
      pendentes: 0,
    };
  }

  private formatarNumero(valor: number): string {
    return Number(valor || 0).toFixed(1).replace('.', '.');
  }

  private numero(valor: unknown): number {
    const numero = Number(valor ?? 0);
    return Number.isFinite(numero) ? numero : 0;
  }

  private mensagemErro(erro: any, fallback: string): string {
    return erro?.error?.message || erro?.error?.erro || erro?.error?.detail || fallback;
  }
}
