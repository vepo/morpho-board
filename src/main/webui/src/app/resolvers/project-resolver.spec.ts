import { TestBed } from '@angular/core/testing';
import { ResolveFn } from '@angular/router';

import { projectResolver } from './project-resolver';
import { Project } from '../services/projects.service';

describe('projectResolver', () => {
  const executeResolver: ResolveFn<Project> = (...resolverParameters) => 
      TestBed.runInInjectionContext(() => projectResolver(...resolverParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeResolver).toBeTruthy();
  });
});
