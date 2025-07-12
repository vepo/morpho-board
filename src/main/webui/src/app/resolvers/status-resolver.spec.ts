import { TestBed } from '@angular/core/testing';
import { ResolveFn } from '@angular/router';

import { statusResolver } from './status-resolver';
import { ProjectStatus } from '../services/status.service';

describe('statusResolver', () => {
  const executeResolver: ResolveFn<ProjectStatus[]> = (...resolverParameters) => 
      TestBed.runInInjectionContext(() => statusResolver(...resolverParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeResolver).toBeTruthy();
  });
});
