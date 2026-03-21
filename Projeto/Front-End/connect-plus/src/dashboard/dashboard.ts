import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { BaseChartDirective } from 'ng2-charts';
import { Chart, ChartConfiguration, ChartOptions, registerables } from 'chart.js';

Chart.register(...registerables);

type MenuItem = {
  readonly label: string;
  readonly icon: string;
  readonly active?: boolean;
};

type DashboardOverview = {
  readonly activeUsers: number;
  readonly totalXp: number;
  readonly completedTasks: number;
  readonly sentFeedbacks: number;
  readonly activeProjects: number;
};

type HeroBadge = {
  readonly label: string;
  readonly value: string;
};

type StatCard = {
  readonly title: string;
  readonly value: string;
  readonly detail?: string;
  readonly icon: string;
  readonly accent: string;
};

type RankingUser = {
  readonly name: string;
  readonly role: string;
  readonly xp: string;
  readonly progress: number;
};

type TimelineItem = {
  readonly title: string;
  readonly detail: string;
  readonly time: string;
};

type DashboardChart = {
  readonly labels: readonly string[];
  readonly accumulatedXp: readonly number[];
  readonly expectedXp: readonly number[];
};

type DashboardData = {
  readonly overview: DashboardOverview;
  readonly ranking: readonly RankingUser[];
  readonly timeline: readonly TimelineItem[];
  readonly chart: DashboardChart;
};

@Component({
  selector: 'app-dashboard-gestor',
  standalone: true,
  imports: [
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    MatToolbarModule,
    BaseChartDirective,
  ],
  templateUrl: './dashboard-gestor.html',
  styleUrl: './dashboard-gestor.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Dashboard {
  readonly menuItems: readonly MenuItem[] = [
    { label: 'Home', icon: 'home', active: true },
    { label: 'Feedback', icon: 'campaign' },
    { label: 'Perfil', icon: 'person' },
    { label: 'Quadro de tarefas', icon: 'view_kanban' },
    { label: 'Chat', icon: 'forum' },
    { label: 'Projetos', icon: 'workspaces' },
    { label: 'Loja', icon: 'shopping_cart' },
  ];

  dashboardData: DashboardData = {
    overview: {
      activeUsers: 20,
      totalXp: 4350,
      completedTasks: 14,
      sentFeedbacks: 24,
      activeProjects: 6,
    },
    ranking: [],
    timeline: [],
    chart: {
      labels: ['Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sab', 'Dom'],
      // aqui muda os valores do xp 
      accumulatedXp: [3, 10, 15, 20, 26, 31, 100],
      expectedXp: [5, 12, 18, 24, 30, 36, 50],
    },
  };

  get heroBadges(): readonly HeroBadge[] {
    const { sentFeedbacks, activeProjects } = this.dashboardData.overview;

    return [
      {
        label: 'Total de feedbacks',
        value: this.formatNumber(sentFeedbacks),
      },
      {
        label: 'Projetos ativos',
        value: this.formatNumber(activeProjects),
      },
    ];
  }

  get stats(): readonly StatCard[] {
    const { activeUsers, totalXp, completedTasks } = this.dashboardData.overview;

    return [
      {
        title: 'Usuarios logados',
        value: this.formatNumber(activeUsers),
        detail: 'Membros com atividade recente na plataforma.',
        icon: 'groups_2',
        accent: 'linear-gradient(135deg, #20379a 0%, #4f54df 100%)',
      },
      {
        title: 'XP total',
        value: this.formatNumber(totalXp),
        detail: 'Pontuacao acumulada pelo time ate agora.',
        icon: 'workspace_premium',
        accent: 'linear-gradient(135deg, #6250ff 0%, #9155ff 100%)',
      },
      {
        title: 'Tarefas concluidas',
        value: this.formatNumber(completedTasks),
        detail: 'Entregas marcadas como finalizadas no dia.',
        icon: 'task_alt',
        accent: 'linear-gradient(135deg, #6d7cff 0%, #8198ff 100%)',
      },
    ];
  }

  get ranking(): readonly RankingUser[] {
    return this.dashboardData.ranking;
  }

  get timeline(): readonly TimelineItem[] {
    return this.dashboardData.timeline;
  }

  get chartData(): ChartConfiguration<'line'>['data'] {
    const { labels, accumulatedXp, expectedXp } = this.dashboardData.chart;

    return {
      labels: [...labels],
      datasets: [
        {
          data: [...accumulatedXp],
          label: 'XP acumulado',
          borderColor: '#243aa7',
          backgroundColor: 'rgba(36, 58, 167, 0.12)',
          fill: true,
          tension: 0.35,
          pointRadius: 0,
        },
        {
          data: [...expectedXp],
          label: 'XP esperado',
          borderColor: '#7c56ff',
          backgroundColor: 'rgba(124, 86, 255, 0)',
          fill: false,
          tension: 0.35,
          pointRadius: 0,
          borderDash: [8, 6],
        },
      ],
    };
  }

  readonly chartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        labels: {
          usePointStyle: true,
          boxWidth: 10,
          color: '#52618f',
          font: {
            family: 'Poppins',
            size: 12,
            weight: 600,
          },
        },
      },
      tooltip: {
        backgroundColor: '#1f2a69',
        titleFont: {
          family: 'Poppins',
          weight: 700,
        },
        bodyFont: {
          family: 'Poppins',
        },
        padding: 12,
        displayColors: false,
      },
    },
    scales: {
      x: {
        grid: {
          display: false,
        },
        ticks: {
          color: '#7181b1',
          font: {
            family: 'Poppins',
            weight: 500,
          },
        },
        border: {
          display: false,
        },
      },
      y: {
        suggestedMin: 0,
        suggestedMax: 60,
        ticks: {
          stepSize: 10,
          color: '#8a98bf',
          font: {
            family: 'Poppins',
            weight: 500,
          },
        },
        grid: {
          color: 'rgba(109, 124, 255, 0.12)',
          drawTicks: false,
        },
        border: {
          dash: [4, 4],
          color: 'rgba(109, 124, 255, 0.18)',
        },
      },
    },
  };

  private formatNumber(value: number): string {
    return new Intl.NumberFormat('pt-BR').format(value);
  }
}
