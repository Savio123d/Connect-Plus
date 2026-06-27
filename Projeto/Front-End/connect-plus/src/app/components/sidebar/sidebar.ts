import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, OnDestroy, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthSessionService } from '../../core/auth-session.service';

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

interface Notificacao {
  idNotificacao: number;
  idEmpresa: number;
  idUsuarioEmpresa: number;
  tipo: string;
  titulo?: string;
  mensagem: string;
  lida: boolean;
  dataCriacao: string;
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
  private http = inject(HttpClient);
  private cdr = inject(ChangeDetectorRef);
  private authSessionService = inject(AuthSessionService);

  usuarioLogado: UsuarioLogado | null = null;

  termoBusca = '';

  notificacoes: Notificacao[] = [];
  quantidadeNaoLidas = 0;

  mostrarNotificacoes = false;

  private intervaloNotificacoes?: ReturnType<typeof setInterval>;
  private readonly apiNotificacoes = 'http://localhost:8080/api/notificacoes';

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
    { label: 'Sair', icon: 'logout', route: '/login' },
  ];

  ngOnInit(): void {
    this.carregarUsuarioLogado();
    this.carregarNotificacoes();
    this.iniciarAtualizacaoNotificacoes();
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

  ngOnDestroy(): void {
    if (this.intervaloNotificacoes) {
      clearInterval(this.intervaloNotificacoes);
    }
  }

  iniciarAtualizacaoNotificacoes(): void {
    this.intervaloNotificacoes = setInterval(() => {
      this.carregarNotificacoes();
    }, 10000);
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

  carregarNotificacoes(): void {
    const idUsuarioEmpresa = this.usuarioLogado?.idUsuarioEmpresa;

    if (!idUsuarioEmpresa) {
      this.notificacoes = [];
      this.quantidadeNaoLidas = 0;
      this.cdr.detectChanges();
      return;
    }

    this.http
      .get<Notificacao[]>(`${this.apiNotificacoes}/usuario-empresa/${idUsuarioEmpresa}/ultimas`)
      .subscribe({
        next: (notificacoes) => {
          this.notificacoes = notificacoes;
          this.cdr.detectChanges();
        },
        error: (erro) => {
          console.error('Erro ao carregar notificações:', erro);
        },
      });

    this.http
      .get<{
        quantidade: number;
      }>(`${this.apiNotificacoes}/usuario-empresa/${idUsuarioEmpresa}/nao-lidas/quantidade`)
      .subscribe({
        next: (resposta) => {
          this.quantidadeNaoLidas = resposta.quantidade || 0;
          this.cdr.detectChanges();
        },
        error: (erro) => {
          console.error('Erro ao carregar quantidade de notificações:', erro);
        },
      });
  }

  alternarNotificacoes(): void {
    this.carregarNotificacoes();
    this.mostrarNotificacoes = !this.mostrarNotificacoes;
  }

  marcarComoLida(notificacao: Notificacao): void {
    if (notificacao.lida) {
      return;
    }

    this.http
      .patch<Notificacao>(
        `${this.apiNotificacoes}/${notificacao.idNotificacao}/marcar-como-lida`,
        {},
      )
      .subscribe({
        next: () => {
          notificacao.lida = true;

          if (this.quantidadeNaoLidas > 0) {
            this.quantidadeNaoLidas--;
          }

          this.cdr.detectChanges();
        },
        error: (erro) => {
          console.error('Erro ao marcar notificação como lida:', erro);
        },
      });
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

    this.router.navigate(['/login']);
  }
}
