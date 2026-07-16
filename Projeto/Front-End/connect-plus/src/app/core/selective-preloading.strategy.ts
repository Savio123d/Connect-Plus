import { Injectable } from '@angular/core';
import { PreloadingStrategy, Route } from '@angular/router';
import { Observable, of } from 'rxjs';
import { AuthSessionService } from './auth-session.service';

@Injectable({
  providedIn: 'root',
})
export class SelectivePreloadingStrategy implements PreloadingStrategy {
  constructor(private readonly authSessionService: AuthSessionService) {}

  preload(route: Route, carregar: () => Observable<unknown>): Observable<unknown> {
    return route.data?.['preload'] === true && this.authSessionService.estaAutenticado()
      ? carregar()
      : of(null);
  }
}
