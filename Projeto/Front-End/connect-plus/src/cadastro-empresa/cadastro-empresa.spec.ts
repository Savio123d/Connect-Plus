import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CadastroEmpresa } from './cadastro-empresa';

describe('CadastroEmpresa', () => {
  let component: CadastroEmpresa;
  let fixture: ComponentFixture<CadastroEmpresa>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CadastroEmpresa],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(CadastroEmpresa);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
