import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

type CompanyControlName = 'corporateName' | 'tradeName' | 'cnpj';
type SubmitState = 'idle' | 'success' | 'error';

type RegistrationStep = {
  readonly title: string;
  readonly detail: string;
  readonly complete: boolean;
};

type SummaryBadge = {
  readonly label: string;
  readonly value: string;
};

type HelperNote = {
  readonly title: string;
  readonly detail: string;
};

const EMPTY_COMPANY_FORM = {
  corporateName: '',
  tradeName: '',
  cnpj: '',
};

const CNPJ_PATTERN = /^\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2}$/;
const CNPJ_MAX_DIGITS = 14;

@Component({
  selector: 'app-cadastro-empresa',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cadastro-empresa.html',
  styleUrl: './cadastro-empresa-page.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CadastroEmpresa {
  private readonly fb = inject(FormBuilder);

  readonly totalFields = Object.keys(EMPTY_COMPANY_FORM).length;
  readonly helperNotes: readonly HelperNote[] = [
    {
      title: 'Razao social',
      detail: 'Use o nome registrado no contrato social ou no cartao CNPJ.',
    },
    {
      title: 'Nome fantasia',
      detail: 'Esse nome pode ser exibido para usuarios e parceiros na plataforma.',
    },
    {
      title: 'CNPJ valido',
      detail: 'O campo aceita mascara automatica no formato 00.000.000/0000-00.',
    },
  ];

  readonly companyForm = this.fb.nonNullable.group({
    corporateName: ['', [Validators.required, Validators.minLength(3)]],
    tradeName: ['', [Validators.required, Validators.minLength(2)]],
    cnpj: ['', [Validators.required, Validators.pattern(CNPJ_PATTERN)]],
  });

  submitState: SubmitState = 'idle';

  get controls() {
    return this.companyForm.controls;
  }

  get filledFieldsCount(): number {
    return Object.values(this.companyForm.getRawValue()).filter((value) => value.trim().length > 0).length;
  }

  get completionPercent(): number {
    return Math.round((this.filledFieldsCount / this.totalFields) * 100);
  }

  get cnpjDigitsCount(): number {
    return this.extractDigits(this.controls.cnpj.value).length;
  }

  get registrationSteps(): readonly RegistrationStep[] {
    return [
      {
        title: 'Identificacao juridica',
        detail: 'Razao social preenchida corretamente.',
        complete: this.controls.corporateName.valid,
      },
      {
        title: 'Marca da empresa',
        detail: 'Nome fantasia pronto para exibicao.',
        complete: this.controls.tradeName.valid,
      },
      {
        title: 'Documento fiscal',
        detail: 'CNPJ com mascara completa e valido no formulario.',
        complete: this.controls.cnpj.valid,
      },
    ];
  }

  get summaryBadges(): readonly SummaryBadge[] {
    return [
      {
        label: 'Campos preenchidos',
        value: `${this.filledFieldsCount}/${this.totalFields}`,
      },
      {
        label: 'Digitos do CNPJ',
        value: `${this.cnpjDigitsCount}/14`,
      },
      {
        label: 'Status atual',
        value: this.statusLabel,
      },
    ];
  }

  get companyPreviewName(): string {
    return this.controls.corporateName.value || 'Sua empresa aparecera aqui';
  }

  get tradeNamePreview(): string {
    return this.controls.tradeName.value || 'Nome fantasia para exibicao';
  }

  get cnpjPreview(): string {
    return this.controls.cnpj.value || '00.000.000/0000-00';
  }

  get statusLabel(): string {
    if (this.submitState === 'success') {
      return 'Validado';
    }

    if (this.companyForm.valid) {
      return 'Pronto para envio';
    }

    return 'Em preenchimento';
  }

  onFieldInteraction(): void {
    if (this.submitState !== 'idle') {
      this.submitState = 'idle';
    }
  }

  onCnpjInput(): void {
    const formattedValue = this.formatCnpj(this.controls.cnpj.value);

    if (formattedValue !== this.controls.cnpj.value) {
      this.controls.cnpj.setValue(formattedValue, { emitEvent: false });
    }

    this.onFieldInteraction();
  }

  onSubmit(): void {
    this.onCnpjInput();

    if (this.companyForm.invalid) {
      this.submitState = 'error';
      this.companyForm.markAllAsTouched();
      return;
    }

    this.submitState = 'success';
    this.companyForm.markAsPristine();
    console.log('Cadastro empresarial pronto para envio:', this.companyForm.getRawValue());
  }

  clearForm(): void {
    this.companyForm.reset(EMPTY_COMPANY_FORM);
    this.submitState = 'idle';
  }

  shouldShowError(controlName: CompanyControlName): boolean {
    const control = this.controls[controlName];
    return control.invalid && (control.touched || control.dirty || this.submitState === 'error');
  }

  errorMessage(controlName: CompanyControlName): string {
    const control = this.controls[controlName];

    if (control.hasError('required')) {
      return `${this.getFieldLabel(controlName)} e obrigatorio.`;
    }

    if (control.hasError('minlength')) {
      return `${this.getFieldLabel(controlName)} precisa ter mais caracteres.`;
    }

    if (control.hasError('pattern')) {
      return 'Informe o CNPJ no formato 00.000.000/0000-00.';
    }

    return 'Confira este campo antes de continuar.';
  }

  private getFieldLabel(controlName: CompanyControlName): string {
    const fieldLabels: Record<CompanyControlName, string> = {
      corporateName: 'Razao social',
      tradeName: 'Nome fantasia',
      cnpj: 'CNPJ',
    };

    return fieldLabels[controlName];
  }

  private formatCnpj(value: string): string {
    const digits = this.extractDigits(value).slice(0, CNPJ_MAX_DIGITS);
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

  private extractDigits(value: string): string {
    return value.replace(/\D/g, '');
  }
}
