import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

interface MenuItem {
  label: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css'
})

export class Sidebar {
  menuItems: MenuItem[] = [
    { label: 'Início', icon: 'home', route: '/dashboard' },
    { label: 'Feedbacks', icon: 'feedback', route: '/feedbacks' },
    { label: 'Perfil', icon: 'person', route: '/perfil' },
    { label: 'Quadro de Tarefas', icon: 'check_box', route: '/tarefas' },
    { label: 'Chat', icon: 'chat', route: '/chat' },
    { label: 'Projetos', icon: 'work', route: '/projetos' },
    { label: 'Loja', icon: 'store', route: '/loja' },
    { label: 'Usuários', icon: 'group', route: '/usuarios' },
    { label: 'Configurações', icon: 'settings', route: '/configuracoes' },
    { label: 'Suporte', icon: 'support_agent', route: '/suporte' },
    { label: 'Sair', icon: 'logout', route: '/login' }
  ];
}
