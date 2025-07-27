import { TestBed } from '@angular/core/testing';

import { HttpTestingController } from '@angular/common/http/testing';
import { TicketService } from './ticket.service';
import { HttpClient } from '@angular/common/http';

describe('TicketService', () => {
  let service: TicketService;
  let httpClientSpy: jasmine.SpyObj<HttpClient>;
  beforeEach(() => {
      httpClientSpy = jasmine.createSpyObj('HttpClient', ['get']);
      service = new TicketService(httpClientSpy);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
