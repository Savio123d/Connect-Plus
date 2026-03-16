import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

type MenuItem = {
  readonly label: string;
  readonly icon: string;
  readonly active?: boolean;
};

type UserProfile = {
  readonly name: string;
  readonly email: string;
  readonly initials: string;
  readonly role: string;
};

type DashboardUsuarioMetric = {
  readonly label: string;
  readonly value: string;
  readonly helper: string;
  readonly icon: string;
  readonly accent: string;
};

type DashboardUsuarioHome = {
  readonly title: string;
  readonly description: string;
  readonly metrics: readonly DashboardUsuarioMetric[];
};

type DashboardUsuarioData = {
  readonly profile: UserProfile;
  readonly home: DashboardUsuarioHome;
};

const DASHBOARD_USUARIO_MOCK: DashboardUsuarioData = {
  profile: {
    name: 'Amanda Costa',
    email: 'guiggay@connectmail.app',
    initials: 'AC',
    role: 'Colaboradora',
  },
  home: {
    title: 'Home do usuario',
    description: '',
    metrics: [
    ],
  },
};

@Component({
  selector: 'app-dashboard-usuario',
  standalone: true,
  imports: [MatIconModule],
  templateUrl: './dashboard-usuario.html',
  styleUrl: './dashboard-usuario.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardUsuario {
  readonly dashboardUsuarioData = signal<DashboardUsuarioData>(DASHBOARD_USUARIO_MOCK);

  readonly menuItems: readonly MenuItem[] = [
    { label: 'Home', icon: 'home', active: true },
    { label: 'Feedback', icon: 'campaign' },
    { label: 'Perfil', icon: 'person' },
    { label: 'Quadro de tarefas', icon: 'view_kanban' },
    { label: 'Chat', icon: 'forum' },
    { label: 'Projetos', icon: 'workspaces' },
  ];

  readonly currentUser = computed(() => this.dashboardUsuarioData().profile);

  readonly home = computed(() => this.dashboardUsuarioData().home);

  applyDashboardData(data: DashboardUsuarioData): void {
    this.dashboardUsuarioData.set(data);
  }
}
