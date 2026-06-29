import {
  AfterViewChecked,
  ChangeDetectorRef,
  Component,
  ElementRef,
  NgZone,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService } from './chat.service';
import {
  ChatEvento,
  ConversaDetalhe,
  ConversaResumo,
  Mensagem,
  MensagemAnexo,
  TipoConversa,
  UsuarioChat,
} from './chat.model';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.html',
  styleUrl: './chat.css',
})
export class Chat implements OnInit, OnDestroy, AfterViewChecked {
  @ViewChild('mensagensContainer')
  mensagensContainer?: ElementRef<HTMLDivElement>;

  modalGrupoAberto = false;
  nomeGrupo = '';
  idsParticipantesGrupo: number[] = [];
  criandoGrupo = false;

  conversas: ConversaResumo[] = [];
  conversaSelecionada: ConversaDetalhe | null = null;

  termoBusca = '';
  novaMensagem = '';
  filtroTipo: TipoConversa | '' = '';
  imagemSelecionada: File | null = null;
  imagemPreviewUrl = '';

  carregandoConversas = false;
  enviandoMensagem = false;

  mensagemErro = '';
  usuarioEmpresaId = 0;

  usuariosEmpresa: UsuarioChat[] = [];
  carregandoUsuarios = false;
  empresaId = 0;

  private socket?: WebSocket;
  private precisaRolarFinal = false;
  private tentandoReconectar = false;
  carregandoMensagens = false;
  private readonly tiposImagemPermitidos = ['image/png', 'image/jpeg', 'image/webp'];
  private readonly tamanhoMaximoImagem = 5 * 1024 * 1024;

  constructor(
    private chatService: ChatService,
    private ngZone: NgZone,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.usuarioEmpresaId = this.buscarUsuarioEmpresaId();
    this.empresaId = this.buscarEmpresaId();

    if (!this.usuarioEmpresaId) {
      this.mensagemErro =
        'Não foi possível identificar o usuário logado. Verifique se o usuarioEmpresaId está salvo no localStorage.';
      return;
    }

    this.carregarConversas();
    this.carregarUsuariosEmpresa();
    this.conectarWebSocket();
  }
  ngAfterViewChecked(): void {
    if (this.precisaRolarFinal) {
      this.rolarParaFinal();
      this.precisaRolarFinal = false;
    }
  }

  ngOnDestroy(): void {
    if (this.socket) {
      this.socket.close();
    }

    this.liberarPreviewImagem();
  }

  get conversasFiltradas(): ConversaResumo[] {
    const busca = this.termoBusca.trim().toLowerCase();

    return this.conversas.filter((conversa) => {
      const bateTipo = this.filtroTipo ? conversa.tipo === this.filtroTipo : true;

      const ultimaMensagem = conversa.ultimaMensagem?.conteudo ?? '';

      const bateBusca =
        !busca ||
        conversa.nome?.toLowerCase().includes(busca) ||
        ultimaMensagem.toLowerCase().includes(busca) ||
        conversa.participantes?.some((participante) =>
          participante.nome?.toLowerCase().includes(busca),
        );

      return bateTipo && bateBusca;
    });
  }

  abrirModalGrupo(): void {
    this.nomeGrupo = '';
    this.idsParticipantesGrupo = [];
    this.modalGrupoAberto = true;
  }

  fecharModalGrupo(): void {
    this.modalGrupoAberto = false;
    this.nomeGrupo = '';
    this.idsParticipantesGrupo = [];
  }

  alternarParticipanteGrupo(usuario: UsuarioChat): void {
    const idUsuarioEmpresa = this.obterIdUsuarioEmpresaDoUsuario(usuario);

    if (!idUsuarioEmpresa) {
      return;
    }

    const jaSelecionado = this.idsParticipantesGrupo.includes(idUsuarioEmpresa);

    if (jaSelecionado) {
      this.idsParticipantesGrupo = this.idsParticipantesGrupo.filter(
        (id) => id !== idUsuarioEmpresa,
      );
      return;
    }

    this.idsParticipantesGrupo = [...this.idsParticipantesGrupo, idUsuarioEmpresa];
  }

  usuarioSelecionadoGrupo(usuario: UsuarioChat): boolean {
    const idUsuarioEmpresa = this.obterIdUsuarioEmpresaDoUsuario(usuario);
    return this.idsParticipantesGrupo.includes(idUsuarioEmpresa);
  }

  criarGrupo(): void {
    const nome = this.nomeGrupo.trim();

    if (!nome) {
      this.mensagemErro = 'Informe o nome do grupo.';
      return;
    }

    if (this.idsParticipantesGrupo.length === 0) {
      this.mensagemErro = 'Selecione pelo menos um participante.';
      return;
    }

    this.criandoGrupo = true;

    this.chatService
      .criarConversaGrupo(this.usuarioEmpresaId, nome, this.idsParticipantesGrupo)
      .subscribe({
        next: (grupoCriado) => {
          this.ngZone.run(() => {
            this.inserirOuAtualizarConversa(grupoCriado);
            this.conversaSelecionada = grupoCriado;
            this.fecharModalGrupo();
            this.criandoGrupo = false;
            this.precisaRolarFinal = true;
            this.atualizarTela();
          });
        },
        error: (erro) => {
          this.ngZone.run(() => {
            console.error('Erro ao criar grupo:', erro);
            this.mensagemErro = 'Não foi possível criar o grupo.';
            this.criandoGrupo = false;
            this.atualizarTela();
          });
        },
      });
  }

  get usuariosFiltrados(): UsuarioChat[] {
    const busca = this.termoBusca.trim().toLowerCase();

    if (!busca) {
      return [];
    }

    return this.usuariosEmpresa.filter((usuario) => {
      const idUsuarioEmpresa = this.obterIdUsuarioEmpresaDoUsuario(usuario);

      if (idUsuarioEmpresa === this.usuarioEmpresaId) {
        return false;
      }

      return (
        usuario.nome?.toLowerCase().includes(busca) || usuario.email?.toLowerCase().includes(busca)
      );
    });
  }

  carregarUsuariosEmpresa(): void {
    if (!this.empresaId) {
      return;
    }

    this.carregandoUsuarios = true;

    this.chatService.listarUsuariosDaEmpresa(this.empresaId).subscribe({
      next: (usuarios) => {
        this.usuariosEmpresa = usuarios;
        this.carregandoUsuarios = false;
      },
      error: (erro) => {
        console.error('Erro ao carregar usuários da empresa:', erro);
        this.carregandoUsuarios = false;
      },
    });
  }

  abrirOuCriarConversaComUsuario(usuario: UsuarioChat): void {
    const idDestinatarioUsuarioEmpresa = this.obterIdUsuarioEmpresaDoUsuario(usuario);

    if (!idDestinatarioUsuarioEmpresa) {
      this.mensagemErro = 'Não foi possível identificar esse usuário.';
      return;
    }

    this.termoBusca = '';
    this.atualizarTela();

    const conversaExistente = this.conversas.find(
      (conversa) =>
        conversa.tipo === 'privada' &&
        conversa.participantes?.some(
          (participante) => participante.idUsuarioEmpresa === idDestinatarioUsuarioEmpresa,
        ),
    );

    if (conversaExistente) {
      this.selecionarConversa(conversaExistente);
      return;
    }

    this.chatService
      .criarConversaPrivada(this.usuarioEmpresaId, idDestinatarioUsuarioEmpresa)
      .subscribe({
        next: (conversaCriada) => {
          this.ngZone.run(() => {
            this.inserirOuAtualizarConversa(conversaCriada);
            this.conversaSelecionada = conversaCriada;
            this.carregarConversas(true);
            this.precisaRolarFinal = true;
            this.atualizarTela();
          });
        },
        error: (erro) => {
          this.ngZone.run(() => {
            console.error('Erro ao criar conversa privada:', erro);
            this.mensagemErro = 'Não foi possível iniciar a conversa.';
            this.atualizarTela();
          });
        },
      });
  }

  usuarioJaTemConversa(usuario: UsuarioChat): boolean {
    const idUsuarioEmpresa = this.obterIdUsuarioEmpresaDoUsuario(usuario);

    return this.conversas.some(
      (conversa) =>
        conversa.tipo === 'privada' &&
        conversa.participantes?.some(
          (participante) => participante.idUsuarioEmpresa === idUsuarioEmpresa,
        ),
    );
  }

  private inserirOuAtualizarConversa(conversa: ConversaResumo): void {
    const index = this.conversas.findIndex((item) => item.id === conversa.id);

    if (index >= 0) {
      this.conversas[index] = conversa;
      return;
    }

    this.conversas.unshift(conversa);
  }

  obterIdUsuarioEmpresaDoUsuario(usuario: UsuarioChat): number {
    return Number(usuario.idUsuarioEmpresa ?? usuario.id);
  }

  private buscarEmpresaId(): number {
    const valor =
      localStorage.getItem('idEmpresa') ||
      localStorage.getItem('empresaId') ||
      localStorage.getItem('usuarioEmpresaIdEmpresa');

    return Number(valor) || 1;
  }

  carregarConversas(silencioso = false): void {
    if (!silencioso) {
      this.carregandoConversas = true;
    }

    this.mensagemErro = '';

    this.chatService.listarConversas(this.usuarioEmpresaId, this.filtroTipo).subscribe({
      next: (conversas) => {
        this.ngZone.run(() => {
          this.conversas = [...conversas];
          this.carregandoConversas = false;

          if (!this.conversaSelecionada && conversas.length > 0) {
            this.selecionarConversa(conversas[0]);
          }

          this.atualizarTela();
        });
      },
      error: (erro) => {
        this.ngZone.run(() => {
          console.error('Erro ao carregar conversas:', erro);
          this.mensagemErro = 'Não foi possível carregar as conversas.';
          this.carregandoConversas = false;
          this.atualizarTela();
        });
      },
    });
  }

  selecionarConversa(conversa: ConversaResumo): void {
    this.carregandoMensagens = true;
    this.mensagemErro = '';

    this.chatService.detalharConversa(this.usuarioEmpresaId, conversa.id).subscribe({
      next: (detalhe) => {
        this.ngZone.run(() => {
          this.conversaSelecionada = {
            ...detalhe,
            mensagens: [...detalhe.mensagens],
          };

          this.carregandoMensagens = false;
          this.precisaRolarFinal = true;
          this.marcarMensagensRecebidasComoLidas(detalhe.mensagens);
          this.atualizarTela();
        });
      },
      error: (erro) => {
        console.error('Erro ao carregar conversa:', erro);
        this.mensagemErro = 'Não foi possível carregar o histórico da conversa.';
        this.carregandoMensagens = false;
      },
    });
  }

  enviarMensagem(): void {
    const texto = this.novaMensagem.trim();

    if ((!texto && !this.imagemSelecionada) || !this.conversaSelecionada || this.enviandoMensagem) {
      return;
    }

    const idConversa = this.conversaSelecionada.id;

    this.enviandoMensagem = true;

    if (this.imagemSelecionada) {
      this.chatService
        .enviarImagemChat(this.usuarioEmpresaId, this.empresaId, this.imagemSelecionada)
        .subscribe({
          next: (anexo) => this.enviarMensagemComAnexo(idConversa, texto, anexo),
          error: (erro) => {
            this.ngZone.run(() => {
              console.error('Erro ao enviar imagem:', erro);
              this.mensagemErro = erro?.error?.message ?? 'Nao foi possivel enviar a imagem.';
              this.enviandoMensagem = false;
              this.atualizarTela();
            });
          },
        });
      return;
    }

    this.enviarMensagemComAnexo(idConversa, texto);
  }

  private enviarMensagemComAnexo(idConversa: number, texto: string, anexo?: MensagemAnexo): void {
    this.chatService.enviarMensagem(this.usuarioEmpresaId, idConversa, texto, anexo, anexo ? 'imagem' : 'texto').subscribe({
      next: (mensagem) => {
        this.ngZone.run(() => {
          this.novaMensagem = '';
          this.limparImagemSelecionada();
          this.enviandoMensagem = false;

          if (this.conversaSelecionada?.id === idConversa) {
            this.adicionarMensagemSemDuplicar(mensagem);
          }

          this.atualizarUltimaMensagemDaConversa(idConversa, mensagem);
          this.carregarConversas(true);
          this.precisaRolarFinal = true;
          this.atualizarTela();
        });
      },
      error: (erro) => {
        this.ngZone.run(() => {
          console.error('Erro ao enviar mensagem:', erro);
          this.mensagemErro = 'Não foi possível enviar a mensagem.';
          this.enviandoMensagem = false;
          this.atualizarTela();
        });
      },
    });
  }

  selecionarImagem(event: Event): void {
    const input = event.target as HTMLInputElement;
    const arquivo = input.files?.[0];
    input.value = '';

    if (!arquivo) {
      return;
    }

    if (!this.tiposImagemPermitidos.includes(arquivo.type)) {
      this.mensagemErro = 'Envie apenas imagens PNG, JPG ou WebP.';
      return;
    }

    if (arquivo.size > this.tamanhoMaximoImagem) {
      this.mensagemErro = 'A imagem deve ter no maximo 5 MB.';
      return;
    }

    this.mensagemErro = '';
    this.liberarPreviewImagem();
    this.imagemSelecionada = arquivo;
    this.imagemPreviewUrl = URL.createObjectURL(arquivo);
  }

  limparImagemSelecionada(): void {
    this.imagemSelecionada = null;
    this.liberarPreviewImagem();
  }

  private atualizarUltimaMensagemDaConversa(idConversa: number, mensagem: Mensagem): void {
    this.conversas = this.conversas.map((conversa) => {
      if (conversa.id !== idConversa) {
        return conversa;
      }

      return {
        ...conversa,
        ultimaMensagem: mensagem,
        atualizadoEm: mensagem.enviadaEm,
      };
    });
  }
  enviarComEnter(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.enviarMensagem();
    }
  }

  alterarFiltro(tipo: TipoConversa | ''): void {
    this.filtroTipo = tipo;
    this.conversaSelecionada = null;
    this.carregarConversas();
  }

  obterIniciais(nome?: string): string {
    if (!nome) {
      return '?';
    }

    const partes = nome.trim().split(' ').filter(Boolean);

    if (partes.length === 1) {
      return partes[0].substring(0, 2).toUpperCase();
    }

    return `${partes[0][0]}${partes[partes.length - 1][0]}`.toUpperCase();
  }

  obterSubtitulo(conversa: ConversaResumo): string {
    if (conversa.tipo === 'grupo') {
      return `${conversa.participantes?.length ?? 0} participantes`;
    }

    const outroParticipante = conversa.participantes?.find(
      (participante) => participante.idUsuarioEmpresa !== this.usuarioEmpresaId,
    );

    return outroParticipante?.email ?? 'Conversa privada';
  }

  obterPreviaMensagem(conversa: ConversaResumo): string {
    if (!conversa.ultimaMensagem) {
      return 'Nenhuma mensagem enviada ainda.';
    }

    if (conversa.ultimaMensagem.anexo) {
      return this.ehAnexoImagem(conversa.ultimaMensagem)
        ? `Imagem: ${conversa.ultimaMensagem.anexo.filename}`
        : `Arquivo: ${conversa.ultimaMensagem.anexo.filename}`;
    }

    return conversa.ultimaMensagem.conteudo || 'Mensagem sem texto';
  }

  ehAnexoImagem(mensagem: Mensagem): boolean {
    return mensagem.tipo === 'imagem' || mensagem.anexo?.tipoMime?.startsWith('image/') === true;
  }

  formatarHorario(data?: string): string {
    if (!data) {
      return '';
    }

    return new Date(data).toLocaleTimeString('pt-BR', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  formatarData(data?: string): string {
    if (!data) {
      return '';
    }

    return new Date(data).toLocaleDateString('pt-BR');
  }

  conversaEstaSelecionada(conversa: ConversaResumo): boolean {
    return this.conversaSelecionada?.id === conversa.id;
  }

  private conectarWebSocket(): void {
    this.socket = this.chatService.conectarWebSocket(
      this.usuarioEmpresaId,
      (evento) => {
        this.ngZone.run(() => {
          this.processarEventoRealtime(evento);
          this.atualizarTela();
        });
      },
      () => {
        this.ngZone.run(() => {
          this.reconectarWebSocket();
          this.atualizarTela();
        });
      },
    );
  }

  private processarEventoRealtime(evento: ChatEvento): void {
    if (evento.tipo === 'CONVERSA_CRIADA') {
      this.carregarConversas(true);
      return;
    }

    if (evento.tipo === 'MENSAGEM_ENVIADA') {
      this.carregarConversas(true);

      if (this.conversaSelecionada?.id === evento.idConversa) {
        this.recarregarMensagensDaConversaAtual();
      }
    }

    if (evento.tipo === 'MENSAGEM_LIDA') {
      if (this.conversaSelecionada?.id === evento.idConversa) {
        this.recarregarMensagensDaConversaAtual();
      }
    }
  }

  private recarregarMensagensDaConversaAtual(): void {
    if (!this.conversaSelecionada) {
      return;
    }

    const idConversa = this.conversaSelecionada.id;

    this.chatService.listarMensagens(this.usuarioEmpresaId, idConversa).subscribe({
      next: (mensagens) => {
        if (this.conversaSelecionada?.id === idConversa) {
          this.conversaSelecionada.mensagens = mensagens;
          this.marcarMensagensRecebidasComoLidas(mensagens);
          this.precisaRolarFinal = true;
        }
      },
      error: (erro) => {
        console.error('Erro ao atualizar mensagens:', erro);
      },
    });
  }

  private adicionarMensagemSemDuplicar(mensagem: Mensagem): void {
    if (!this.conversaSelecionada) {
      return;
    }

    const jaExiste = this.conversaSelecionada.mensagens.some((msg) => msg.id === mensagem.id);

    if (!jaExiste) {
      this.conversaSelecionada = {
        ...this.conversaSelecionada,
        mensagens: [...this.conversaSelecionada.mensagens, mensagem],
      };

      this.precisaRolarFinal = true;
      this.atualizarTela();
    }
  }

  private marcarMensagensRecebidasComoLidas(mensagens: Mensagem[]): void {
    mensagens
      .filter((mensagem) => !mensagem.enviadaPeloUsuarioLogado && !mensagem.lidaPeloUsuarioLogado)
      .forEach((mensagem) => {
        this.chatService.marcarMensagemComoLida(this.usuarioEmpresaId, mensagem.id).subscribe({
          error: (erro) => {
            console.error('Erro ao marcar mensagem como lida:', erro);
          },
        });
      });
  }

  private reconectarWebSocket(): void {
    if (this.tentandoReconectar) {
      return;
    }

    this.tentandoReconectar = true;

    setTimeout(() => {
      this.tentandoReconectar = false;

      if (this.usuarioEmpresaId) {
        this.conectarWebSocket();
      }
    }, 3000);
  }

  private rolarParaFinal(): void {
    const container = this.mensagensContainer?.nativeElement;

    if (!container) {
      return;
    }

    container.scrollTop = container.scrollHeight;
  }

  private atualizarTela(): void {
    this.cdr.detectChanges();
  }

  private liberarPreviewImagem(): void {
    if (this.imagemPreviewUrl) {
      URL.revokeObjectURL(this.imagemPreviewUrl);
      this.imagemPreviewUrl = '';
    }
  }

  private buscarUsuarioEmpresaId(): number {
    const valor =
      localStorage.getItem('usuarioEmpresaId') ||
      localStorage.getItem('idUsuarioEmpresa') ||
      localStorage.getItem('usuario_empresa_id');

    return Number(valor);
  }
}
