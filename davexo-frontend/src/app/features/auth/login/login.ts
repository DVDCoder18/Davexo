import { Component, inject, signal } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { AuthenticationService } from '../../../api/api/authentication.service';
import { LoginRequestDto } from '../../../api/model/loginRequestDto';
import { Auth } from '../../../core/services/auth';
import { Router } from '@angular/router';

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-login',
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {

  private readonly authenticationService = inject(AuthenticationService);
  private readonly authService = inject(Auth);
  private readonly router = inject(Router);
  protected readonly errorMessage = signal("");

  loginForm = new FormGroup({
    email: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.email
      ]
    }),

    password: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required
      ]
    })
  });

  onSubmit() {
    this.errorMessage.set("");
    if (this.loginForm.valid) {
      const loginRequest: LoginRequestDto = {
        email: this.loginForm.controls.email.value,
        password: this.loginForm.controls.password.value
      };

      this.authenticationService.login(loginRequest).subscribe({
        next: (response) => {
          this.authService.registerToken(response.token);
          this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          if (error.status === 401) {
            this.errorMessage.set("Invalid email or password");
          }
          else {
            this.errorMessage.set("Something went wrong. Please try again");
          }
        }
      });
    }
  }
}
