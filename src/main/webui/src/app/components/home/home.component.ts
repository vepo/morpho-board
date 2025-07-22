import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProjectsService, Project } from '../../services/projects.service';
import { FormsModule } from '@angular/forms';
import { TrimPipe } from '../pipes/trim.pipe';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  imports: [CommonModule, RouterModule, FormsModule, TrimPipe, MatIconModule, MatButtonModule],
  standalone: true
})
export class HomeComponent implements OnInit {
  projects: Project[] = [];

  constructor(private router: Router, private projectsService: ProjectsService, private authService: AuthService) {}

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

  navigateToKanban(projectId: number) {
    this.router.navigate(['/kanban', projectId]);
  }
} 