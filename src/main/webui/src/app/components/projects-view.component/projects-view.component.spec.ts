import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectsViewComponent } from './projects-view.component';
import { of } from 'rxjs';
import { ActivatedRoute } from '@angular/router';

describe('ProjectsViewComponent', () => {
  let component: ProjectsViewComponent;
  let fixture: ComponentFixture<ProjectsViewComponent>;
  let mockActivatedRoute: any;

  beforeEach(async () => {
    mockActivatedRoute = {
      data: of({
        projects: []
      })
    };
    await TestBed.configureTestingModule({
      imports: [ProjectsViewComponent],
      providers: [
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectsViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
