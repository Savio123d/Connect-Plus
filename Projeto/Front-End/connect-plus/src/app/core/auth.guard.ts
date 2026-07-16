import { inject } from '@angular/core';
import { CanActivateChildFn, CanActivateFn, Router } from '@angular/router';
import { AuthSessionService } from './auth-session.service';

const validarSessao = (url: string): boolean | ReturnType<Router['createUrlTree']> => {
  const authSessionService = inject(AuthSessionService);
  const router = inject(Router);

  return authSessionService.estaAutenticado()
    ? true
    : router.createUrlTree(['/login'], {
        queryParams: { returnUrl: url },
      });
};

export const authGuard: CanActivateFn = (_route, state) => validarSessao(state.url);

export const authChildGuard: CanActivateChildFn = (_route, state) => validarSessao(state.url);
