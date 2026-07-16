import { HttpContextToken, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthSessionService } from './auth-session.service';
import { ResponseCacheService } from './response-cache.service';

interface CachePolicy {
  readonly matches: RegExp;
  readonly ttlMs: number;
}

const CACHE_POLICIES: readonly CachePolicy[] = [
  { matches: /\/api\/dashboard\/resumo\/\d+\/?$/, ttlMs: 30_000 },
  { matches: /\/api\/perfil\/\d+\/?$/, ttlMs: 60_000 },
  { matches: /\/api\/empresas\/\d+\/?$/, ttlMs: 60_000 },
  { matches: /\/api\/usuarios\/empresa\/\d+\/?$/, ttlMs: 45_000 },
  {
    matches: /\/api\/projetos(?:\/\d+|\/usuarios-disponiveis)?\/?$/,
    ttlMs: 30_000,
  },
  { matches: /\/api\/tarefas\/empresa\/\d+\/?$/, ttlMs: 15_000 },
  { matches: /\/api\/lojas(?:\/\d+)?\/?$/, ttlMs: 15_000 },
  { matches: /\/api\/saldos-xp\/me\/?$/, ttlMs: 15_000 },
  {
    matches:
      /\/api\/feedbacks\/(?:empresa\/\d+(?:\/resumo|\/360\/pendentes)?|360\/(?:status|usuario\/cards|gestor))\/?$/,
    ttlMs: 15_000,
  },
];

const MUTATING_METHODS = new Set(['POST', 'PUT', 'PATCH', 'DELETE']);

export const SKIP_RESPONSE_CACHE = new HttpContextToken<boolean>(() => false);

export const responseCacheInterceptor: HttpInterceptorFn = (request, next) => {
  const cache = inject(ResponseCacheService);

  if (MUTATING_METHODS.has(request.method.toUpperCase())) {
    cache.invalidateAll();
    return next(request);
  }

  const ttlMs = cacheTtl(request);
  if (ttlMs === null) {
    return next(request);
  }

  const authSession = inject(AuthSessionService);
  const user = authSession.obterUsuario();
  const scope = [
    user?.idEmpresa ?? 0,
    user?.idUsuarioEmpresa ?? 0,
    user?.idUsuario ?? 0,
    authSession.obterPapel() ?? 'anonymous',
  ].join(':');

  return cache.getOrLoad(request, next, scope, ttlMs);
};

function cacheTtl(request: HttpRequest<unknown>): number | null {
  if (
    request.method.toUpperCase() !== 'GET' ||
    request.responseType !== 'json' ||
    request.context.get(SKIP_RESPONSE_CACHE)
  ) {
    return null;
  }

  const urlPath = request.url.replace(/[?#].*$/, '');

  return CACHE_POLICIES.find((policy) => policy.matches.test(urlPath))?.ttlMs ?? null;
}
