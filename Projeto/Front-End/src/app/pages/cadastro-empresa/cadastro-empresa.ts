import { Component } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

// Enum de Status
enum Status {
  Ativo = 'A',
  Baixada = 'B',
}

// Definir um tipo para os estados brasileiros
type Estado =
  | 'AC' | 'AL' | 'AP' | 'AM' | 'BA' | 'CE' | 'DF' | 'ES'
  | 'GO' | 'MA' | 'MT' | 'MS' | 'MG' | 'PA' | 'PB' | 'PR'
  | 'PE' | 'PI' | 'RJ' | 'RN' | 'RS' | 'RO' | 'RR' | 'SC'
  | 'SP' | 'SE' | 'TO';

interface Empresa {
  razaoSocial: string;
  nomeFantasia: string;
  cnpj: string;
  cidade: string;
  situacao: Status;
  uf: Estado;
}

@Component({
  selector: 'app-cadastro-empresa',
  standalone: true,
  templateUrl: './cadastro-empresa.html',
  styleUrls: ['./cadastro-empresa.css'],
  imports: [ReactiveFormsModule, CommonModule],
})
export class CadastroEmpresa {
  Status = Status;
  empresas: Empresa[] = []; // Lista de empresas cadastradas

  estados: Estado[] = [
    'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES',
    'GO', 'MA', 'MT', 'MS', 'MG', 'PA', 'PB', 'PR',
    'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC',
    'SP', 'SE', 'TO'
  ];

  empresaC = new FormGroup({
    razaoSocial: new FormControl(''),
    nomeFantasia: new FormControl(''),
    cnpj: new FormControl("", Validators.maxLength(14)),
    cidade: new FormControl(''),
    situacao: new FormControl(Status.Ativo), 
    uf: new FormControl(''),
  });

  // Função de submit para adicionar uma nova empresa no vetor
  onSubmit(): void {
    const empresa = this.empresaC.value as Empresa; 
    this.empresas.push(empresa);  
    console.log(this.empresas);  // Exibe as empresas no console
  }

  Delete(index: number): void {
    this.empresas.splice(index, 1); 
  }

}