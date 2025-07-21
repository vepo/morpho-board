import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';

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

  constructor(private auth: AuthService, private router: Router) {}

  login() {
    this.auth.login(this.email, this.password).subscribe({
      next: () => this.router.navigate(['/']),
      error: () => this.error = 'E-mail ou senha inválidos'
    });
  }

  clickEvent(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.stopPropagation();
  }
} 