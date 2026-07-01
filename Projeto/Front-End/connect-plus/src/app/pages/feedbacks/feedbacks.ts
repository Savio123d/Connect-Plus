import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import {
  ColaboradorFeedback,
  Feedback360Pendente,
  FeedbackClassificacao,
  FeedbackFiltro,
  FeedbackItem,
  FeedbackResumo,
  FeedbacksService,
} from './feedbacks.service';

type ModalFeedback = 'novo' | 'avaliacao360' | null;

interface OpcaoFiltro {
  label: string;
  value: FeedbackFiltro;
}

interface OpcaoClassificacao {
  label: string;
  value: FeedbackClassificacao;
}

interface FeedbackForm {
  destinatarioUsuarioEmpresaId: number | null;
  classificacao: FeedbackClassificacao | '';
  categoria: string;
  comentario: string;
}

interface Feedback360Form {
  comprometimento: number | null;
  nivelEntregas: number | null;
  colaboracao: number | null;
  comunicacao: number | null;
  comentario: string;
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
  pendencias360: Feedback360Pendente[] = [];
  pendenciaSelecionada: Feedback360Pendente | null = null;

  resumo: FeedbackResumo = {
    positivos: 0,
    medianos: 0,
    negativos: 0,
  };

  filtroAtivo: FeedbackFiltro = 'todos';
  modal: ModalFeedback = null;

  carregando = false;
  carregando360 = false;
  salvando = false;
  erro = '';
  mensagemSucesso = '';

  formFeedback: FeedbackForm = this.criarFormularioVazio();
  form360: Feedback360Form = this.criarFormulario360Vazio();

  filtros: OpcaoFiltro[] = [
    { label: 'Todos', value: 'todos' },
    { label: 'Avaliação 360°', value: 'avaliacao360' },
    { label: 'Positivos', value: 'positivos' },
    { label: 'Medianos', value: 'medianos' },
    { label: 'Negativos', value: 'negativos' },
  ];

  classificacoes: OpcaoClassificacao[] = [
    { label: 'Positivo', value: 'POSITIVO' },
    { label: 'Mediano', value: 'MEDIANO' },
    { label: 'Negativo', value: 'NEGATIVO' },
  ];

  categorias: string[] = [
    'Trabalho em Equipe',
    'Qualidade Técnica',
    'Desenvolvimento',
    'Comunicação',
    'Liderança',
    'Produtividade',
    'Geral',
  ];

  notas360 = [1, 2, 3, 4, 5];

  constructor(
    private feedbacksService: FeedbacksService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      const aba = params.get('aba') || params.get('filtro');

      if (aba === 'avaliacao360') {
        this.filtroAtivo = 'avaliacao360';
      }

      this.carregarTela();
    });
  }

  carregarTela(): void {
    this.carregarResumo();
    this.carregarColaboradores();
    this.carregarFeedbacks();

    if (this.filtroAtivo === 'avaliacao360') {
      this.carregarPendencias360();
    }
  }

  carregarFeedbacks(): void {
    this.carregando = true;
    this.erro = '';

    this.feedbacksService.listar(this.filtroAtivo).subscribe({
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
      error: (erro) => {
        console.error('Erro ao carregar resumo dos feedbacks:', erro);
      },
    });
  }

  carregarColaboradores(): void {
    this.feedbacksService.listarColaboradores().subscribe({
      next: (colaboradores) => {
        this.colaboradores = colaboradores;
        this.cdr.detectChanges();
      },
      error: (erro) => {
        console.error('Erro ao carregar colaboradores:', erro);
      },
    });
  }

  carregarPendencias360(): void {
    this.carregando360 = true;

    this.feedbacksService.listarPendentes360().subscribe({
      next: (pendencias) => {
        this.pendencias360 = pendencias;
        this.carregando360 = false;
        this.cdr.detectChanges();
      },
      error: (erro) => {
        console.error('Erro ao carregar avaliações 360° pendentes:', erro);
        this.carregando360 = false;
        this.cdr.detectChanges();
      },
    });
  }

  alterarFiltro(filtro: FeedbackFiltro): void {
    this.filtroAtivo = filtro;
    this.carregarFeedbacks();

    if (filtro === 'avaliacao360') {
      this.carregarPendencias360();
    }
  }

  abrirNovoFeedback(): void {
    this.formFeedback = this.criarFormularioVazio();
    this.erro = '';
    this.mensagemSucesso = '';
    this.modal = 'novo';
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
    this.modal = null;
    this.pendenciaSelecionada = null;
    this.formFeedback = this.criarFormularioVazio();
    this.form360 = this.criarFormulario360Vazio();
    this.erro = '';
    this.salvando = false;
  }

  salvarFeedback(): void {
    if (!this.formFeedback.destinatarioUsuarioEmpresaId) {
      this.erro = 'Selecione o destinatário do feedback.';
      return;
    }

    if (!this.formFeedback.classificacao) {
      this.erro = 'Selecione a classificação do feedback.';
      return;
    }

    if (!this.formFeedback.categoria.trim()) {
      this.erro = 'Informe a categoria do feedback.';
      return;
    }

    if (!this.formFeedback.comentario.trim()) {
      this.erro = 'Escreva a descrição do feedback.';
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
      })
      .subscribe({
        next: () => {
          this.mensagemSucesso = 'Feedback cadastrado com sucesso.';
          this.fecharModal();
          this.carregarResumo();
          this.carregarFeedbacks();
        },
        error: (erro) => {
          console.error('Erro ao salvar feedback:', erro);
          this.erro = erro?.error?.message || erro?.error?.erro || 'Não foi possível salvar o feedback.';
          this.salvando = false;
          this.cdr.detectChanges();
        },
      });
  }

  salvarAvaliacao360(): void {
    if (!this.pendenciaSelecionada) {
      this.erro = 'Selecione uma avaliação 360° pendente.';
      return;
    }

    if (!this.notaValida(this.form360.comprometimento)) {
      this.erro = 'Avalie Comprometimento de 1 a 5.';
      return;
    }

    if (!this.notaValida(this.form360.nivelEntregas)) {
      this.erro = 'Avalie Nível de Entregas de 1 a 5.';
      return;
    }

    if (!this.notaValida(this.form360.colaboracao)) {
      this.erro = 'Avalie Colaboração de 1 a 5.';
      return;
    }

    if (!this.notaValida(this.form360.comunicacao)) {
      this.erro = 'Avalie Comunicação de 1 a 5.';
      return;
    }

    this.salvando = true;
    this.erro = '';

    this.feedbacksService
      .criarAvaliacao360({
        projetoId: this.pendenciaSelecionada.projetoId,
        destinatarioUsuarioEmpresaId: this.pendenciaSelecionada.destinatarioUsuarioEmpresaId,
        comprometimento: Number(this.form360.comprometimento),
        nivelEntregas: Number(this.form360.nivelEntregas),
        colaboracao: Number(this.form360.colaboracao),
        comunicacao: Number(this.form360.comunicacao),
        comentario: this.form360.comentario.trim(),
      })
      .subscribe({
        next: () => {
          this.mensagemSucesso = 'Avaliação 360° enviada com sucesso.';
          this.fecharModal();
          this.filtroAtivo = 'avaliacao360';
          this.carregarResumo();
          this.carregarFeedbacks();
          this.carregarPendencias360();
        },
        error: (erro) => {
          console.error('Erro ao salvar avaliação 360°:', erro);
          this.erro = erro?.error?.message || erro?.error?.erro || 'Não foi possível salvar a avaliação 360°.';
          this.salvando = false;
          this.cdr.detectChanges();
        },
      });
  }

  textoClassificacao(classificacao: FeedbackClassificacao): string {
    const textos: Record<FeedbackClassificacao, string> = {
      POSITIVO: 'Positivo',
      MEDIANO: 'Mediano',
      NEGATIVO: 'Negativo',
    };

    return textos[classificacao] ?? classificacao;
  }

  classeClassificacao(classificacao: FeedbackClassificacao): string {
    const classes: Record<FeedbackClassificacao, string> = {
      POSITIVO: 'positivo',
      MEDIANO: 'mediano',
      NEGATIVO: 'negativo',
    };

    return classes[classificacao] ?? 'mediano';
  }

  iconeClassificacao(classificacao: FeedbackClassificacao): string {
    const icones: Record<FeedbackClassificacao, string> = {
      POSITIVO: 'thumb_up',
      MEDIANO: 'drag_handle',
      NEGATIVO: 'thumb_down',
    };

    return icones[classificacao] ?? 'feedback';
  }

  textoPrazo360(pendencia: Feedback360Pendente): string {
    if (pendencia.vencido) {
      return 'Prazo encerrado';
    }

    if (pendencia.diasRestantes === 0) {
      return 'Vence hoje';
    }

    if (pendencia.diasRestantes === 1) {
      return 'Vence amanhã';
    }

    return `${pendencia.diasRestantes} dias restantes`;
  }

  media360(feedback: FeedbackItem): string {
    if (feedback.media360 === null || feedback.media360 === undefined) {
      return '-';
    }

    return Number(feedback.media360).toFixed(1).replace('.', ',');
  }

  trackByFeedback(_: number, feedback: FeedbackItem): number {
    return feedback.idFeedback;
  }

  trackByPendencia(_: number, pendencia: Feedback360Pendente): string {
    return `${pendencia.projetoId}-${pendencia.destinatarioUsuarioEmpresaId}`;
  }

  private notaValida(nota: number | null): boolean {
    return nota !== null && Number(nota) >= 1 && Number(nota) <= 5;
  }

  private criarFormularioVazio(): FeedbackForm {
    return {
      destinatarioUsuarioEmpresaId: null,
      classificacao: '',
      categoria: 'Trabalho em Equipe',
      comentario: '',
    };
  }

  private criarFormulario360Vazio(): Feedback360Form {
    return {
      comprometimento: null,
      nivelEntregas: null,
      colaboracao: null,
      comunicacao: null,
      comentario: '',
    };
  }
}
