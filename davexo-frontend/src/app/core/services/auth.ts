import { inject, Service } from '@angular/core';
import { Router } from '@angular/router';

@Service()
export class Auth {

  private readonly tokenKey = 'davexo_token';
  private readonly router = inject(Router);

  public registerToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  public getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  public deleteToken(): void {
    localStorage.removeItem(this.tokenKey);
  }

  public isAuthenticated(): boolean {
    if (this.getToken() === null) {
      return false;
    }
    return true;
  }

  public logout(): void {
    this.deleteToken();
    this.router.navigate(['/login']);
  }
}
