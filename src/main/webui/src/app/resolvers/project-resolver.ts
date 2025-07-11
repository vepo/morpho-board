import { inject } from '@angular/core';
import { RedirectCommand, ResolveFn, Router } from '@angular/router';
import { Project, ProjectsService } from '../services/projects.service';

export const projectResolver: ResolveFn<Project> = (route, state) => {
  const projectId = route.paramMap.get('projectId');
  console.log('ProjectResolver: Resolving project for ID:', projectId);
  if (!projectId) {
    return new RedirectCommand(inject(Router).parseUrl('/'));
  }
  return inject(ProjectsService).findById(Number(projectId));
};