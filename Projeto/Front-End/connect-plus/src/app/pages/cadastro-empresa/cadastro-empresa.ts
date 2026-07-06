import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { CadastroEmpresaCompleto, EmpresaService, TipoPlano } from './service_empresa';
import { environment } from '../../../environments/environment';

export enum Status {
  Ativa = 'ativa',
  Inativa = 'inativa',
}

@Component({
  selector: 'app-cadastro-empresa',
  standalone: true,
  templateUrl: './cadastro-empresa.html',
  styleUrls: ['./cadastro-empresa.css'],
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
})
export class CadastroEmpresa implements OnInit {
  Status = Status;
  mensagemErro = '';
  enviando = false;

  estados = [
    'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA',
    'MT', 'MS', 'MG', 'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN',
    'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO',
  ];

  cadastroForm = new FormGroup({
    idEmpresa: new FormControl<number | null>(null),
    razaoSocial: new FormControl('', Validators.required),
    nomeFantasia: new FormControl(''),
    cnpj: new FormControl('', [Validators.required, Validators.maxLength(18)]),
    cidade: new FormControl(''),
    status: new FormControl(Status.Ativa),
    uf: new FormControl(''),
    tipoPlano: new FormControl<TipoPlano>('gratuito', {
      nonNullable: true,
      validators: Validators.required,
    }),

    nomeAdmin: new FormControl('', Validators.required),
    emailAdmin: new FormControl('', [Validators.required, Validators.email]),
    senhaAdmin: new FormControl('', [Validators.required, Validators.minLength(8)]),
    confirmarSenhaAdmin: new FormControl(''),
  });

  constructor(
    private empresaService: EmpresaService,
    private router: Router,
  ) {}

  ngOnInit(): void {}

  onSubmit(): void {
    this.mensagemErro = '';

    if (this.cadastroForm.invalid) {
      this.cadastroForm.markAllAsTouched();
      this.mensagemErro = 'Preencha todos os campos obrigatorios corretamente.';
      return;
    }

    const { senhaAdmin, confirmarSenhaAdmin } = this.cadastroForm.value;

    if (senhaAdmin !== confirmarSenhaAdmin) {
      this.mensagemErro = 'As senhas nao coincidem.';
      return;
    }

    const cnpj = this.removerMascara(this.cadastroForm.value.cnpj!);

    if (cnpj.length !== 14) {
      this.mensagemErro = 'Informe um CNPJ valido com 14 digitos.';
      return;
    }

    const dados: CadastroEmpresaCompleto = {
      razaoSocial: this.cadastroForm.value.razaoSocial!,
      nomeFantasia: this.cadastroForm.value.nomeFantasia ?? '',
      cnpj,
      cidade: this.cadastroForm.value.cidade ?? '',
      uf: this.cadastroForm.value.uf ?? '',
      tipoPlano: this.cadastroForm.controls.tipoPlano.value,
      nomeAdmin: this.cadastroForm.value.nomeAdmin!,
      emailAdmin: this.cadastroForm.value.emailAdmin!,
      senhaAdmin: this.cadastroForm.value.senhaAdmin!,
    };

    this.enviando = true;

    this.empresaService.cadastrarEmpresa(dados).subscribe({
      next: (resposta) => {
        this.enviando = false;
        alert(resposta.mensagem || 'Cadastro realizado com sucesso!');

        if (resposta.checkoutUrl) {
          window.location.href = resposta.checkoutUrl;
          return;
        }

        this.router.navigate(['/login']);
      },
      error: (erro: HttpErrorResponse) => {
        this.enviando = false;
        this.mensagemErro = this.extrairMensagemErro(erro);
      },
    });
  }

  private removerMascara(valor: string): string {
    return valor.replace(/\D/g, '');
  }

  private extrairMensagemErro(erro: HttpErrorResponse): string {
    const mensagemBackend = this.lerMensagemDoBackend(erro.error);

    if (mensagemBackend) {
      return this.normalizarMensagem(mensagemBackend);
    }

    if (erro.status === 409) {
      return 'CNPJ ou email ja cadastrado. Use outros dados para concluir o cadastro.';
    }

    if (erro.status === 0) {
      return `Nao foi possivel conectar ao backend em ${environment.apiBase}.`;
    }

    return 'Erro ao realizar cadastro. Tente novamente.';
  }

  private lerMensagemDoBackend(corpoErro: unknown): string {
    if (!corpoErro) {
      return '';
    }

    if (typeof corpoErro === 'string') {
      try {
        return this.lerMensagemDoBackend(JSON.parse(corpoErro));
      } catch {
        return corpoErro;
      }
    }

    if (typeof corpoErro === 'object') {
      const erro = corpoErro as Record<string, unknown>;
      return String(erro['detail'] ?? erro['message'] ?? erro['error'] ?? erro['title'] ?? '');
    }

    return '';
  }

  private normalizarMensagem(mensagem: string): string {
    const texto = mensagem.replace(/^409\s+CONFLICT\s*/i, '').trim();
    const textoMinusculo = texto.toLowerCase();

    if (textoMinusculo.includes('cnpj')) {
      return 'CNPJ ja cadastrado. Use outro CNPJ para criar uma nova empresa.';
    }

    if (textoMinusculo.includes('email')) {
      return 'Email ja cadastrado. Use outro email para o administrador.';
    }

    return texto || 'Erro ao realizar cadastro. Tente novamente.';
  }
}