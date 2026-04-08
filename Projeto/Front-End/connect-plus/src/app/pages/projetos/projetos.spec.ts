import { ComponentFixture, TestBed } from '@angular/core/testing';
import { projetos } from './projetos';

describe('Projetos', () => {
  let component: projetos;
  let fixture: ComponentFixture<projetos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [projetos]
    }).compileComponents();

    fixture = TestBed.createComponent(projetos);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
