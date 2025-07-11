import { TestBed } from '@angular/core/testing';
import { ResolveFn } from '@angular/router';

import { stagesResolver } from './stages-resolver';

describe('stagesResolver', () => {
  const executeResolver: ResolveFn<boolean> = (...resolverParameters) => 
      TestBed.runInInjectionContext(() => stagesResolver(...resolverParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeResolver).toBeTruthy();
  });
});
