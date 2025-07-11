import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProjectsService, Project } from '../../services/projects.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  imports: [CommonModule, RouterModule, FormsModule],
  standalone: true
})
export class HomeComponent implements OnInit {
  projects: Project[] = [];
  searchTerm: string = '';

  constructor(private router: Router, private projectsService: ProjectsService) {}

  ngOnInit() {
    this.projectsService.findAll().subscribe({
      next: (projects) => {
        this.projects = projects;
      }
    });
  }

  navigateToKanban(projectId: string) {
    this.router.navigate(['/kanban', projectId]);
  }

  onSearchKeydown(event: KeyboardEvent) {
    if (event.key === 'Enter' && this.searchTerm.trim()) {
      this.router.navigate(['/search'], { queryParams: { q: this.searchTerm.trim() } });
    }
  }
} 