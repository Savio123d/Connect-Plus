import { AreaChartBuilder, AreaChartOptions } from '../builders/area-chart.builder';
import { BarChartBuilder, BarChartOptions } from '../builders/bar-chart.builder';
import { DashboardCard, DashboardResumo, DesempenhoEquipe } from '../models/dashboard.model';

export interface DashboardViewModel {
  cards: DashboardCard[];
  areaChartOptions?: AreaChartOptions;
  barChartOptions?: BarChartOptions;
}

export class DashboardFactory {
  private static meses = [
    'Jan',
    'Fev',
    'Mar',
    'Abr',
    'Mai',
    'Jun',
    'Jul',
    'Ago',
    'Set',
    'Out',
    'Nov',
    'Dez',
  ];

  static criarDashboard(resumo: DashboardResumo): DashboardViewModel {
    const ocultarGraficos = this.temItemExcluidoNoHistorico(resumo);

    return {
      cards: this.criarCards(resumo),
      areaChartOptions: ocultarGraficos ? undefined : this.criarGraficoDesempenho(resumo),
      barChartOptions: ocultarGraficos ? undefined : this.criarGraficoStatusTarefas(resumo),
    };
  }

  static criarCards(resumo: DashboardResumo): DashboardCard[] {
    return [
      {
        titulo: 'Usuarios Ativos',
        valor: resumo.usuariosAtivos ?? 0,
        icone: 'people',
        iconeClasse: 'icone-usuarios',
      },
      {
        titulo: 'Projetos Ativos',
        valor: resumo.projetosAtivos ?? 0,
        icone: 'folder',
        iconeClasse: 'icone-projetos',
      },
      {
        titulo: 'Tarefas Concluidas',
        valor: resumo.tarefasConcluidas ?? 0,
        icone: 'check_circle',
        iconeClasse: 'icone-tarefas',
      },
      {
        titulo: 'Feedbacks',
        valor: resumo.feedbacks ?? 0,
        icone: 'chat_bubble',
        iconeClasse: 'icone-feedbacks',
      },
    ];
  }

  static criarGraficoDesempenho(resumo: DashboardResumo): AreaChartOptions | undefined {
    const historico = resumo.desempenhoEquipe ?? resumo.tarefasConcluidasPorMes ?? [];
    const desempenhoPorMes = Array<number>(12).fill(0);

    historico.forEach((item) => {
      const indiceMes = item.mes - 1;

      if (indiceMes >= 0 && indiceMes < 12) {
        desempenhoPorMes[indiceMes] = item.total ?? 0;
      }
    });

    return new AreaChartBuilder()
      .withCategories(this.meses)
      .withSeries('Tarefas concluidas', desempenhoPorMes)
      .build();
  }

  static criarGraficoStatusTarefas(resumo: DashboardResumo): BarChartOptions {
    return new BarChartBuilder()
      .withCategories(['Concluidas', 'Em Andamento', 'Pendentes', 'Atrasadas'])
      .withSeries('Tarefas', [
        resumo.tarefasConcluidas ?? 0,
        resumo.tarefasEmAndamento ?? 0,
        resumo.tarefasPendentes ?? 0,
        resumo.tarefasAtrasadas ?? 0,
      ])
      .build();
  }

  private static foiMarcadoComoExcluido(item: DesempenhoEquipe): boolean {
    if (typeof item.excluido === 'boolean') {
      return item.excluido;
    }

    return item.excluido !== null && item.excluido !== undefined && String(item.excluido).trim() !== '';
  }

  private static temItemExcluidoNoHistorico(resumo: DashboardResumo): boolean {
    const historico = resumo.desempenhoEquipe ?? resumo.tarefasConcluidasPorMes ?? [];

    return historico.some((item) => this.foiMarcadoComoExcluido(item));
  }
}
