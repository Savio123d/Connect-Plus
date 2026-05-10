import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { EmpresaService, Empresa } from './service_empresa';

// Enum de Status
export enum Status {
  Ativa = 'ativa',
  Inativa = 'inativa'
}

// Definir um tipo para os estados brasileiros
export type Estado =
  | 'AC'
  | 'AL'
  | 'AP'
  | 'AM'
  | 'BA'
  | 'CE'
  | 'DF'
  | 'ES'
  | 'GO'
  | 'MA'
  | 'MT'
  | 'MS'
  | 'MG'
  | 'PA'
  | 'PB'
  | 'PR'
  | 'PE'
  | 'PI'
  | 'RJ'
  | 'RN'
  | 'RS'
  | 'RO'
  | 'RR'
  | 'SC'
  | 'SP'
  | 'SE'
  | 'TO';

@Component({
  selector: 'app-cadastro-empresa',
  standalone: true,
  templateUrl: './cadastro-empresa.html',
  styleUrls: ['./cadastro-empresa.css'],
  imports: [ReactiveFormsModule, CommonModule],
})
export class CadastroEmpresa implements OnInit {
  Status = Status;
  empresas: Empresa[] = [];

  // O vetor de estados para o menu suspenso
  estados: Estado[] = [
    'AC',
    'AL',
    'AP',
    'AM',
    'BA',
    'CE',
    'DF',
    'ES',
    'GO',
    'MA',
    'MT',
    'MS',
    'MG',
    'PA',
    'PB',
    'PR',
    'PE',
    'PI',
    'RJ',
    'RN',
    'RS',
    'RO',
    'RR',
    'SC',
    'SP',
    'SE',
    'TO',
  ];

  empresaC = new FormGroup({
    idEmpresa: new FormControl<number | null>(null),
    razaoSocial: new FormControl('', Validators.required),
    nomeFantasia: new FormControl(''),
    cnpj: new FormControl('', Validators.maxLength(14)),
    cidade: new FormControl(''),
    status: new FormControl(Status.Ativa),
    uf: new FormControl(''),
  });

  constructor(private empresaService: EmpresaService) {}

  ngOnInit(): void {
    this.listarEmpresas();
  }

  prepararEdicao(empresa: Empresa): void {
    this.empresaC.patchValue({
      idEmpresa: empresa.idEmpresa ?? null,
      razaoSocial: empresa.razaoSocial,
      nomeFantasia: empresa.nomeFantasia,
      cnpj: empresa.cnpj,
      cidade: empresa.cidade,
      status: empresa.status as any,
      uf: empresa.uf,
    });
  }

  onSubmit(): void {
    if (this.empresaC.invalid) {
      return;
    }

    const dadosParaSalvar = this.empresaC.value as Empresa;

    if (dadosParaSalvar.idEmpresa) {
      this.atualizar();
    } else {
      this.empresaService.salvar(dadosParaSalvar).subscribe({
        next: () => {
          this.listarEmpresas();
          this.empresaC.reset({ status: Status.Ativa });
          alert('Empresa cadastrada com sucesso! ✅');
        },
        error: (err) => console.error('Erro ao salvar', err),
      });
    }
  }

  listarEmpresas(): void {
    this.empresaService.listar().subscribe({
      next: (dados) => (this.empresas = dados),
      error: (err) => console.error('Erro ao listar:', err),
    });
  }

  deleta(empresa: Empresa): void {
    if (empresa.idEmpresa) {
      this.empresaService.deleta(empresa.idEmpresa).subscribe({
        next: () => {
          this.empresas = this.empresas.filter((e) => e.idEmpresa !== empresa.idEmpresa);
          alert('Empresa removida! ✅');
        },
        error: (err) => console.error('Erro ao deletar:', err),
      });
    }
  }

  atualizar(): void {
    this.empresaService.upadate(this.empresaC.value as Empresa).subscribe({
      next: () => {
        this.listarEmpresas();
        this.empresaC.reset({ status: Status.Ativa });
        alert('Empresa atualizada com sucesso! ✅');
      },
      error: (err) => console.error('Erro ao atualizar:', err),
    });
  }
}

export default CadastroEmpresa
