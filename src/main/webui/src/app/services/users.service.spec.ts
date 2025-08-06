import { TestBed } from '@angular/core/testing';
import { UsersService } from './users.service';
import { HttpClient } from '@angular/common/http';

describe('UsersService', () => {
  let service: UsersService;
  let httpMock: jasmine.SpyObj<HttpClient>;

  beforeEach(() => {

    httpMock = jasmine.createSpyObj('HttpClient', ['get']);
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpClient, useValue: httpMock }
      ]
    });
    service = TestBed.inject(UsersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
