import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Project, ProjectsService } from '../../services/projects.service';
import { TrimPipe } from '../pipes/trim.pipe';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  imports: [CommonModule, RouterModule, FormsModule, TrimPipe, MatIconModule, MatButtonModule],
  standalone: true
})
export class HomeComponent implements OnInit {
  private readonly projectsService = inject(ProjectsService);
  private readonly authService = inject(AuthService);

  projects: Project[] = [];

  ngOnInit() {
    this.projectsService.findAll().subscribe({
      next: (projects) => {
        this.projects = projects;
      }
    });
  }

  isAuthenticated(): boolean {
    return this.authService.isLoggedIn();
  }
} 