/*
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-cadastro-empresa',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cadastro-empresa.html',
  styleUrls: ['./cadastro-empresa.css']
})
export class AppComponent {
  myForm: FormGroup;

  constructor(private fb: FormBuilder) {
    // Inicialización del formulario con validaciones
    this.myForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      edad: [null, [Validators.required, Validators.min(18)]]
    });
  }

  // Método para enviar el formulario
  onSubmit() {
    if (this.myForm.valid) {
      console.log('Datos enviados:', this.myForm.value);
      alert('Formulario enviado con éxito');
      this.myForm.reset();
    } else {
      this.myForm.markAllAsTouched(); // Marca todos los campos para mostrar errores
    }
  }

  // Método para acceder fácilmente a los controles en la plantilla
  get f() {
    return this.myForm.controls;
  }
}
*/

export { CadastroEmpresa as AppComponent } from './cadastro-empresa-page';
