import { inject } from '@angular/core';
import { RedirectCommand, ResolveFn, Router } from '@angular/router';
import { ProjectStatus, StatusService } from '../services/status.service';

export const statusResolver: ResolveFn<ProjectStatus[]> = (route, state) => {
  const projectId = route.paramMap.get('projectId');
  console.log('StatusResolver: Resolving statuses for project ID:', projectId);
  if (!projectId) {
    return new RedirectCommand(inject(Router).parseUrl('/'));
  }
  return inject(StatusService).findProjectsStatuses(Number(projectId));
};
