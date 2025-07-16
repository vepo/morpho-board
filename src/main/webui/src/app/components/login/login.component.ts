import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  template: `
    <div class="login-container">
      <h2>Entrar</h2>
      <form (ngSubmit)="login()">
        <!--input [(ngModel)]="email" name="email" placeholder="E-mail" required /-->
        <!--input [(ngModel)]="password" name="password" type="password" placeholder="Senha" required /-->
        <button type="submit">Entrar</button>
        <div *ngIf="error" class="error">{{error}}</div>
      </form>
    </div>
  `,
  styles: [`
    .login-container { max-width: 350px; margin: 60px auto; background: #fff; border-radius: 16px; box-shadow: 0 4px 16px #0001; padding: 32px; }
    h2 { color: #4f8cff; text-align: center; margin-bottom: 18px; }
    input { width: 100%; margin-bottom: 14px; padding: 10px; border-radius: 6px; border: 1.5px solid #a259ff; }
    button { width: 100%; background: linear-gradient(90deg, #4f8cff 0%, #a259ff 100%); color: #fff; border: none; border-radius: 6px; padding: 10px; font-weight: 600; }
    .error { color: #d32f2f; margin-top: 10px; text-align: center; }
  `]
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';

  constructor(private auth: AuthService, private router: Router) {}

  login() {
    this.auth.login(this.email, this.password).subscribe({
      next: () => this.router.navigate(['/']),
      error: () => this.error = 'E-mail ou senha inválidos'
    });
  }
} 