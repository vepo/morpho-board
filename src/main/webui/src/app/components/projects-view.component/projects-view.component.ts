import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Project } from '../../services/projects.service';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-projects-view.component',
  imports: [RouterLink, MatIconModule, MatButtonModule],
  templateUrl: './projects-view.component.html'
})
export class ProjectsViewComponent implements OnInit {
  private readonly activatedRoute = inject(ActivatedRoute);

  projects: Project[] = [];
  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ projects }) => this.projects = projects);
  }
}
