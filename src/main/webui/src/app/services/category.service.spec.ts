import { TestBed } from '@angular/core/testing';

import { CategoryService } from './category.service';
import { HttpClient } from '@angular/common/http';

describe('CategoryService', () => {
  let httpClientSpy: jasmine.SpyObj<HttpClient>;
  let service: CategoryService;

  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['get']);
    service = new CategoryService(httpClientSpy);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
