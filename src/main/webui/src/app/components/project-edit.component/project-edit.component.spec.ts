import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectEditComponent } from './project-edit.component';
import { of } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { ProjectsService } from '../../services/projects.service';

describe('ProjectEditComponent', () => {
  let component: ProjectEditComponent;
  let fixture: ComponentFixture<ProjectEditComponent>;
  let mockActivatedRoute: any;
  let projectsService: jasmine.SpyObj<ProjectsService>;

  beforeEach(async () => {
    mockActivatedRoute = {
      data: of({
        project: { id: 1, name: 'Test Project', description: 'A test project', prefix: 'TP', workflow: 1 },
        workflows: [{ id: 1, name: 'Default Workflow' }]
      })
    };
    projectsService = jasmine.createSpyObj('ProjectsService', ['create', 'update']);
    await TestBed.configureTestingModule({
      imports: [ProjectEditComponent],
      providers: [
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: ProjectsService, useValue: projectsService }
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProjectEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
