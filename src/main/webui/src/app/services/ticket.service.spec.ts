import { TestBed } from '@angular/core/testing';

import { HttpClient } from '@angular/common/http';
import { TicketService } from './ticket.service';
import { inject } from '@angular/core';

describe('TicketService', () => {
  let service: TicketService;
  let httpClientSpy: jasmine.SpyObj<HttpClient>;
  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['get']);

    TestBed.configureTestingModule({
      providers: [
        { provide: HttpClient, useValue: httpClientSpy }
      ]
    });
    TestBed.runInInjectionContext(() => {
      service = inject(TicketService);
    });
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
