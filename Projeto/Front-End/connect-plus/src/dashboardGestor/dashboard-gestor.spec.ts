import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardGestor } from './dashboard-gestor';

describe('DashboardGestor', () => {
  let component: DashboardGestor;
  let fixture: ComponentFixture<DashboardGestor>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardGestor]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DashboardGestor);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
