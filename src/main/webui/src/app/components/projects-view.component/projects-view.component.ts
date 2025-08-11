import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Project } from '../../services/projects.service';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-projects-view.component',
  imports: [RouterLink, MatIconModule, MatButtonModule],
  templateUrl: './projects-view.component.html',
  styleUrl: './projects-view.component.scss'
})
export class ProjectsViewComponent implements OnInit {
  projects: Project[] = [];
  constructor(private readonly activatedRoute: ActivatedRoute) { }
  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ projects }) => this.projects = projects);
  }
}
