import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

import { AuthSessionService } from '../../core/auth-session.service';
import { NotificacaoDTO } from '../../pages/notificacoes/notificacoes.service';
import { NotificacoesRealtimeService } from '../../pages/notificacoes/notificacoes-realtime.service';

interface MenuItem {
  label: string;
  icon: string;
  route: string;
}

interface UsuarioLogado {
  idUsuario?: number;
  idUsuarioEmpresa?: number;
  idEmpresa?: number;
  idSetor?: number;

  nome: string;
  email: string;
  status?: string;

  cargo?: string;
  departamento?: string;
  avatar?: string | null;
}

interface NotificacaoSidebar extends NotificacaoDTO {
  titulo: string;
  mensagem: string;
  lida: boolean;
  dataCriacao?: string;
  criadaEm?: string;
  dataLeitura?: string | null;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar implements OnInit, OnDestroy {
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private authSessionService = inject(AuthSessionService);
  private notificacoesRealtimeService = inject(NotificacoesRealtimeService);

  private destroy$ = new Subject<void>();

  usuarioLogado: UsuarioLogado | null = null;

  termoBusca = '';
  notificacoes: NotificacaoSidebar[] = [];
  quantidadeNaoLidas = 0;

  mostrarNotificacoes = false;

  toastNotificacao: NotificacaoSidebar | null = null;

  private intervaloNotificacoes?: ReturnType<typeof setInterval>;
  menuItems: MenuItem[] = [
    { label: 'Início', icon: 'home', route: '/dashboard' },
    { label: 'Quadro de Tarefas', icon: 'check_box', route: '/tarefas' },
    { label: 'Chat', icon: 'chat_bubble_outline', route: '/chat' },
    { label: 'Feedbacks', icon: 'mode_comment', route: '/feedbacks' },
    { label: 'Loja', icon: 'storefront', route: '/loja' },
    { label: 'Projetos', icon: 'business_center', route: '/projetos' },
    { label: 'Perfil', icon: 'person_outline', route: '/perfil' },
    { label: 'Usuários', icon: 'groups', route: '/usuarios' },
    { label: 'Configurações', icon: 'settings', route: '/configuracoes' },
    { label: 'Suporte', icon: 'headphones', route: '/suporte-interno' },
    { label: 'Sair', icon: 'logout', route: '/login' },
  ];

  ngOnInit(): void {
    this.carregarUsuarioLogado();
    this.iniciarNotificacoesTempoReal();
  }

  get iniciaisUsuario(): string {
    const nome = this.usuarioLogado?.nome?.trim();

    if (!nome) {
      return 'US';
    }

    const partes = nome.split(' ').filter(Boolean);

    if (partes.length === 1) {
      return partes[0].substring(0, 2).toUpperCase();
    }

    const primeiraLetra = partes[0][0];
    const ultimaLetra = partes[partes.length - 1][0];

    return `${primeiraLetra}${ultimaLetra}`.toUpperCase();
  }

  carregarUsuarioLogado(): void {
    const usuarioSalvo = localStorage.getItem('usuarioLogado');

    if (!usuarioSalvo) {
      this.usuarioLogado = null;
      this.cdr.detectChanges();
      return;
    }

    try {
      this.usuarioLogado = JSON.parse(usuarioSalvo);
    } catch (erro) {
      console.error('Erro ao carregar usuário logado:', erro);
      this.usuarioLogado = null;
    }

    this.cdr.detectChanges();
  }

  iniciarNotificacoesTempoReal(): void {
    this.notificacoesRealtimeService.iniciar();

    this.notificacoesRealtimeService.naoLidas$.pipe(takeUntil(this.destroy$)).subscribe((total: number) => {
      this.quantidadeNaoLidas = total;
      this.cdr.detectChanges();
    });

    this.notificacoesRealtimeService.ultimas$
      .pipe(takeUntil(this.destroy$))
      .subscribe((notificacoes: NotificacaoDTO[]) => {
        this.notificacoes = notificacoes.map((notificacao: NotificacaoDTO) =>
          this.normalizarNotificacao(notificacao),
        );

        this.cdr.detectChanges();
      });

    this.notificacoesRealtimeService.toast$
      .pipe(takeUntil(this.destroy$))
      .subscribe((notificacao: NotificacaoDTO) => {
        this.toastNotificacao = this.normalizarNotificacao(notificacao);
        this.cdr.detectChanges();

        setTimeout(() => {
          this.toastNotificacao = null;
          this.cdr.detectChanges();
        }, 4000);
      });
  }

  alternarNotificacoes(): void {
    this.mostrarNotificacoes = !this.mostrarNotificacoes;

    if (this.mostrarNotificacoes) {
      this.notificacoesRealtimeService.sincronizarComRest();
    }
  }

  marcarComoLida(notificacao: NotificacaoSidebar): void {
    if (notificacao.lida) {
      return;
    }

    this.notificacoesRealtimeService.marcarComoLida(notificacao);
  }

  pesquisarGlobal(): void {
    const termo = this.termoBusca.trim();

    if (!termo) {
      return;
    }

    console.log('Pesquisa global:', termo);
  }

  onMenuClick(event: Event, item: MenuItem): void {
    if (item.label === 'Sair') {
      event.preventDefault();
      this.sair();
    }
  }

  sair(): void {
    this.authSessionService.limparSessao();

    localStorage.removeItem('usuarioLogado');
    localStorage.removeItem('idUsuario');
    localStorage.removeItem('idUsuarioEmpresa');
    localStorage.removeItem('idEmpresa');
    localStorage.removeItem('usuarioEmpresaId');

    this.notificacoesRealtimeService.parar();

    this.router.navigate(['/login']);
  }

  dataDaNotificacao(notificacao: NotificacaoSidebar): string {
    return notificacao.criadaEm || notificacao.dataCriacao || '';
  }

  private normalizarNotificacao(notificacao: NotificacaoDTO): NotificacaoSidebar {
    const dataCriacao =
      notificacao.dataCriacao ||
      notificacao.criadaEm ||
      new Date().toISOString();

    return {
      ...notificacao,
      titulo: notificacao.titulo || 'Notificacao',
      mensagem: notificacao.mensagem || '',
      lida: Boolean(notificacao.lida),
      criadaEm: dataCriacao,
      dataCriacao,
    };
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
