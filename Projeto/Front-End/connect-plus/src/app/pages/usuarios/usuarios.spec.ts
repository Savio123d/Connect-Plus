import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { AuthSessionService } from '../../core/auth-session.service';
import { UsuarioService } from './usuario.service';

import { Usuarios } from './usuarios';

describe('Usuarios', () => {
  let component: Usuarios;
  let fixture: ComponentFixture<Usuarios>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Usuarios],
      providers: [
        {
          provide: AuthSessionService,
          useValue: { temAlgumPapel: () => true },
        },
        {
          provide: UsuarioService,
          useValue: { listar: () => of([]) },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Usuarios);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
