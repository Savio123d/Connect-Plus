import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { DashboardUsuario } from './dashboard-usuario';

describe('DashboardUsuario', () => {
  let component: DashboardUsuario;
  let fixture: ComponentFixture<DashboardUsuario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardUsuario],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardUsuario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
