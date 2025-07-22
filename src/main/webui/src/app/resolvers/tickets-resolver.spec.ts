import { TestBed } from '@angular/core/testing';
import { ResolveFn } from '@angular/router';

import { ticketsResolver } from './tickets-resolver';
import { Ticket } from '../services/ticket.service';

describe('ticketsResolver', () => {
  const executeResolver: ResolveFn<Ticket[]> = (...resolverParameters) => 
      TestBed.runInInjectionContext(() => ticketsResolver(...resolverParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeResolver).toBeTruthy();
  });
});
