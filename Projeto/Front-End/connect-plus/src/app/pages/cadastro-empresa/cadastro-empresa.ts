import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { EmpresaService, Empresa, Usuario } from './service_empresa';
import { Router, RouterLink } from '@angular/router';

export enum Status {
  Ativa = 'ativa',
  Inativa = 'inativa'
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
  estados = [
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

  cadastroForm = new FormGroup({
    idEmpresa: new FormControl<number | null>(null),
    razaoSocial: new FormControl('', Validators.required),
    nomeFantasia: new FormControl(''),
    cnpj: new FormControl('', [Validators.required, Validators.maxLength(14),Validators.minLength(14)]),
    cidade: new FormControl(''),
    status: new FormControl(Status.Ativa),
    uf: new FormControl(''),

    nomeAdmin: new FormControl('', Validators.required),
    emailAdmin: new FormControl('', [Validators.required, Validators.email]),
    senhaAdmin: new FormControl('', [Validators.required, Validators.minLength(8)]),
    confirmarSenhaAdmin: new FormControl(''),
  });

  constructor(
    private empresaService: EmpresaService,
    private router: Router,
  ) {}

  ngOnInit(): void {
  }

  onSubmit(): void {
    if (this.cadastroForm.invalid) {
      alert('Preencha todos os campos corretamente.');
      return;
    }

    const { senhaAdmin, confirmarSenhaAdmin } = this.cadastroForm.value;
    if (senhaAdmin !== confirmarSenhaAdmin) {
      alert('As senhas não coincidem.');
      return;
    }

    const dadosEmpresa = {
      razaoSocial: this.cadastroForm.value.razaoSocial,
      nomeFantasia: this.cadastroForm.value.nomeFantasia,
      cnpj: this.cadastroForm.value.cnpj,
      cidade: this.cadastroForm.value.cidade,
      status: this.cadastroForm.value.status,
      uf: this.cadastroForm.value.uf,
    } as Empresa;

    this.empresaService.salvar(dadosEmpresa).subscribe({
      next: () => {
        const dadosAdmin: Usuario = {
          nome: this.cadastroForm.value.nomeAdmin!,
          email: this.cadastroForm.value.emailAdmin!,
          senha: this.cadastroForm.value.senhaAdmin!,
          status: 'Ativo',
        };

        this.empresaService.cadastrarAdmin(dadosAdmin).subscribe({
          next: () => {
            alert('Cadastro realizado com sucesso! ✅');
            this.router.navigate(['/login']);
          },
          error: () => alert('Erro ao criar administrador.'),
        });
      },
      error: () => alert('Erro ao salvar empresa.'),
    });
  }
}
