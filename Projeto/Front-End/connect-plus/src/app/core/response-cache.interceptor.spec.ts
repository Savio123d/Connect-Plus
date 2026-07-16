import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { responseCacheInterceptor } from './response-cache.interceptor';
import { ResponseCacheService } from './response-cache.service';

describe('responseCacheInterceptor', () => {
  let http: HttpClient;
  let httpTesting: HttpTestingController;
  let cache: ResponseCacheService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([responseCacheInterceptor])),
        provideHttpClientTesting(),
      ],
    });

    http = TestBed.inject(HttpClient);
    httpTesting = TestBed.inject(HttpTestingController);
    cache = TestBed.inject(ResponseCacheService);
  });

  afterEach(() => {
    cache.invalidateAll();
    httpTesting.verify();
    sessionStorage.clear();
    localStorage.clear();
  });

  it('reuses a successful GET response within its TTL', () => {
    const url = '/api/dashboard/resumo/7';
    let firstResponse: unknown;
    let secondResponse: unknown;

    http.get(url).subscribe((response) => (firstResponse = response));
    httpTesting.expectOne(url).flush({ usuariosAtivos: 4 });

    http.get(url).subscribe((response) => (secondResponse = response));
    httpTesting.expectNone(url);

    expect(firstResponse).toEqual({ usuariosAtivos: 4 });
    expect(secondResponse).toEqual({ usuariosAtivos: 4 });
    expect(secondResponse).not.toBe(firstResponse);
  });

  it('deduplicates equal GET requests while the first one is pending', () => {
    const url = '/api/projetos?empresaId=7';
    const responses: unknown[] = [];

    http.get(url).subscribe((response) => responses.push(response));
    http.get(url).subscribe((response) => responses.push(response));

    const requests = httpTesting.match(url);
    expect(requests).toHaveLength(1);
    requests[0].flush([{ id: 1, nome: 'Projeto' }]);

    expect(responses).toHaveLength(2);
  });

  it('invalidates cached reads before a mutation', () => {
    const url = '/api/usuarios/empresa/7';

    http.get(url).subscribe();
    httpTesting.expectOne(url).flush([{ idUsuario: 1 }]);

    http.get(url).subscribe();
    httpTesting.expectNone(url);

    http.post('/api/usuarios', { nome: 'Novo' }).subscribe();
    httpTesting.expectOne('/api/usuarios').flush({ idUsuario: 2 });

    http.get(url).subscribe();
    httpTesting.expectOne(url).flush([{ idUsuario: 1 }, { idUsuario: 2 }]);
  });

  it('does not cache chat or notification reads', () => {
    const url = '/api/conversas?idUsuarioEmpresa=3';

    http.get(url).subscribe();
    httpTesting.expectOne(url).flush([]);

    http.get(url).subscribe();
    httpTesting.expectOne(url).flush([]);
  });
});
