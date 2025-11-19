import { TestBed } from '@angular/core/testing';

import { HttpClient } from '@angular/common/http';
import { StatusService } from './status.service';
import { inject } from '@angular/core';

describe('StatusService', () => {
  let service: StatusService;
  let httpClientSpy: jasmine.SpyObj<HttpClient>;
  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['get']);
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpClient, useValue: httpClientSpy }
      ]
    });
    TestBed.runInInjectionContext(() => {
      service = inject(StatusService);
    });
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
