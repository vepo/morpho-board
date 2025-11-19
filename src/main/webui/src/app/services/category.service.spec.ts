import { TestBed } from '@angular/core/testing';

import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { CategoryService } from './category.service';

describe('CategoryService', () => {
  let httpClientSpy: jasmine.SpyObj<HttpClient>;
  let service: CategoryService;

  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['get']);
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpClient, useValue: httpClientSpy }
      ]
    });
    TestBed.runInInjectionContext(() => {
      service = inject(CategoryService);
    });      
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
