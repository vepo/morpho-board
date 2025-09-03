import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationComponent } from './notification.component';
import { NotificationService } from '../../services/notification.service';
import { EMPTY } from 'rxjs';

describe('NotificationComponent', () => {
  let component: NotificationComponent;
  let fixture: ComponentFixture<NotificationComponent>;
  let notificationService: jasmine.SpyObj<NotificationService>;
  beforeEach(async () => {
    notificationService = jasmine.createSpyObj('NotificationService', ['connect', 'listen']);
    notificationService.listen.and.returnValue(EMPTY);
    await TestBed.configureTestingModule({
      imports: [NotificationComponent],
      providers: [{
        provide: NotificationService, useValue: notificationService
      }]
    })
      .compileComponents();

    fixture = TestBed.createComponent(NotificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
