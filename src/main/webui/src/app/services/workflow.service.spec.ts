import { TestBed } from '@angular/core/testing';

import { WorkflowService } from './workflow.service';
import { HttpClient } from '@angular/common/http';

describe('WorkflowService', () => {
  let service: WorkflowService;
  let httpMock: jasmine.SpyObj<any>;

  beforeEach(() => {
    httpMock = jasmine.createSpyObj('HttpClient', ['get', 'post', 'put', 'delete']);
    TestBed.configureTestingModule({
      providers: [
        WorkflowService,
        { provide: HttpClient, useValue: httpMock }
      ]
    }).compileComponents();
    service = TestBed.inject(WorkflowService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
