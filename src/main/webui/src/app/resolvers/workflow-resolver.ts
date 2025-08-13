import { inject } from '@angular/core';
import { RedirectCommand, ResolveFn, Router } from '@angular/router';
import { Workflow, WorkflowService } from '../services/workflow.service';

export const workflowResolver: ResolveFn<Workflow> = (route, state) => {
  const workflowId = route.paramMap.get('workflowId');
  console.log('workflowResolver: Resolving Workflow with id:', workflowId);
  if (!workflowId) {
    return new RedirectCommand(inject(Router).parseUrl('/'));
  }
  return inject(WorkflowService).findById(Number(workflowId));
};

export const workflowsResolver: ResolveFn<Workflow[]> = (route, state) => {
  return inject(WorkflowService).findAll();
};
