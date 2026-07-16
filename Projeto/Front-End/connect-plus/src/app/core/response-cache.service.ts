import { HttpEvent, HttpHandlerFn, HttpRequest, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, finalize, of, shareReplay, tap } from 'rxjs';

interface CachedResponse {
  readonly expiresAt: number;
  readonly response: HttpResponse<unknown>;
}

interface PendingRequest {
  readonly id: symbol;
  readonly response$: Observable<HttpEvent<unknown>>;
}

@Injectable({
  providedIn: 'root',
})
export class ResponseCacheService {
  private readonly responses = new Map<string, CachedResponse>();
  private readonly pendingRequests = new Map<string, PendingRequest>();
  private readonly maxEntries = 100;
  private generation = 0;

  getOrLoad(
    request: HttpRequest<unknown>,
    next: HttpHandlerFn,
    scope: string,
    ttlMs: number,
  ): Observable<HttpEvent<unknown>> {
    const key = scope + '|' + request.urlWithParams;
    const now = Date.now();
    const cached = this.responses.get(key);

    if (cached && cached.expiresAt > now) {
      return of(this.cloneResponse(cached.response));
    }

    if (cached) {
      this.responses.delete(key);
    }

    const pending = this.pendingRequests.get(key);
    if (pending) {
      return pending.response$;
    }

    const requestGeneration = this.generation;
    const requestId = Symbol(key);
    const response$ = next(request).pipe(
      tap((event) => {
        if (event instanceof HttpResponse && requestGeneration === this.generation) {
          this.store(key, event, ttlMs);
        }
      }),
      finalize(() => {
        if (this.pendingRequests.get(key)?.id === requestId) {
          this.pendingRequests.delete(key);
        }
      }),
      shareReplay({ bufferSize: 1, refCount: false }),
    );

    this.pendingRequests.set(key, { id: requestId, response$ });
    return response$;
  }

  invalidateAll(): void {
    this.generation += 1;
    this.responses.clear();
    this.pendingRequests.clear();
  }

  private store(key: string, response: HttpResponse<unknown>, ttlMs: number): void {
    const now = Date.now();
    this.removeExpired(now);

    while (this.responses.size >= this.maxEntries) {
      const oldestKey = this.responses.keys().next().value as string | undefined;

      if (!oldestKey) {
        break;
      }

      this.responses.delete(oldestKey);
    }

    this.responses.set(key, {
      expiresAt: now + ttlMs,
      response: this.cloneResponse(response),
    });
  }

  private removeExpired(now: number): void {
    for (const [key, cached] of this.responses) {
      if (cached.expiresAt <= now) {
        this.responses.delete(key);
      }
    }
  }

  private cloneResponse(response: HttpResponse<unknown>): HttpResponse<unknown> {
    return response.clone({ body: this.cloneBody(response.body) });
  }

  private cloneBody(body: unknown): unknown {
    if (body === null || body === undefined) {
      return body;
    }

    try {
      return structuredClone(body);
    } catch {
      return body;
    }
  }
}
