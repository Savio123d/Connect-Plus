import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthSessionService } from './auth-session.service';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const authSessionService = inject(AuthSessionService);
  const router = inject(Router);
  const token = authSessionService.obterToken();

  const requestAutenticada = token
    ? request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      })
    : request;

  return next(requestAutenticada).pipe(
    catchError((erro: HttpErrorResponse) => {
      const rotaPublicaDeAuth = request.url.includes('/api/auth/');

      if (erro.status === 401 && !rotaPublicaDeAuth) {
        authSessionService.limparSessao();
        void router.navigate(['/login'], {
          queryParams: { sessaoExpirada: '1' },
        });
      } else if (erro.status === 403 && !rotaPublicaDeAuth) {
        void router.navigate(['/dashboard'], {
          queryParams: { acessoNegado: '1' },
        });
      }

      return throwError(() => erro);
    }),
  );
};
