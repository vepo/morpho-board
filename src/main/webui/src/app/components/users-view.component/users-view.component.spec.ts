import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { UsersService } from '../../services/users.service';
import { UsersViewComponent } from './users-view.component';

describe('UsersViewComponent', () => {
  let component: UsersViewComponent;
  let fixture: ComponentFixture<UsersViewComponent>;
  let usersService: jasmine.SpyObj<UsersService>;
  let mockActivatedRoute: any;

  beforeEach(async () => {
    usersService = jasmine.createSpyObj('UsersService', ['search']);
    mockActivatedRoute = {
          data: of({
            users: []
          })
        };
    await TestBed.configureTestingModule({
      imports: [UsersViewComponent],
      providers: [
        { provide: UsersService, useValue: usersService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(UsersViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
