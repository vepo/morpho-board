import { ActivatedRouteSnapshot, MaybeAsync, RedirectCommand, Resolve, ResolveFn, Router, RouterStateSnapshot } from '@angular/router';
import { ProjectStage, StagesService } from '../services/stages.service';
import { inject } from '@angular/core';

export const stagesResolver: ResolveFn<ProjectStage[]> = (route, state) => {
  const projectId = route.paramMap.get('projectId');
  console.log('StagesResolver: Resolving stages for project ID:', projectId);
  if (!projectId) {
    return new RedirectCommand(inject(Router).parseUrl('/'));
  }
  return inject(StagesService).findProjectsStages(Number(projectId));
};
