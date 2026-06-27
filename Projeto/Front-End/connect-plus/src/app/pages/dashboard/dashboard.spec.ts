import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { Menu } from './dashboard';
import { DashboardService } from './dashboard.service';

describe('Menu', () => {
  let component: Menu;
  let fixture: ComponentFixture<Menu>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Menu],
      providers: [
        {
          provide: DashboardService,
          useValue: {
            buscarResumo: () => of({}),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Menu);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
