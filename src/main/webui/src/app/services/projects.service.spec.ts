import { TestBed } from '@angular/core/testing';

import { ProjectsService } from './projects.service';
import { HttpClient } from '@angular/common/http';

describe('ProjectsService', () => {
  let service: ProjectsService;
  let httpClientSpy: jasmine.SpyObj<HttpClient>;
  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['get']);
    service = new ProjectsService(httpClientSpy);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
