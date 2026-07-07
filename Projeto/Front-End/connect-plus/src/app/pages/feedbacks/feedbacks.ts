import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  AbaFeedback,
  ColaboradorFeedback,
  Feedback360Pendente,
  Feedback360ProjetoGestor,
  Feedback360UsuarioCard,
  FeedbackItem,
  FeedbackResumo,
  FeedbacksService,
  ProjetoFeedback,
} from './feedbacks.service';

type ModalFeedback = null | 'novo' | 'avaliacao360' | 'observacao360' | 'obrigatoriedade';
type Campo360 = 'assiduidade' | 'nivelEntregas' | 'comunicacao' | 'colaboracao' | 'comprometimento';

interface Criterio360 {
  campo: Campo360;
  label: string;
}

interface Form360 extends Record<Campo360, number> {
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
  abaAtiva: AbaFeedback = '360';
  modal: ModalFeedback = null;

  carregando = false;
  carregandoGestor = false;
  salvando = false;

  erro = '';
  mensagemSucesso = '';

  termoBusca = '';
  filtroProjetoId: number | null = null;

  resumo: FeedbackResumo = {
    positivos: 0,
    medianos: 0,
    negativos: 0,
  };

  feedbacksProjeto: FeedbackItem[] = [];
  pendencias360: Feedback360Pendente[] = [];
  pendenciasObrigatorias: Feedback360Pendente[] = [];
  cardsUsuario360: Feedback360UsuarioCard[] = [];
  resumoGestor360: Feedback360ProjetoGestor[] = [];

  colaboradores: ColaboradorFeedback[] = [];
  projetos: ProjetoFeedback[] = [];

  usuarioEhGestor = false;
  mostrarGestor = false;

  status360 = {
    bloqueiaSistema: false,
    rodadaId: null as number | null,
    projetoId: null as number | null,
    projetoNome: null as string | null,
    obrigatoria: false,
    pendentes: 0,
  };

  pendenciaSelecionada: Feedback360Pendente | null = null;
  indicePendenciaObrigatoria = 0;
  nomeProjetoObrigatorio = '';
  observacaoProjeto = '';
  rodadaObservacaoAtualId: number | null = null;

  categorias = ['Reconhecimento', 'Melhoria', 'Comunicação', 'Entrega', 'Comportamento'];
  notas360 = [1, 2, 3, 4, 5];

  criterios360: Criterio360[] = [
    { campo: 'assiduidade', label: 'Assiduidade' },
    { campo: 'nivelEntregas', label: 'Nível de entregas' },
    { campo: 'comunicacao', label: 'Comunicação' },
    { campo: 'colaboracao', label: 'Colaboração em equipe' },
    { campo: 'comprometimento', label: 'Comprometimento' },
  ];

  formFeedback = {
    destinatarioUsuarioEmpresaId: null as number | null,
    categoria: 'Reconhecimento',
    comentario: '',
    projetoId: null as number | null,
    tarefaId: null as number | null,
  };

  formObrigatoriedade = {
    projetoId: null as number | null,
    obrigatoria: true,
  };

  form360: Form360 = this.criarForm360();

  constructor(private feedbacksService: FeedbacksService) {}

  ngOnInit(): void {
    this.carregarUsuarioLogado();
    this.carregarDadosIniciais();
  }

  get exibirBloqueioObrigatorio(): boolean {
    return (
      this.status360.bloqueiaSistema &&
      this.pendenciasObrigatorias.length > 0 &&
      this.modal !== 'observacao360'
    );
  }

  get pendenciaObrigatoriaAtual(): Feedback360Pendente | null {
    return this.pendenciasObrigatorias[this.indicePendenciaObrigatoria] ?? null;
  }

  get etapaObrigatoriaAtual(): number {
    return this.pendenciasObrigatorias.length ? this.indicePendenciaObrigatoria + 1 : 0;
  }

  get totalPendenciasObrigatorias(): number {
    return this.pendenciasObrigatorias.length;
  }

  get totalAvaliacoes360(): number {
    return this.cardsUsuario360.length;
  }

  get mediaGeral360(): string {
    if (!this.resumoGestor360.length) {
      return '0.0';
    }

    const soma = this.resumoGestor360.reduce((total, projeto) => total + Number(projeto.mediaGeral ?? 0), 0);
    return (soma / this.resumoGestor360.length).toFixed(1);
  }

  get feedbacksFiltrados(): FeedbackItem[] {
    const termo = this.termoBusca.trim().toLowerCase();

    return this.feedbacksProjeto.filter((feedback) => {
      const bateProjeto = !this.filtroProjetoId || feedback.projetoId === this.filtroProjetoId;
      const texto = `${feedback.autorNome ?? ''} ${feedback.destinatarioNome ?? ''} ${feedback.projetoNome ?? ''} ${feedback.categoria ?? ''} ${feedback.comentario ?? ''}`.toLowerCase();
      const bateBusca = !termo || texto.includes(termo);

      return bateProjeto && bateBusca;
    });
  }

  carregarDadosIniciais(): void {
    this.carregarResumo();
    this.carregarFeedbacks();
    this.carregarColaboradores();
    this.carregarProjetos();
    this.carregarPendencias360();
    this.carregarCardsUsuario360();
    this.carregarStatus360();

    if (this.usuarioEhGestor) {
      this.carregarResumoGestor360();
    }
  }

  carregarUsuarioLogado(): void {
    const usuario = this.feedbacksService.getUsuarioLogado() as any;
    const papel = String(usuario?.papel ?? usuario?.cargo ?? '').toLowerCase();

    this.usuarioEhGestor = papel === 'gestor';
    this.mostrarGestor = this.usuarioEhGestor;
  }

  carregarResumo(): void {
    this.feedbacksService.buscarResumo().subscribe({
      next: (resumo) => {
        this.resumo = resumo;
      },
      error: () => {
        this.resumo = { positivos: 0, medianos: 0, negativos: 0 };
      },
    });
  }

  carregarFeedbacks(): void {
    this.carregando = true;

    this.feedbacksService.listar('todos').subscribe({
      next: (feedbacks) => {
        this.feedbacksProjeto = feedbacks ?? [];
        this.carregando = false;
      },
      error: (erro) => {
        console.error('Erro ao carregar feedbacks:', erro);
        this.feedbacksProjeto = [];
        this.carregando = false;
      },
    });
  }

  carregarColaboradores(): void {
    this.feedbacksService.listarColaboradores().subscribe({
      next: (colaboradores) => {
        this.colaboradores = colaboradores ?? [];
      },
      error: (erro) => {
        console.error('Erro ao carregar colaboradores:', erro);
        this.colaboradores = [];
      },
    });
  }

  carregarProjetos(): void {
    this.feedbacksService.listarProjetos().subscribe({
      next: (projetos) => {
        this.projetos = projetos ?? [];
      },
      error: (erro) => {
        console.error('Erro ao carregar projetos:', erro);
        this.projetos = [];
      },
    });
  }

  carregarPendencias360(): void {
    this.feedbacksService.listarPendentes360().subscribe({
      next: (pendencias) => {
        this.pendencias360 = pendencias ?? [];
        this.pendenciasObrigatorias = this.pendencias360.filter((pendencia) => pendencia.obrigatoria);

        if (this.indicePendenciaObrigatoria >= this.pendenciasObrigatorias.length) {
          this.indicePendenciaObrigatoria = 0;
        }

        const atual = this.pendenciaObrigatoriaAtual;
        if (atual) {
          this.nomeProjetoObrigatorio = atual.projetoNome;
          this.rodadaObservacaoAtualId = atual.rodadaId;
        }
      },
      error: (erro) => {
        console.error('Erro ao carregar pendências 360:', erro);
        this.pendencias360 = [];
        this.pendenciasObrigatorias = [];
      },
    });
  }

  carregarCardsUsuario360(): void {
    this.feedbacksService.listarCardsUsuario360().subscribe({
      next: (cards) => {
        this.cardsUsuario360 = cards ?? [];
        console.log('cardsUsuario360:', this.cardsUsuario360);
      },
      error: (erro) => {
        console.error('Erro ao carregar cards 360 do usuário:', erro);
        this.cardsUsuario360 = [];
      },
    });
  }

  carregarResumoGestor360(): void {
    if (!this.usuarioEhGestor) {
      this.resumoGestor360 = [];
      return;
    }

    this.carregandoGestor = true;

    this.feedbacksService.listarResumoGestor360().subscribe({
      next: (resumo) => {
        this.resumoGestor360 = resumo ?? [];
        this.carregandoGestor = false;
      },
      error: (erro) => {
        console.error('Erro ao carregar resumo 360 do gestor:', erro);
        this.resumoGestor360 = [];
        this.carregandoGestor = false;
      },
    });
  }

  carregarStatus360(): void {
    this.feedbacksService.buscarStatus360().subscribe({
      next: (status) => {
        this.status360 = status;
      },
      error: (erro) => {
        console.error('Erro ao carregar status 360:', erro);
        this.status360 = {
          bloqueiaSistema: false,
          rodadaId: null,
          projetoId: null,
          projetoNome: null,
          obrigatoria: false,
          pendentes: 0,
        };
      },
    });
  }

  alternarAba(aba: AbaFeedback): void {
    this.abaAtiva = aba;
    this.erro = '';
    this.mensagemSucesso = '';

    if (aba === '360') {
      this.carregarPendencias360();
      this.carregarCardsUsuario360();
      this.carregarStatus360();
    }

    if (aba === 'projetos' && this.usuarioEhGestor) {
      this.carregarResumoGestor360();
    }
  }

  abrirNovoFeedback(): void {
    this.erro = '';
    this.mensagemSucesso = '';
    this.formFeedback = {
      destinatarioUsuarioEmpresaId: null,
      categoria: 'Reconhecimento',
      comentario: '',
      projetoId: null,
      tarefaId: null,
    };
    this.modal = 'novo';
  }

  abrirModalObrigatoriedade(): void {
    this.erro = '';
    this.mensagemSucesso = '';
    this.formObrigatoriedade = {
      projetoId: null,
      obrigatoria: true,
    };
    this.modal = 'obrigatoriedade';
  }

  abrirAvaliacao360(pendencia: Feedback360Pendente): void {
    this.erro = '';
    this.mensagemSucesso = '';
    this.pendenciaSelecionada = pendencia;
    this.form360 = this.criarForm360();
    this.modal = 'avaliacao360';
  }

  fecharModal(): void {
    if (this.exibirBloqueioObrigatorio && this.modal !== 'observacao360') {
      return;
    }

    this.modal = null;
    this.erro = '';
  }

  salvarFeedback(): void {
    if (!this.formFeedback.destinatarioUsuarioEmpresaId) {
      this.erro = 'Selecione para quem o feedback será enviado.';
      return;
    }

    if (!this.formFeedback.comentario.trim()) {
      this.erro = 'Escreva a mensagem do feedback.';
      return;
    }

    this.salvando = true;
    this.erro = '';

    this.feedbacksService
      .criar({
        destinatarioUsuarioEmpresaId: this.formFeedback.destinatarioUsuarioEmpresaId,
        classificacao: 'POSITIVO',
        categoria: this.formFeedback.categoria,
        comentario: this.formFeedback.comentario.trim(),
        avaliacao360: false,
        projetoId: this.formFeedback.projetoId,
        tarefaId: this.formFeedback.tarefaId,
      })
      .subscribe({
        next: () => {
          this.salvando = false;
          this.modal = null;
          this.mensagemSucesso = 'Feedback enviado com sucesso.';
          this.carregarFeedbacks();
          this.carregarResumo();
        },
        error: (erro) => {
          console.error('Erro ao salvar feedback:', erro);
          this.salvando = false;
          this.erro = this.extrairMensagemErro(erro, 'Não foi possível enviar o feedback.');
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
      .definirObrigatoriedadeProjeto360(
        this.formObrigatoriedade.projetoId,
        this.formObrigatoriedade.obrigatoria,
      )
      .subscribe({
        next: () => {
          this.salvando = false;
          this.modal = null;
          this.mensagemSucesso = this.formObrigatoriedade.obrigatoria
            ? 'Avaliação 360° configurada como obrigatória.'
            : 'Avaliação 360° configurada como opcional.';
          this.carregarStatus360();
        },
        error: (erro) => {
          console.error('Erro ao salvar obrigatoriedade:', erro);
          this.salvando = false;
          this.erro = this.extrairMensagemErro(erro, 'Não foi possível salvar a configuração.');
        },
      });
  }

  salvarAvaliacao360Obrigatoria(): void {
    this.salvarAvaliacao360(true);
  }

  salvarAvaliacao360(obrigatoria = false): void {
    const pendencia = obrigatoria ? this.pendenciaObrigatoriaAtual : this.pendenciaSelecionada;

    if (!pendencia) {
      this.erro = 'Avaliação 360° pendente não encontrada.';
      return;
    }

    if (!this.formulario360Valido()) {
      this.erro = 'Preencha todas as notas de 1 a 5 estrelas.';
      return;
    }

    this.salvando = true;
    this.erro = '';

    this.feedbacksService
      .criarAvaliacao360({
        avaliacaoId: pendencia.avaliacaoId,
        projetoId: pendencia.projetoId,
        destinatarioUsuarioEmpresaId: pendencia.destinatarioUsuarioEmpresaId,
        assiduidade: this.form360.assiduidade,
        nivelEntregas: this.form360.nivelEntregas,
        comunicacao: this.form360.comunicacao,
        colaboracao: this.form360.colaboracao,
        comprometimento: this.form360.comprometimento,
        comentario: this.form360.comentario?.trim() ?? '',
      })
      .subscribe({
        next: () => {
          this.salvando = false;
          this.aposSalvarAvaliacao360(pendencia, obrigatoria);
        },
        error: (erro) => {
          console.error('Erro ao salvar avaliação 360:', erro);
          this.salvando = false;
          this.erro = this.extrairMensagemErro(erro, 'Não foi possível salvar a avaliação 360°.');
        },
      });
  }

  salvarObservacao360(): void {
    const rodadaId = this.rodadaObservacaoAtualId ?? this.status360.rodadaId;

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
        this.salvando = false;
        this.modal = null;
        this.observacaoProjeto = '';
        this.mensagemSucesso = 'Avaliação 360° concluída com sucesso.';

        this.pendenciasObrigatorias = [];
        this.pendencias360 = [];
        this.status360 = {
          bloqueiaSistema: false,
          rodadaId: null,
          projetoId: null,
          projetoNome: null,
          obrigatoria: false,
          pendentes: 0,
        };

        this.abaAtiva = '360';
        this.carregarPendencias360();
        this.carregarCardsUsuario360();
        this.carregarStatus360();

        if (this.usuarioEhGestor) {
          this.carregarResumoGestor360();
        }
      },
      error: (erro) => {
        console.error('Erro ao salvar observação 360:', erro);
        this.salvando = false;
        this.erro = this.extrairMensagemErro(erro, 'Não foi possível salvar a observação do projeto.');
      },
    });
  }

  selecionarNota(campo: Campo360, nota: number): void {
    this.form360[campo] = nota;
  }

  textoNivel(nota: number): string {
    switch (nota) {
      case 1:
        return 'Muito baixo';
      case 2:
        return 'Baixo';
      case 3:
        return 'Médio';
      case 4:
        return 'Bom';
      case 5:
        return 'Excelente';
      default:
        return 'Selecione uma nota';
    }
  }

  estrelas(media: number): boolean[] {
    const valor = Math.round(Number(media ?? 0));
    return [1, 2, 3, 4, 5].map((estrela) => estrela <= valor);
  }

  alternarProjetoGestor(projeto: Feedback360ProjetoGestor): void {
    projeto.aberto = !projeto.aberto;
  }

  iniciais(nome?: string | null): string {
    return this.feedbacksService.gerarIniciaisPublico(nome || 'Usuário');
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

  trackByAvaliado(_: number, avaliado: { avaliadoId: number }): number {
    return avaliado.avaliadoId;
  }

  private aposSalvarAvaliacao360(pendencia: Feedback360Pendente, obrigatoria: boolean): void {
    this.mensagemSucesso = 'Avaliação 360° salva com sucesso.';

    this.pendencias360 = this.pendencias360.filter((item) => item.avaliacaoId !== pendencia.avaliacaoId);
    this.pendenciasObrigatorias = this.pendenciasObrigatorias.filter((item) => item.avaliacaoId !== pendencia.avaliacaoId);

    this.rodadaObservacaoAtualId = pendencia.rodadaId;
    this.nomeProjetoObrigatorio = pendencia.projetoNome;

    if (obrigatoria) {
      if (this.indicePendenciaObrigatoria >= this.pendenciasObrigatorias.length) {
        this.indicePendenciaObrigatoria = 0;
      }

      if (this.pendenciasObrigatorias.length > 0) {
        this.form360 = this.criarForm360();
        return;
      }

      this.form360 = this.criarForm360();
      this.observacaoProjeto = '';
      this.modal = 'observacao360';
      return;
    }

    this.modal = null;
    this.form360 = this.criarForm360();
    this.pendenciaSelecionada = null;
    this.abaAtiva = '360';

    this.carregarPendencias360();
    this.carregarCardsUsuario360();
    this.carregarStatus360();

    if (this.usuarioEhGestor) {
      this.carregarResumoGestor360();
    }
  }

  private formulario360Valido(): boolean {
    return this.criterios360.every((criterio) => {
      const nota = this.form360[criterio.campo];
      return nota >= 1 && nota <= 5;
    });
  }

  private criarForm360(): Form360 {
    return {
      assiduidade: 0,
      nivelEntregas: 0,
      comunicacao: 0,
      colaboracao: 0,
      comprometimento: 0,
      comentario: '',
    };
  }

  private extrairMensagemErro(erro: any, padrao: string): string {
    return erro?.error?.message || erro?.message || padrao;
  }
}
