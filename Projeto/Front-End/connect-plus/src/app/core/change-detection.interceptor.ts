import { HttpInterceptorFn } from '@angular/common/http';
import { ApplicationRef, inject } from '@angular/core';
import { finalize } from 'rxjs';

/**
 * Ponte para o modo zoneless: agenda uma verificação de mudanças após cada
 * resposta HTTP. Sem isso, componentes que atualizam campos comuns dentro de
 * subscribe() não re-renderizam (ficam presos em "Carregando...").
 */
export const changeDetectionInterceptor: HttpInterceptorFn = (request, next) => {
  const appRef = inject(ApplicationRef);

  return next(request).pipe(
    finalize(() => {
      queueMicrotask(() => {
        try {
          appRef.tick();
        } catch {
          // tick já em andamento ou aplicação destruída: a próxima
          // verificação agendada cobre esta atualização.
        }
      });
    }),
  );
};
