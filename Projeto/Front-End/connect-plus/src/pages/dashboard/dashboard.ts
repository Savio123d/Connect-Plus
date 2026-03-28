import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterLink, RouterLinkActive } from '@angular/router'; 

type MenuItem = {
  label: string;
  icon: string;
  route: string;
};

@Component({
  selector: 'app-dashboard-gestor',
  standalone: true,
  imports: [
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    MatToolbarModule,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})

export class Menu {
  menuItems: MenuItem[] = [
    { label: 'Início', icon: 'home', route: '/dashboard' },
    { label: 'Feedbacks', icon: 'feedback', route: '/feedbacks' },
    { label: 'Perfil', icon: 'person', route: '/perfil' },
    { label: 'Quadro de tarefas:', icon: 'assignment', route: '/quadro-tarefas' },
    { label: 'Chat', icon: 'chat', route: '/chat' },
    { label: 'Projetos', icon: 'work', route: '/projetos' },
    { label: 'Loja', icon: 'store', route: '/loja' },
    { label: 'Configurações', icon: 'settings', route: '/configuracoes' },
    { label: 'Suporte', icon: 'support_agent', route: '/suporte' },
    { label: 'Sair', icon: 'logout', route: '/login' }
  ];

  totalEmpresasCadastradas = 0;
  usuariosAtivos = 1;
}
