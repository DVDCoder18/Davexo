import { inject } from '@angular/core';
import {
  HttpErrorResponse,
  HttpInterceptorFn
} from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Auth } from '../services/auth';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const authService = inject(Auth);
  const router = inject(Router);

  const token = authService.getToken();
  const apiUrl = environment.apiUrl;

  const isPublicAuthRequest =
    req.url.includes('/api/auth/login') ||
    req.url.includes('/api/auth/sign-up');

  const isProtectedApiRequest =
    req.url.startsWith(apiUrl) &&
    !isPublicAuthRequest;

  let requestToSend = req;

  if (token && isProtectedApiRequest) {
    requestToSend = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(requestToSend).pipe(
    catchError((error: HttpErrorResponse) => {

      if (error.status === 401 && isProtectedApiRequest) {
        authService.deleteToken();
        router.navigate(['/login']);
      }

      return throwError(() => error);
    })
  );
};
