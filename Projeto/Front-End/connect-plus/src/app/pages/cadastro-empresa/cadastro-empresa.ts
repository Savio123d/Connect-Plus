import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { EmpresaService, Empresa } from './service_empresa';




// Enum de Status
export enum Status {
  Ativo = 'A',
  Baixada = 'B',
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

  // O vetor de estados para o menu suspenso (Select)
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
    razaoSocial: new FormControl(''),
    nomeFantasia: new FormControl(''),
    cnpj: new FormControl('', Validators.maxLength(14)),
    cidade: new FormControl(''),
    situacao: new FormControl(Status.Ativo),
    uf: new FormControl(''),
  });

  constructor(private empresaService: EmpresaService) {}

  ngOnInit(): void {
    this.listarEmpresas();
  }
  prepararEdicao(empresa: Empresa): void {
    // @ts-ignore
    this.empresaC.patchValue(empresa);
  }

  onSubmit(): void {
    if (this.empresaC.invalid) {return}

    const dadosParaSalvar = this.empresaC.value as Empresa;

    if (dadosParaSalvar.idEmpresa) {
      this.atualizar();
    }else{
      this.empresaService.salvar(dadosParaSalvar).subscribe({
        next: (empresaSalva: Empresa) => {
          this.empresas.push(empresaSalva);
          this.empresaC.reset({ situacao: Status.Ativo }); // Reseta mantendo o Ativo padrão
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
      next: (empresaAtualizada: any) => {
        const index = this.empresas.findIndex((e) => e.idEmpresa === empresaAtualizada.idEmpresa);

        if (index !== -1) {
          this.empresas[index] = empresaAtualizada;
        }
      },
      error: (err) => console.error('Erro ao atualizar:', err),
    })}}

export default CadastroEmpresa

