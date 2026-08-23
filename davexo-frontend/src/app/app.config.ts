import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners
} from '@angular/core';

import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { environment } from '../environments/environment';
import { authInterceptor } from './core/interceptors/auth-interceptor';

import { routes } from './app.routes';
import { provideApi } from './api/provide-api';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),

    provideHttpClient(withInterceptors([authInterceptor])),

    provideRouter(routes),

    provideApi({
      basePath: environment.apiUrl
    })
  ]
};
