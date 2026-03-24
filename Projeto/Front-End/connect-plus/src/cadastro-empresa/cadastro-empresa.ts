import { HttpClient } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

type ConsultaCnpjResponse = {
  readonly cnpj: string;
  readonly razao_social: string;
  readonly nome_fantasia?: string | null;
  readonly descricao_situacao_cadastral: string | null;
  readonly municipio?: string | null;
  readonly uf?: string | null;
};

type RegisteredCompany = {
  readonly corporateName: string;
  readonly tradeName: string;
  readonly cnpj: string;
  readonly status: string;
  readonly city: string;
  readonly uf: string;
};

const EMPTY_COMPANY_FORM = {
  cnpj: '',
};

@Component({
  selector: 'app-cadastro-empresa',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cadastro-empresa.html',
  styleUrl: './cadastro-empresa.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})

export class CadastroEmpresa {
  //aqui confere se cnpj é valido no governo
  private readonly fb = inject(FormBuilder);
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'https://brasilapi.com.br/api/cnpj/v1';
  private lastFetchedCnpj = '';

  readonly companyForm = this.fb.nonNullable.group({
    cnpj: ['', [Validators.required]],
  });

  //mensagem de erros
  readonly loading = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');
  readonly consultationResult = signal<ConsultaCnpjResponse | null>(null);
  readonly registeredCompanies = signal<readonly RegisteredCompany[]>([]);

  get controls() {
    return this.companyForm.controls;
  }

  get totalCompanies(): number {
    return this.registeredCompanies().length;
  }

  get lastConsultedStatus(): string {
    return this.consultationResult()?.descricao_situacao_cadastral ?? 'Nenhuma consulta feita';
  }

  onCnpjInput(): void {
  // classes para formatar o cnpj no formato visual
    const formattedValue = this.formatCnpj(this.controls.cnpj.value);
    const normalizedCnpj = this.normalizeCnpj(formattedValue);

    if (formattedValue !== this.controls.cnpj.value) {
      this.controls.cnpj.setValue(formattedValue, { emitEvent: false });
    }

    if (!normalizedCnpj) {
      this.consultationResult.set(null);
      this.lastFetchedCnpj = '';
      this.clearMessages();
      return;
    }

    if (
      this.consultationResult() &&
      this.normalizeCnpj(this.consultationResult()!.cnpj) !== normalizedCnpj
    ) {
      this.consultationResult.set(null);
    }

    this.clearMessages();

    if (normalizedCnpj.length < 14) {
      return;
    }

    if (!this.validarCnpj(normalizedCnpj)) {
      this.errorMessage.set('CNPJ invalido.');
      return;
    }

    if (!this.loading() && this.lastFetchedCnpj !== normalizedCnpj) {
      this.fetchCompanyData(normalizedCnpj);
    }
  }

  saveCompany(): void {
    this.clearMessages();
    this.onCnpjInput();

    if (this.companyForm.invalid) {
      this.companyForm.markAllAsTouched();
      this.errorMessage.set('Informe um CNPJ antes de salvar.');
      return;
    }

    const normalizedCnpj = this.normalizeCnpj(this.controls.cnpj.value);

    if (!this.validarCnpj(normalizedCnpj)) {
      this.errorMessage.set('CNPJ invalido.');
      return;
    }

    if (this.registeredCompanies().some((company) => this.normalizeCnpj(company.cnpj) === normalizedCnpj)) {
      this.errorMessage.set('Esse CNPJ ja foi salvo no vetor local.');
      return;
    }

    const currentResult = this.consultationResult();
    const currentResultCnpj = currentResult ? this.normalizeCnpj(currentResult.cnpj) : '';

    if (currentResult && currentResultCnpj === normalizedCnpj) {
      this.finishSave(currentResult);
      return;
    }

    this.fetchCompanyData(normalizedCnpj, true);
  }

  clearForm(): void {
    this.companyForm.reset(EMPTY_COMPANY_FORM);
    this.consultationResult.set(null);
    this.lastFetchedCnpj = '';
    this.clearMessages();
  }

  shouldShowError(): boolean {
    const control = this.controls.cnpj;
    return control.invalid && (control.touched || control.dirty);
  }

  private finishSave(response: ConsultaCnpjResponse): void {
    const company: RegisteredCompany = {
      //depois de verificar se cnpj é valido ele puxar da api as informaçoes cadastrada no sistema do governo
      corporateName: response.razao_social,
      tradeName: response.nome_fantasia || 'Sem nome fantasia',
      cnpj: this.formatCnpj(response.cnpj || this.controls.cnpj.value),
      status: response.descricao_situacao_cadastral || 'Sem informacao',
      city: response.municipio || 'Nao informado',
      uf: response.uf || 'Nao informado',
    };

    this.registeredCompanies.update((companies) => [company, ...companies]);
    this.successMessage.set('Empresa validada e salva no vetor local.');
    this.controls.cnpj.setValue(this.formatCnpj(response.cnpj));
  }

  private clearMessages(): void {
    this.errorMessage.set('');
    this.successMessage.set('');
  }

  private fetchCompanyData(normalizedCnpj: string, saveAfterFetch = false): void {
    this.loading.set(true);
    this.lastFetchedCnpj = normalizedCnpj;

    this.http.get<ConsultaCnpjResponse>(`${this.apiUrl}/${normalizedCnpj}`).subscribe({
      next: (response) => {
        this.loading.set(false);

        if (this.normalizeCnpj(this.controls.cnpj.value) !== normalizedCnpj) {
          return;
        }
        //onde puxar os dados da api
        this.consultationResult.set(response);
        this.controls.cnpj.setValue(this.formatCnpj(response.cnpj), { emitEvent: false });
        this.successMessage.set('Dados carregados automaticamente pelo CNPJ.');

        if (saveAfterFetch) {
          this.finishSave(response);
        }
      },
      error: () => {
        this.loading.set(false);

        if (this.normalizeCnpj(this.controls.cnpj.value) !== normalizedCnpj) {
          return;
        }

        this.lastFetchedCnpj = '';
        this.errorMessage.set('Nao foi possivel consultar o CNPJ agora.');
      },
    });
  }

    //validação antes de mandar para api
  private validarCnpj(cnpj: string): boolean {
    const normalizedCnpj = this.normalizeCnpj(cnpj);

    if (normalizedCnpj.length !== 14 || /^(\d)\1{13}$/.test(normalizedCnpj)) {
      return false;
    }

    const base = normalizedCnpj.slice(0, 12);
    const firstDigit = this.calculateDigit(base);
    const secondDigit = this.calculateDigit(`${base}${firstDigit}`);

    return normalizedCnpj === `${base}${firstDigit}${secondDigit}`;
  }

  private normalizeCnpj(cnpj: string): string {
    return cnpj.replace(/\D/g, '');
  }

//converto para ficar no padrão
  private formatCnpj(cnpj: string): string {
    const digits = this.normalizeCnpj(cnpj).slice(0, 14);
    const firstBlock = digits.slice(0, 2);
    const secondBlock = digits.slice(2, 5);
    const thirdBlock = digits.slice(5, 8);
    const fourthBlock = digits.slice(8, 12);
    const fifthBlock = digits.slice(12, 14);

    let formattedValue = firstBlock;

    if (secondBlock) {
      formattedValue += `.${secondBlock}`;
    }

    if (thirdBlock) {
      formattedValue += `.${thirdBlock}`;
    }

    if (fourthBlock) {
      formattedValue += `/${fourthBlock}`;
    }

    if (fifthBlock) {
      formattedValue += `-${fifthBlock}`;
    }

    return formattedValue;
  }

  //proprio nome ja diz com é feito o calculo 
  private calculateDigit(base: string): number {
    let sum = 0;
    let weight = base.length - 7;

    for (const digit of base) {
      sum += Number(digit) * weight;
      weight -= 1;

      if (weight < 2) {
        weight = 9;
      }
    }

    const remainder = sum % 11;
    return remainder < 2 ? 0 : 11 - remainder;
  }
}
