import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthSessionService, PapelEmpresa } from './auth-session.service';

export const roleGuard: CanActivateFn = (route) => {
  const authSessionService = inject(AuthSessionService);
  const router = inject(Router);
  const papeis = (route.data?.['papeis'] ?? []) as PapelEmpresa[];

  if (papeis.length === 0 || authSessionService.temAlgumPapel(papeis)) {
    return true;
  }

  return router.createUrlTree(['/dashboard'], {
    queryParams: { acessoNegado: '1' },
  });
};
