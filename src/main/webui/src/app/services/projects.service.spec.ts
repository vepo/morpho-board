import { TestBed } from '@angular/core/testing';

import { ProjectsService } from './projects.service';
import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';

describe('ProjectsService', () => {
  let service: ProjectsService;
  let httpClientSpy: jasmine.SpyObj<HttpClient>;
  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['get']);
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpClient, useValue: httpClientSpy }
      ]
    });
    TestBed.runInInjectionContext(() => {
      service = inject(ProjectsService);
    });
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
