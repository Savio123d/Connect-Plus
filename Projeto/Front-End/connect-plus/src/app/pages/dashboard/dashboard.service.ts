import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { DashboardResumo } from './models/dashboard.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private readonly apiUrl = `${environment.apiBase}/api/dashboard`;

  constructor(private http: HttpClient) {}

  buscarResumo(empresaId: number): Observable<DashboardResumo> {
    return this.http.get<DashboardResumo>(`${this.apiUrl}/resumo/${empresaId}`);
  }
}
