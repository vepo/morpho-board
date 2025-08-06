import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  imports: [FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule]
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';
  hide = signal(true);

  constructor(private readonly auth: AuthService, 
              private readonly router: Router) {}

  login() {
    this.auth.login(this.email, this.password).subscribe({
      next: async () => await this.router.navigate(['/']),
      error: () => this.error = 'E-mail ou senha inválidos'
    });
  }

  clickEvent(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.stopPropagation();
  }
} 