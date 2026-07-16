import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AuthSessionService } from '../../core/auth-session.service';
import {
  ConfiguracoesService,
  EmpresaConfiguracaoBackend,
} from './configuracoes.service';

type AbaConfiguracao = 'geral' | 'notificacoes' | 'seguranca';

interface PreferenciaNotificacao {
  titulo: string;
  descricao: string;
  ativa: boolean;
}

@Component({
  selector: 'app-configuracoes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './configuracoes.html',
  styleUrl: './configuracoes.css',
})
export class Configuracoes implements OnInit {
  abaAtiva: AbaConfiguracao = 'geral';

  empresa = {
    nome: '',
    email: '',
    idioma: 'Português (Brasil)',
    fusoHorario: 'América/São Paulo (GMT-3)',
  };

  senha = {
    atual: '',
    nova: '',
    confirmacao: '',
  };

  notificacoes: PreferenciaNotificacao[] = [
    {
      titulo: 'Notificações por Email',
      descricao: 'Receber atualizações importantes por e-mail',
      ativa: true,
    },
    {
      titulo: 'Novos Feedbacks',
      descricao: 'Ser notificado quando receber um feedback',
      ativa: true,
    },
    {
      titulo: 'Tarefas Atribuídas',
      descricao: 'Notificar quando uma tarefa for atribuída a você',
      ativa: true,
    },
    {
      titulo: 'Mensagens no Chat',
      descricao: 'Alertas de novas mensagens',
      ativa: false,
    },
    {
      titulo: 'Atualizações de Projeto',
      descricao: 'Mudanças em projetos que você participa',
      ativa: false,
    },
  ];

  carregandoGeral = false;
  salvandoGeral = false;
  salvandoSenha = false;
  salvandoNotificacoes = false;

  mensagemSucesso = '';
  mensagemErro = '';

  private empresaOriginal: EmpresaConfiguracaoBackend | null = null;

  constructor(
    private configuracoesService: ConfiguracoesService,
    private authSessionService: AuthSessionService,
  ) {}

  ngOnInit(): void {
    this.preencherEmailUsuario();
    this.carregarConfiguracoesGerais();
  }

  alterarAba(aba: AbaConfiguracao): void {
    this.abaAtiva = aba;
    this.limparMensagens();
  }

  carregarConfiguracoesGerais(): void {
    const idEmpresa = this.pegarIdEmpresa();

    if (!idEmpresa) {
      this.mensagemErro = 'Não foi possível identificar a empresa logada. Faça login novamente.';
      return;
    }

    this.carregandoGeral = true;
    this.limparMensagens();

    this.configuracoesService.buscarEmpresa(idEmpresa).subscribe({
      next: (empresa) => {
        this.empresaOriginal = empresa;
        this.empresa.nome = empresa.nomeFantasia || empresa.razaoSocial || 'Connect+ Technology';
        this.carregandoGeral = false;
      },
      error: (erro) => {
        console.error('Erro ao carregar configurações da empresa:', erro);
        this.mensagemErro = 'Não foi possível carregar as configurações gerais pela API.';
        this.carregandoGeral = false;
      },
    });
  }

  salvarConfiguracoesGerais(): void {
    const idEmpresa = this.pegarIdEmpresa();
    const nomeEmpresa = this.empresa.nome.trim();

    if (!idEmpresa) {
      this.mensagemErro = 'Não foi possível identificar a empresa logada. Faça login novamente.';
      return;
    }

    if (!nomeEmpresa) {
      this.mensagemErro = 'Informe o nome da empresa.';
      return;
    }

    const empresaAtualizada: EmpresaConfiguracaoBackend = {
      ...this.empresaOriginal,
      razaoSocial: this.empresaOriginal?.razaoSocial || nomeEmpresa,
      nomeFantasia: nomeEmpresa,
    };

    this.salvandoGeral = true;
    this.limparMensagens();

    this.configuracoesService.atualizarEmpresa(idEmpresa, empresaAtualizada).subscribe({
      next: (empresa) => {
        this.empresaOriginal = empresa;
        this.empresa.nome = empresa.nomeFantasia || empresa.razaoSocial || nomeEmpresa;
        this.mensagemSucesso = 'Alterações gerais salvas no banco com sucesso.';
        this.salvandoGeral = false;
      },
      error: (erro) => {
        console.error('Erro ao salvar configurações da empresa:', erro);
        this.mensagemErro = this.extrairMensagemErro(erro, 'Não foi possível salvar as configurações gerais.');
        this.salvandoGeral = false;
      },
    });
  }

  salvarPreferencias(): void {
    this.limparMensagens();
    this.salvandoNotificacoes = true;

    window.setTimeout(() => {
      this.mensagemSucesso = 'Preferências de notificações salvas nesta sessão.';
      this.salvandoNotificacoes = false;
    }, 250);
  }

  atualizarSenha(): void {
    const idUsuario = this.pegarIdUsuario();

    if (!idUsuario) {
      this.mensagemErro = 'Não foi possível identificar o usuário logado. Faça login novamente.';
      return;
    }

    if (!this.senha.atual || !this.senha.nova || !this.senha.confirmacao) {
      this.mensagemErro = 'Preencha a senha atual, a nova senha e a confirmação.';
      return;
    }

    if (this.senha.nova.length < 6) {
      this.mensagemErro = 'A nova senha deve ter pelo menos 6 caracteres.';
      return;
    }

    if (this.senha.nova !== this.senha.confirmacao) {
      this.mensagemErro = 'A confirmação da nova senha não confere.';
      return;
    }

    this.salvandoSenha = true;
    this.limparMensagens();

    this.configuracoesService
      .alterarSenha(idUsuario, {
        senhaAtual: this.senha.atual,
        novaSenha: this.senha.nova,
      })
      .subscribe({
        next: () => {
          this.senha = {
            atual: '',
            nova: '',
            confirmacao: '',
          };
          this.mensagemSucesso = 'Senha atualizada no banco com sucesso.';
          this.salvandoSenha = false;
        },
        error: (erro) => {
          console.error('Erro ao atualizar senha:', erro);
          this.mensagemErro = this.extrairMensagemErro(erro, 'Não foi possível atualizar a senha.');
          this.salvandoSenha = false;
        },
      });
  }

  private preencherEmailUsuario(): void {
    const usuario = this.authSessionService.obterUsuario();
    this.empresa.email = usuario?.email ?? '';
  }

  private pegarIdEmpresa(): number {
    return this.authSessionService.obterIdEmpresa();
  }

  private pegarIdUsuario(): number {
    return this.authSessionService.obterUsuario()?.idUsuario ?? 0;
  }

  private limparMensagens(): void {
    this.mensagemSucesso = '';
    this.mensagemErro = '';
  }

  private extrairMensagemErro(erro: any, mensagemPadrao: string): string {
    if (typeof erro?.error === 'string' && erro.error.trim()) {
      return erro.error;
    }

    if (typeof erro?.error?.message === 'string' && erro.error.message.trim()) {
      return erro.error.message;
    }

    if (typeof erro?.message === 'string' && erro.message.trim()) {
      return erro.message;
    }

    return mensagemPadrao;
  }
}