import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, ViewEncapsulation } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { NgApexchartsModule } from 'ng-apexcharts';

import { DashboardService } from './dashboard.service';
import { LoginService } from '../login/login.service';
import { AreaChartOptions } from './builders/area-chart.builder';
import { BarChartOptions } from './builders/bar-chart.builder';
import { DashboardFactory } from './factories/dashboard.factory';
import { DashboardCard } from './models/dashboard.model';

@Component({
  selector: 'app-dashboard-gestor',
  standalone: true,
  imports: [CommonModule, MatIconModule, NgApexchartsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
  encapsulation: ViewEncapsulation.None,
})
export class Menu implements OnInit {
  cards: DashboardCard[] = [];

  areaChartOptions?: AreaChartOptions;
  barChartOptions?: BarChartOptions;

  carregando = true;
  mensagemErro = '';

  constructor(
    private dashboardService: DashboardService,
    private loginService: LoginService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.carregarDashboard();
  }

  carregarDashboard(): void {
    const empresaId = this.loginService.getEmpresaId();

    if (!empresaId) {
      this.mensagemErro = 'Não foi possível identificar a empresa do usuário logado. Faça login novamente.';
      this.carregando = false;
      this.cdr.detectChanges();
      return;
    }

    this.dashboardService.buscarResumo(empresaId).subscribe({
      next: (resumo) => {
        try {
          const dashboard = DashboardFactory.criarDashboard(resumo);

          this.cards = dashboard.cards;
          this.areaChartOptions = dashboard.areaChartOptions;
          this.barChartOptions = dashboard.barChartOptions;
        } catch (erroFactory) {
          console.error('Erro ao montar dashboard na factory:', erroFactory);
        } finally {
          this.carregando = false;
          this.cdr.detectChanges();
        }
      },
      error: (erro) => {
        console.error('Erro ao carregar dashboard:', erro);
        this.mensagemErro = 'Erro ao carregar os dados da empresa.';
        this.carregando = false;
        this.cdr.detectChanges();
      },
    });
  }
}
