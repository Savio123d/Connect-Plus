import {
  provideHttpClient,
  withInterceptors,
} from '@angular/common/http';
import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZonelessChangeDetection,
} from '@angular/core';
import { provideRouter, withPreloading } from '@angular/router';
import { routes } from './app.routes';
import { authInterceptor } from './core/auth.interceptor';
import { changeDetectionInterceptor } from './core/change-detection.interceptor';
import { SelectivePreloadingStrategy } from './core/selective-preloading.strategy';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(
      routes,
      withPreloading(SelectivePreloadingStrategy),
    ),
    provideHttpClient(withInterceptors([authInterceptor, changeDetectionInterceptor])),
  ],
};
