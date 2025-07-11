import { TestBed } from '@angular/core/testing';
import { ResolveFn } from '@angular/router';

import { ticketsResolver } from './tickets-resolver';

describe('ticketsResolver', () => {
  const executeResolver: ResolveFn<boolean> = (...resolverParameters) => 
      TestBed.runInInjectionContext(() => ticketsResolver(...resolverParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeResolver).toBeTruthy();
  });
});
