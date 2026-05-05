import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
<<<<<<< HEAD
  providers: [provideRouter(routes)],
};

=======
  providers: [
    provideRouter(routes),
    provideHttpClient()
  ],
};
>>>>>>> 25faf32da0a8a3ae8f25b0584a40c695afbcc8b8
