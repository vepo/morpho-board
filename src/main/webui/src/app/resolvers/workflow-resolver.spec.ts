import { TestBed } from '@angular/core/testing';
import { ResolveFn } from '@angular/router';

import { workflowResolver } from './workflow-resolver';
import { ProjectWorkflow } from '../services/projects.service';

describe('workflowResolver', () => {
  const executeResolver: ResolveFn<ProjectWorkflow> = (...resolverParameters) => 
      TestBed.runInInjectionContext(() => workflowResolver(...resolverParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeResolver).toBeTruthy();
  });
});
